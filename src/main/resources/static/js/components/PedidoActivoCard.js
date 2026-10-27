/**
 * PedidoActivoCard - Representa UN pedido dentro de una mesa ocupada.
 * Se muestra en el modal de "Ver pedidos" (una mesa puede tener N).
 */

import { formatCurrency, getRelativeTime } from '../utils/formatters.js';
import { CONFIG_ESTADO_PEDIDO, EstadoPedido } from '../state/domain.js';

export const renderPedidoActivo = (pedido) => {
    const config = CONFIG_ESTADO_PEDIDO[pedido.estado];

    const itemsHTML = pedido.items.map(it => `
        <li>
            <span class="qty">${it.cantidad}</span>
            <div class="item-desc">
                <strong>${it.nombre}</strong>
                <small>${it.categoria}</small>
            </div>
            <span class="item-subtotal">${formatCurrency(it.precio * it.cantidad)}</span>
        </li>
    `).join('');

    // Acciones según estado (regla de dominio)
    const acciones = [];
    if (pedido.estado === EstadoPedido.ENTREGADO) {
        acciones.push(`<button class="btn-primary btn-sm" data-action="abrir-pago" data-pedido-id="${pedido.id}">
            <i class="ph ph-currency-dollar"></i> Cobrar
        </button>`);
    }
    if (pedido.estado !== EstadoPedido.PAGADO && pedido.estado !== EstadoPedido.CANCELADO) {
        acciones.push(`<button class="btn-secondary btn-sm" data-action="cancelar-pedido" data-pedido-id="${pedido.id}">
            Cancelar
        </button>`);
    }

    return `
        <div class="pedido-activo-card" data-pedido-id="${pedido.id}">
            <div class="pedido-header">
                <div>
                    <strong>Orden #${pedido.id}</strong>
                    <small>${getRelativeTime(pedido.fecha)}</small>
                </div>
                <span class="badge ${config.badgeClass}">${config.label}</span>
            </div>

            <ul class="pedido-items">${itemsHTML}</ul>

            <div class="pedido-footer">
                <span class="pedido-total">${formatCurrency(pedido.total)}</span>
                <div class="pedido-actions">${acciones.join('')}</div>
            </div>
        </div>
    `;
};
