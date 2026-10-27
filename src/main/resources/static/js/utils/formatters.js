/**
 * Formatea un monto numérico como moneda peruana
 * @param {number} amount 
 * @returns {string} ej. "S/25.00"
 */
export const formatCurrency = (amount) => {
    const value = Number.isFinite(amount) ? amount : 0;
    return `S/${value.toFixed(2)}`;
};

/**
 * Obtiene la hora actual en formato HH:MM
 * @returns {string} ej. "15:52"
 */
export const getCurrentTime = () => {
    const now = new Date();
    const hh = now.getHours().toString().padStart(2, '0');
    const mm = now.getMinutes().toString().padStart(2, '0');
    return `${hh}:${mm}`;
};

/**
 * Calcula los minutos transcurridos desde una fecha dada
 * @param {Date|string} from 
 * @returns {string} ej. "Hace 25 min"
 */
export const getRelativeTime = (from) => {
    const start = new Date(from).getTime();
    const diffMin = Math.floor((Date.now() - start) / 60000);
    if (diffMin < 1) return 'Recién ordenado';
    if (diffMin < 60) return `Hace ${diffMin} min`;
    const hours = Math.floor(diffMin / 60);
    return `Hace ${hours}h ${diffMin % 60}min`;
};
