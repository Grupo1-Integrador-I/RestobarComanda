/**
 * pedidoService - Reglas de negocio del ciclo de vida de un Pedido.
 * 
 */

import { getState, setState } from '../state/store.js';
import { EstadoPedido, TipoPago, esPedidoActivo } from '../state/domain.js';
import {
    iniciarPedidoReal,
    agregarProductoReal,
    enviarPedidoReal,
    patchPedidoEstado
} from './httpClient.js';

// ============================================
// CREAR / ENVIAR PEDIDO
// ============================================

/**
 * Envía la comanda activa al backend y la registra en el store.
 * Un pedido nace en CREADO y pasa a ENVIADO al confirmarse.
 */

let envioEnProceso = false;

export const enviarComanda = async () => {
    if (envioEnProceso) {
        throw new Error('Ya se está enviando una comanda.');
    }

    const { comandaActiva, usuario } = getState();
    const { mesaId, items } = comandaActiva;

    if (!mesaId) {
        throw new Error('Selecciona una mesa.');
    }

    if (!items.length) {
        throw new Error('Agrega al menos un producto.');
    }

    for (const item of items) {
        if (!Number.isSafeInteger(item.productoId) ||
            !Number.isSafeInteger(item.cantidad) ||
            item.cantidad <= 0) {
            throw new Error('Hay productos o cantidades inválidas.');
        }
    }

    envioEnProceso = true;
    let pedidoId = null;

    try {
        // 1. Crear pedido en PostgreSQL
        pedidoId = await iniciarPedidoReal(mesaId);

        // 2. Registrar los productos
        for (const item of items) {
            await agregarProductoReal(
                pedidoId,
                item.productoId,
                item.cantidad
            );
        }

        // 3. Enviar el pedido a cocina y barra
        await enviarPedidoReal(pedidoId);

        // 4. Construir el pedido para mostrarlo
        const total = items.reduce(
            (suma, item) => suma + item.precio * item.cantidad,
            0
        );

        const nuevoPedido = {
            id: pedidoId,
            mesaId,
            meseroId: usuario.id,
            meseroNombre: usuario.nombre,
            fecha: new Date(),
            estado: EstadoPedido.ENVIADO,
            items: items.map(item => ({ ...item })),
            total,
            pago: null
        };

        // 5. Actualizar la interfaz
        setState(prev => ({
            pedidos: [...prev.pedidos, nuevoPedido],
            comandaActiva: {
                mesaId: null,
                pedidoId: null,
                items: []
            }
        }));

        return nuevoPedido;

    } catch (error) {
        console.error('Error al registrar comanda:', error);

        if (pedidoId !== null) {
            throw new Error(
                `El pedido #${pedidoId} pudo quedar registrado ` +
                'parcialmente en PostgreSQL. ' +
                'No vuelvas a enviarlo hasta verificar su estado. ' +
                `Detalle: ${error.message}`
            );
        }

        throw error;

    } finally {
        envioEnProceso = false;
    }
};



// ============================================
// TRANSICIONES DE ESTADO (Preparador)
// ============================================

/**
 * Avanza el estado de un pedido.
 *
 * Flujo:
 * ENVIADO → EN_PREPARACION → LISTO → ENTREGADO
 *
 * @param {number} pedidoId - ID del pedido.
 * @param {string} nuevoEstado - Nuevo estado del pedido.
 * @returns {Promise<void>}
 */
export const avanzarEstadoPedido = async (pedidoId, nuevoEstado) => {
    await patchPedidoEstado(pedidoId, nuevoEstado);

    setState(prev => ({
        pedidos: prev.pedidos.map(p =>
            p.id === pedidoId ? { ...p, estado: nuevoEstado } : p
        )
    }));
};

// ============================================
// CANCELAR PEDIDO
// ============================================

export const cancelarPedido = async (pedidoId) => {
    const pedido = getState().pedidos.find(p => p.id === pedidoId);
    if (!pedido) throw new Error('Pedido no encontrado.');
    if (pedido.estado === EstadoPedido.PAGADO)
        throw new Error('No se puede cancelar un pedido ya pagado.');

    await patchPedidoEstado(pedidoId, EstadoPedido.CANCELADO);

    setState(prev => ({
        pedidos: prev.pedidos.map(p =>
            p.id === pedidoId ? { ...p, estado: EstadoPedido.CANCELADO } : p
        )
    }));
};

// ============================================
// HELPERS de lectura (derivados)
// ============================================

/** Pedidos activos (no pagados ni cancelados) de una mesa */
export const obtenerPedidosActivosDeMesa = (mesaId) =>
    getState().pedidos.filter(p => p.mesaId === mesaId && esPedidoActivo(p));

/** Pedidos pendientes en preparación (cocina o barra) */
export const obtenerPedidosEnPreparacion = () =>
    getState().pedidos.filter(p =>
        p.estado === EstadoPedido.ENVIADO ||
        p.estado === EstadoPedido.EN_PREPARACION
    );
