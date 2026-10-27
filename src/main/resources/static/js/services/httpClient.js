/**
 * httpClient - Abstracción de comunicación con el backend.
 */

const SIMULATED_DELAY = 300;
const delay = (ms) => new Promise(resolve => setTimeout(resolve, ms));

// ============================================================
// PEDIDOS
// ============================================================

/**
 * Crea un pedido nuevo en el backend.
 */
export const postPedido = async (pedido) => {
    await delay(SIMULATED_DELAY);
    console.log('[httpClient] POST /api/pedidos →', pedido);
    return { success: true };
};

/**
 * Cambia el estado de un pedido.
 */
export const patchPedidoEstado = async (pedidoId, nuevoEstado) => {
    await delay(SIMULATED_DELAY);
    console.log(`[httpClient] PATCH /api/pedidos/${pedidoId} → estado = ${nuevoEstado}`);
    return { success: true };
};

// ============================================================
// PAGOS
// ============================================================

/**
 * Registra un pago en el backend.
 */
export const postPago = async (pago) => {
    await delay(SIMULATED_DELAY);
    console.log('[httpClient] POST /api/pagos →', pago);
    return { success: true };
};

// ============================================================
// MESAS
// ============================================================

/**
 * Consulta todas las mesas.
 */

/**
 * Consulta las mesas reales registradas en PostgreSQL.
 */
export const getMesas = async () => {

    const respuesta = await fetch('/api/mesas', {
        method: 'GET',
        credentials: 'same-origin',
        headers: {
            'Accept': 'application/json'
        }
    });

    if (!respuesta.ok) {
        throw new Error(
            'Error al consultar mesas: HTTP ' + respuesta.status
        );
    }

    return await respuesta.json();
};


// ============================================================
// LEGACY (deprecado, puedes borrarlo)
// ============================================================

export const patchTicketListo = async (ordenId) => {
    await delay(SIMULATED_DELAY);
    console.log(`[httpClient] PATCH /api/cocina/tickets/${ordenId}/listo (legacy)`);
    return { success: true };
    
};
/**
 * Comprueba la comunicación real con Spring Boot.
 */
export const probarConexionBackend = async () => {
    const respuesta = await fetch('/api/pedidos/prueba');

    if (!respuesta.ok) {
        throw new Error(
            `Error al conectar con Spring Boot: ${respuesta.status}`
        );
    }

    return await respuesta.text();
};


export const getPedidosActivosPorMesa = async (mesaId) => {
    const respuesta = await fetch(
        `/api/pedidos/mesa/${mesaId}/activos`,
        {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json'
            }
        }
    );

    if (!respuesta.ok) {
        throw new Error(
            `Error al consultar pedidos de la mesa ${mesaId}: HTTP ${respuesta.status}`
        );
    }

    return await respuesta.json();
};

/**
 * Inicia un pedido real en PostgreSQL.
 * Devuelve el ID generado por el backend.
 */
export const iniciarPedidoReal = async (mesaId) => {
    const respuesta = await fetch(
        `/api/pedidos/iniciar?mesaId=${encodeURIComponent(mesaId)}`,
        {
            method: 'POST',
            credentials: 'same-origin'
        }
    );

    if (!respuesta.ok) {
        throw new Error(
            `Error al iniciar pedido: HTTP ${respuesta.status}`
        );
    }

    return await respuesta.json();
};

/**
 * Agrega un producto a un pedido existente.
 */
export const agregarProductoReal = async (
    pedidoId,
    productoId,
    cantidad
) => {
    const parametros = new URLSearchParams({
        productoId: String(productoId),
        cantidad: String(cantidad)
    });

    const respuesta = await fetch(
        `/api/pedidos/${pedidoId}/items?${parametros.toString()}`,
        {
            method: 'POST',
            credentials: 'same-origin'
        }
    );

    if (!respuesta.ok) {
        throw new Error(
            `Error al agregar producto: HTTP ${respuesta.status}`
        );
    }

    return await respuesta.text();
};

/**
 * Envía el pedido a cocina y barra.
 */
export const enviarPedidoReal = async (pedidoId) => {
    const respuesta = await fetch(
        `/api/pedidos/${pedidoId}/enviar`,
        {
            method: 'POST',
            credentials: 'same-origin'
        }
    );

    if (!respuesta.ok) {
        throw new Error(
            `Error al enviar pedido: HTTP ${respuesta.status}`
        );
    }

    return await respuesta.text();
};

/**
 * Marca un pedido como ENTREGADO en PostgreSQL.
 * Solo permite entregar pedidos que estén LISTO.
 */
export const entregarPedidoReal = async (pedidoId) => {

    const respuesta = await fetch(
        `/api/pedidos/${pedidoId}/entregar`,
        {
            method: 'POST',
            credentials: 'same-origin'
        }
    );

    if (!respuesta.ok) {
        throw new Error(
            `Error al entregar pedido #${pedidoId}: HTTP ${respuesta.status}`
        );
    }

    return await respuesta.text();
};

/**
 * Consulta los productos disponibles desde PostgreSQL.
 */
export const getProductosDisponibles = async () => {

    const respuesta = await fetch(
        '/api/productos/disponibles',
        {
            method: 'GET',
            credentials: 'same-origin',
            headers: {
                'Accept': 'application/json'
            }
        }
    );

    if (!respuesta.ok) {
        throw new Error(
            'Error al consultar productos: HTTP ' +
            respuesta.status
        );
    }

    const productos = await respuesta.json();

    return productos.map(producto => ({
        id: producto.id,
        nombre: producto.nombre,
        precio: Number(producto.precio.valor),
        categoria: producto.categoria
    }));
};
