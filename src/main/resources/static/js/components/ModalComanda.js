/**
 * ModalComanda - Componente del modal para armar una nueva comanda.
 * El manejo de eventos (abrir/cerrar/agregar) vive en main.js o en un
 * controlador específico.
 */

import { formatCurrency, getCurrentTime } from '../utils/formatters.js';

/** Renderiza un item de la comanda activa */
export const renderItemComanda = (item) => `
    <div class="order-item-row">
        <div class="order-item-info">
            <div class="qty">${item.cantidad}</div>
            <div>
                <strong style="display:block; font-size:15px;">${item.nombre}</strong>
                ${item.nota ? `<small style="color:var(--text-muted); font-size:12px;">${item.nota}</small>` : ''}
            </div>
        </div>
        <div class="order-item-price">${formatCurrency(item.precio * item.cantidad)}</div>
    </div>
`;

/** Renderiza toda la lista de items */
export const renderListaItems = (items) => {
    if (items.length === 0) return '';
    return items.map(renderItemComanda).join('');
};

/** Calcula total a partir de items */
export const calcularTotal = (items) =>
    items.reduce((sum, it) => sum + it.precio * it.cantidad, 0);

/** Actualiza los textos del header del modal */
export const getHeaderInfo = (mesaId) => ({
    titulo: `Comanda Mesa ${mesaId}`,
    hora: getCurrentTime()
});
