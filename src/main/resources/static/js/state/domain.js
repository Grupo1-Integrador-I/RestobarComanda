/**
 * domain.js - Fuente única de verdad para enums y mapeos del dominio.
 * 
 */
// ============================================
// ENUMS (espejo del backend)
// ============================================

export const Rol = Object.freeze({
    MESERO: 'MESERO',
    PREPARADOR: 'PREPARADOR',
    ADMINISTRADOR: 'ADMINISTRADOR'
});

export const EstadoPedido = Object.freeze({
    CREADO: 'CREADO',
    ENVIADO: 'ENVIADO',
    EN_PREPARACION: 'EN_PREPARACION',
    LISTO: 'LISTO',
    ENTREGADO: 'ENTREGADO',
    PAGADO: 'PAGADO',
    CANCELADO: 'CANCELADO'
});

export const TipoPago = Object.freeze({
    EFECTIVO: 'EFECTIVO',
    TARJETA: 'TARJETA'
});

export const CategoriaProducto = Object.freeze({
    COCINA: 'COCINA',
    BARRA: 'BARRA'
});

// ============================================
// MAPEO EstadoPedido → Configuración visual
// ============================================

export const CONFIG_ESTADO_PEDIDO = Object.freeze({
    CREADO: {
        label: 'Borrador',
        badgeClass: 'badge-draft',
        color: 'var(--text-muted)',
        esActivo: true
    },
    ENVIADO: {
        label: 'Enviado',
        badgeClass: 'badge-warning',
        color: 'var(--amber)',
        esActivo: true
    },
    EN_PREPARACION: {
        label: 'En preparación',
        badgeClass: 'badge-warning',
        color: 'var(--amber)',
        esActivo: true
    },
    LISTO: {
        label: 'Listo para servir',
        badgeClass: 'badge-info',
        color: '#3b82f6',
        esActivo: true
    },
    ENTREGADO: {
        label: 'Entregado',
        badgeClass: 'badge-success',
        color: 'var(--green)',
        esActivo: true
    },
    PAGADO: {
        label: 'Pagado',
        badgeClass: 'badge-paid',
        color: 'var(--green)',
        esActivo: false
    },
    CANCELADO: {
        label: 'Cancelado',
        badgeClass: 'badge-danger',
        color: 'var(--danger)',
        esActivo: false
    }
});

// ============================================
// HELPERS de dominio (reglas puras)
// ============================================

/** Un pedido está activo si NO está pagado ni cancelado */
export const esPedidoActivo = (pedido) =>
    pedido.estado !== EstadoPedido.PAGADO &&
    pedido.estado !== EstadoPedido.CANCELADO;

/** Una mesa está OCUPADA si tiene al menos un pedido activo */
export const calcularEstadoMesa = (mesa, pedidos) => {
    const activos = pedidos.filter(p => p.mesaId === mesa.id && esPedidoActivo(p));
    return activos.length > 0 ? 'OCUPADO' : 'LIBRE';
};

/** Total de todos los pedidos activos de una mesa */
export const calcularTotalMesa = (mesaId, pedidos) =>
    pedidos
        .filter(p => p.mesaId === mesaId && esPedidoActivo(p))
        .reduce((sum, p) => sum + p.total, 0);

/** Cuenta pedidos activos de una mesa */
export const contarPedidosActivos = (mesaId, pedidos) =>
    pedidos.filter(p => p.mesaId === mesaId && esPedidoActivo(p)).length;

/** El pedido puede enviarse si tiene al menos 1 ítem */
export const puedeEnviarPedido = (pedido) =>
    pedido.items && pedido.items.length > 0;

/** El pedido puede pagarse si está ENTREGADO */
export const puedePagarPedido = (pedido) =>
    pedido.estado === EstadoPedido.ENTREGADO;

/** El pedido puede cancelarse si NO está PAGADO */
export const puedeCancelarPedido = (pedido) =>
    pedido.estado !== EstadoPedido.PAGADO;
