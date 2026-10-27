/**
 * SelectorRol - Permite alternar entre roles para probar la UI.
 * En producción real esto vendría del login; aquí es un mock.
 */

import { Rol } from '../state/domain.js';

export const renderSelectorRol = (rolActual) => {
    const opciones = [
        { valor: Rol.MESERO,        label: 'Mesero',        icono: 'ph-user' },
        { valor: Rol.PREPARADOR,    label: 'Preparador',    icono: 'ph-cooking-pot' },
        { valor: Rol.ADMINISTRADOR, label: 'Administrador', icono: 'ph-shield-star' }
    ];

    return `
        <div class="selector-rol">
            <p class="selector-label">Cambiar rol (demo)</p>
            <div class="selector-opciones">
                ${opciones.map(o => `
                    <button class="selector-btn ${o.valor === rolActual ? 'active' : ''}"
                            data-action="cambiar-rol"
                            data-rol="${o.valor}">
                        <i class="ph ${o.icono}"></i>
                        <span>${o.label}</span>
                    </button>
                `).join('')}
            </div>
        </div>
    `;
};

/** Devuelve las vistas permitidas por rol */
export const vistasPermitidas = (rol) => {
    switch (rol) {
        case Rol.MESERO:        return ['mesas', 'comandas'];
        case Rol.PREPARADOR:    return ['preparacion'];
        case Rol.ADMINISTRADOR: return ['mesas', 'comandas', 'preparacion', 'admin'];
        default:                return ['mesas'];
    }
};
