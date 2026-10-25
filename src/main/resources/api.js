/**
 * api.js — Comunicación con el backend (REST + WebSocket/STOMP).
 * No usa librerías externas: el sistema debe funcionar sin internet (LAN).
 */
const Api = {
  /** Llamada REST genérica. Lanza Error con el mensaje del backend si falla. */
  async pedir(metodo, url, cuerpo) {
    const r = await fetch(url, {
      method: metodo,
      headers: { 'Content-Type': 'application/json' },
      body: cuerpo ? JSON.stringify(cuerpo) : undefined,
      credentials: 'same-origin'
    });
    let datos = null;
    try { datos = await r.json(); } catch (e) { /* respuesta sin cuerpo */ }
    if (!r.ok) {
      if (r.status === 401 && !url.startsWith('/api/auth/')) {
        window.dispatchEvent(new Event('sesion-expirada'));
      }
      const err = new Error((datos && datos.mensaje) || 'Error ' + r.status);
      err.status = r.status;
      throw err;
    }
    return datos;
  },
  get(url) { return this.pedir('GET', url); },
  post(url, cuerpo) { return this.pedir('POST', url, cuerpo); }
};

/**
 * Cliente STOMP mínimo sobre WebSocket nativo (endpoint /ws del backend).
 * Se suscribe a los tópicos indicados y reintenta la conexión cada 3 s.
 * Devuelve una función para cerrar la conexión.
 */
function conectarStomp(temas, alMensaje, alEstado) {
  let ws = null;
  let cerrado = false;

  const abrir = () => {
    const esquema = location.protocol === 'https:' ? 'wss' : 'ws';
    ws = new WebSocket(esquema + '://' + location.host + '/ws');

    ws.onopen = () => ws.send('CONNECT\naccept-version:1.2\nheart-beat:0,0\n\n\0');

    ws.onmessage = (e) => {
      const trama = String(e.data);
      if (trama.startsWith('CONNECTED')) {
        temas.forEach((t, i) => ws.send('SUBSCRIBE\nid:sub-' + i + '\ndestination:' + t + '\n\n\0'));
        alEstado(true);
      } else if (trama.startsWith('MESSAGE')) {
        const cuerpo = trama.slice(trama.indexOf('\n\n') + 2).replace(/\0$/, '');
        try { alMensaje(JSON.parse(cuerpo)); } catch (_) { alMensaje(null); }
      }
    };

    ws.onclose = () => {
      alEstado(false);
      if (!cerrado) setTimeout(abrir, 3000);
    };
  };

  abrir();
  return () => { cerrado = true; if (ws) ws.close(); };
}
