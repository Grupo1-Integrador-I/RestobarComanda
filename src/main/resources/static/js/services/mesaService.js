import { getState, setState } from '../state/store.js';
import { postPedido } from './httpClient.js';
import { getCurrentTime } from '../utils/formatters.js';

/**
 * Envía la comanda activa a cocina.
 * Flujo completo: validar → persistir → actualizar estado → notificar.
 */
export const enviarComandaACocina = async () => {
    const { comandaActiva, siguienteOrdenId } = getState();
    const { mesaId, items } = comandaActiva;

    // 1. Validación de negocio
    if (!mesaId) throw new Error('No hay mesa seleccionada.');
    if (items.length === 0) throw new Error('Agrega al menos un producto.');

    // 2. Persistir en backend
    await postPedido({ mesaId, items });

    // 3. Calcular total
    const total = items.reduce((sum, it) => sum + it.precio * it.cantidad, 0);
    const ordenId = siguienteOrdenId;

    // 4. Actualizar estado (una sola transacción lógica)
    setState(prev => {
        const nuevasMesas = prev.mesas.map(m =>
            m.id === mesaId
                ? { ...m, estado: 'en_cocina', ordenId, total, createdAt: new Date() }
                : m
        );

        const nuevaComanda = {
            ordenId, mesaId,
            hora: getCurrentTime(),
            estado: 'en_cocina',
            total
        };

        const nuevoTicket = {
            ordenId, mesaId,
            mozo: 'Rodrigo N.',
            items: items.map(it => ({
                cantidad: it.cantidad,
                nombre: it.nombre,
                nota: it.nota || null
            })),
            createdAt: new Date()
        };

        return {
            mesas: nuevasMesas,
            comandas: [nuevaComanda, ...prev.comandas],
            tickets: [...prev.tickets, nuevoTicket],
            siguienteOrdenId: ordenId + 1,
            comandaActiva: { mesaId: null, items: [] } // reset
        };
    });

    return { ordenId };
};

/**
 * Marca un ticket de cocina como listo → mesa pasa a "comiendo".
 */
export const marcarTicketListo = async (ordenId) => {
    const { patchTicketListo } = await import('./httpClient.js');
    await patchTicketListo(ordenId);

    setState(prev => {
        const ticket = prev.tickets.find(t => t.ordenId === ordenId);
        if (!ticket) return prev;

        return {
            mesas: prev.mesas.map(m =>
                m.id === ticket.mesaId ? { ...m, estado: 'comiendo' } : m
            ),
            comandas: prev.comandas.map(c =>
                c.ordenId === ordenId ? { ...c, estado: 'comiendo' } : c
            ),
            tickets: prev.tickets.filter(t => t.ordenId !== ordenId)
        };
    });
};
