/**
 * TicketPreparacion - Ticket para el Preparador (cubre Cocina Y Barra).
 * 
 * Nota: Un mismo pedido puede contener items de COCINA y BARRA.
 * El backend decide cómo separar los tickets; aquí mostramos el área
 * dominante o "MIXTO" si tiene items de ambos.
 */

import { formatCurrency, getRelativeTime } from '../utils/formatters.js';
import { EstadoPedido } from '../state/domain.js';

export const renderTicketPreparacion = (pedido) => {
        const items = Array.isArray(pedido.items) ? pedido.items : [];
    // Detectar área dominante
    const categorias = new Set(items.map(it => it.categoria));
    const area = categorias.size === 1 ? [...categorias][0] : 'MIXTO';
    const areaClass = area === 'COCINA' ? 'area-cocina'
                    : area === 'BARRA'  ? 'area-barra'
                    : 'area-mixto';

    const itemsHTML = items.map(it => `
        <li>
            <span class="qty">${it.cantidad}</span>
            <div class="item-desc">
                <strong>${it.nombre}</strong>
                <small>${it.categoria}</small>
            </div>
        </li>
    `).join('');

    // Siguiente estado según el flujo (regla del dominio)
    const siguiente = siguienteEstado(pedido.estado);
    const labelBoton = siguiente === EstadoPedido.EN_PREPARACION
        ? 'Iniciar preparación'
        : siguiente === EstadoPedido.LISTO
            ? '¡Plato Listo!'
            : 'Marcar entregado';

    return `
        <div class="kitchen-ticket ${areaClass}" id="ticket-${pedido.id}">
            <div class="ticket-header">
                <div>
                    <h2>Mesa ${pedido.mesaId}</h2>
                    <p>Orden #${pedido.id} - ${pedido.meseroNombre}</p>
                </div>
                <div class="ticket-area-badge">${area}</div>
            </div>
            <div class="ticket-body">
                <ul class="ticket-items">${itemsHTML}</ul>
            </div>
            <div class="ticket-footer">
                <button class="btn-success"
                        data-action="avanzar-pedido"
                        data-pedido-id="${pedido.id}"
                        data-siguiente-estado="${siguiente}">
                    <i class="ph ph-check-circle"></i> ${labelBoton}
                </button>
            </div>
        </div>
    `;
};

const siguienteEstado = (estadoActual) => {
    switch (estadoActual) {
        case EstadoPedido.ENVIADO:        return EstadoPedido.EN_PREPARACION;
        case EstadoPedido.EN_PREPARACION: return EstadoPedido.LISTO;
        case EstadoPedido.LISTO:          return EstadoPedido.ENTREGADO;
        default:                          return EstadoPedido.ENTREGADO;
    }
};
