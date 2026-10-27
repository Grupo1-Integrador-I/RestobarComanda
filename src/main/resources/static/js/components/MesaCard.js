/**
 * MesaCard - Tarjeta visual de una mesa.
 */

import { formatCurrency, getRelativeTime } from '../utils/formatters.js';
import {
    calcularEstadoMesa,
    calcularTotalMesa,
    contarPedidosActivos,
    CONFIG_ESTADO_PEDIDO
} from '../state/domain.js';

/**
 * @param {Object} mesa
 * @param {Array} pedidos  - todos los pedidos (para derivar estado)
 * @returns {string} HTML
 */

export const renderMesaCard = (mesa, pedidos) => {

    // Priorizar el estado real enviado por PostgreSQL.
    // Si todavía no existe, usar el cálculo anterior.
    const estado = mesa.estado ??
        calcularEstadoMesa(mesa, pedidos);

    if (estado === 'LIBRE') {
        return renderMesaLibre(mesa);
    }

    return renderMesaOcupada(mesa, pedidos);
};


const renderMesaLibre = (mesa) => `
    <div class="table-card free" id="mesa-${mesa.id}" data-mesa-id="${mesa.id}">
        <div class="free-icon"><i class="ph ph-plus"></i></div>
        <h3>Mesa ${mesa.numero}</h3>
        <span class="status-tag available">Libre</span>
        <small class="table-location">${mesa.ubicacion}</small>
    </div>
`;


const renderMesaOcupada = (mesa, pedidos) => {

    const total = calcularTotalMesa(mesa.id, pedidos);
    const cantidad = contarPedidosActivos(mesa.id, pedidos);

    const pedidosActivos = pedidos.filter(p =>
        p.mesaId === mesa.id &&
        p.estado !== 'PAGADO' &&
        p.estado !== 'CANCELADO'
    );

    const hayEnPreparacion = pedidosActivos.some(p =>
        p.estado === 'ENVIADO' ||
        p.estado === 'EN_PREPARACION' ||
        p.estado === 'CREADO'
    );

    const hayListo = pedidosActivos.some(p =>
        p.estado === 'LISTO'
    );

    const hayEntregado = pedidosActivos.some(p =>
        p.estado === 'ENTREGADO'
    );

    let textoEstado;
    let claseEstado;
    let colorBarra;
    let animBarra = '';

    if (hayEnPreparacion) {
        textoEstado = 'En preparación';
        claseEstado = 'waiting';
        colorBarra = 'var(--amber)';
        animBarra = 'animate-pulse-bar';

    } else if (hayListo) {
        textoEstado = 'Listo para entregar';
        claseEstado = 'eating';
        colorBarra = 'var(--green)';

    } else if (hayEntregado) {
        textoEstado = 'Servido';
        claseEstado = 'eating';
        colorBarra = 'var(--green)';

    } else {
        textoEstado = 'Ocupada';
        claseEstado = 'waiting';
        colorBarra = 'var(--amber)';
    }

    const resumenLabel = cantidad === 1
        ? '1 pedido activo'
        : `${cantidad} pedidos activos`;

    return `
        <div class="table-card" id="mesa-${mesa.id}" data-mesa-id="${mesa.id}">
            <div class="status-bar ${animBarra}"
                 style="background-color: ${colorBarra};">
            </div>

            <div class="table-header">
                <div class="table-number">M${mesa.numero}</div>

                <span class="status-tag ${claseEstado}">
                    <span class="dot"></span> ${textoEstado}
                </span>
            </div>

            <div class="table-info">
                <h3>${resumenLabel}</h3>
                <p>
                    <i class="ph ph-map-pin"></i>
                    ${mesa.ubicacion}
                </p>
            </div>

            <div class="table-footer">
                <span class="price">${formatCurrency(total)}</span>
                <span class="action-link">Ver pedidos →</span>
            </div>
        </div>
    `;
};
