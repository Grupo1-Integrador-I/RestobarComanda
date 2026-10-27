/**
 * store.js - Estado global con patrón Observer.
 */

import { Rol, EstadoPedido, CategoriaProducto, TipoPago } from './domain.js';

const initialState = {
    // --- Sesión ---
    usuario: {
        id: 1,
        nombre: 'Rodrigo N.',
        rol: Rol.MESERO
    },

    // --- Navegación ---
    vistaActual: 'mesas',

    // --- Datos maestros ---
    mesas: [],

    // Catálogo de productos (para el modal de comanda)
    productos: [],

    // --- Pedidos (múltiples por mesa) ---
    pedidos: [],
       

    // --- UI: comanda activa (modal) ---
    comandaActiva: {
        mesaId: null,
        pedidoId: null,     // si es edición de un pedido existente
        items: []
    },

    // --- UI: pago activo (modal) ---
    pagoActivo: {
        pedidoId: null
    },

    // --- Filtro del panel de preparación ---
    filtroPreparacion: 'TODOS', // 'TODOS' | 'COCINA' | 'BARRA'

    // --- Contadores ---
    siguientePedidoId: 4031
};

let state = structuredClone(initialState);
const listeners = new Set();

export const getState = () => ({ ...state });

export const setState = (updater) => {
    const partial = typeof updater === 'function' ? updater(state) : updater;
    state = { ...state, ...partial };
    listeners.forEach(l => l(state));
};

export const subscribe = (listener) => {
    listeners.add(listener);
    return () => listeners.delete(listener);
};

export const resetState = () => {
    state = structuredClone(initialState);
    listeners.forEach(l => l(state));
};
