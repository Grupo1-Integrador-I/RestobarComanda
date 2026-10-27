/**
 * ModalPago - Modal para registrar el cierre económico de un pedido.
 * Regla del dominio: monto == total, tipo ∈ {EFECTIVO, TARJETA}.
 */

import { formatCurrency } from '../utils/formatters.js';
import { TipoPago } from '../state/domain.js';

export const renderModalPago = (pedido) => {
    if (!pedido) return '';

    return `
        <div class="modal-header">
            <div>
                <h2>Cobrar Orden #${pedido.id}</h2>
                <p>Mesa ${pedido.mesaId} • ${pedido.meseroNombre}</p>
            </div>
            <button class="btn-close" data-action="cerrar-pago" aria-label="Cerrar">
                <i class="ph ph-x"></i>
            </button>
        </div>

        <div class="modal-body">
            <div class="pago-total-block">
                <span>Total a cobrar</span>
                <strong>${formatCurrency(pedido.total)}</strong>
            </div>

            <div class="pago-tipos">
                <p class="pago-label">Método de pago</p>
                <div class="pago-opciones">
                    <label class="pago-opcion">
                        <input type="radio" name="tipo-pago" value="${TipoPago.EFECTIVO}" checked>
                        <div class="pago-opcion-content">
                            <i class="ph ph-money"></i>
                            <span>Efectivo</span>
                        </div>
                    </label>
                    <label class="pago-opcion">
                        <input type="radio" name="tipo-pago" value="${TipoPago.TARJETA}">
                        <div class="pago-opcion-content">
                            <i class="ph ph-credit-card"></i>
                            <span>Tarjeta</span>
                        </div>
                    </label>
                </div>
            </div>
        </div>

        <div class="modal-footer">
            <div class="modal-actions">
                <button class="btn-secondary" data-action="cerrar-pago">Cancelar</button>
                <button class="btn-primary" data-action="confirmar-pago" data-pedido-id="${pedido.id}">
                    <i class="ph ph-check"></i> Confirmar Pago
                </button>
            </div>
        </div>
    `;
};
