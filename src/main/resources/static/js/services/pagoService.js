/**
 * pagoService - Registro de pagos.
 * Regla del dominio: monto == total del pedido, 1:1 con el pedido.
 */

import { getState, setState } from '../state/store.js';
import { EstadoPedido, TipoPago, puedePagarPedido } from '../state/domain.js';
import { postPago } from './httpClient.js';

export const registrarPago = async (pedidoId, tipoPago) => {
    const pedido = getState().pedidos.find(p => p.id === pedidoId);
    if (!pedido) throw new Error('Pedido no encontrado.');
    if (pedido.pago) throw new Error('Este pedido ya tiene un pago registrado.');
    if (!puedePagarPedido(pedido))
        throw new Error('Solo se pueden pagar pedidos ENTREGADOS.');
    if (!Object.values(TipoPago).includes(tipoPago))
        throw new Error('Tipo de pago inválido.');

    // Regla: monto == total
    const pago = {
        monto: pedido.total,
        tipo: tipoPago,
        fecha: new Date()
    };

    await postPago({ pedidoId, ...pago });

    setState(prev => ({
        pedidos: prev.pedidos.map(p =>
            p.id === pedidoId
                ? { ...p, pago, estado: EstadoPedido.PAGADO }
                : p
        ),
        pagoActivo: { pedidoId: null }
    }));
};
