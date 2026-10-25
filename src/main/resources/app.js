/**
 * app.js — Login y mapa de mesas del mesero.
 * Endpoints usados:
 *   POST /api/auth/login · GET /api/auth/me · POST /api/auth/logout
 *   GET  /api/mesero/mesas/libres · GET /api/mesero/mesas/ocupadas
 *   POST /api/mesero/mesas/{id}/liberar
 *   WebSocket /ws  →  /topic/meseros
 */
const $ = (id) => document.getElementById(id);
let usuario = null;
let cerrarWs = null;
let temporizador = null;

/* ---------- utilidades ---------- */
function el(etiqueta, clase, texto) {
  const e = document.createElement(etiqueta);
  if (clase) e.className = clase;
  if (texto !== undefined) e.textContent = texto;   // textContent evita inyección de HTML
  return e;
}

function avisar(mensaje, esError) {
  const t = $('toast');
  t.textContent = mensaje;
  t.className = 'toast' + (esError ? ' toast-error' : '');
  t.hidden = false;
  clearTimeout(avisar.id);
  avisar.id = setTimeout(() => { t.hidden = true; }, 3500);
}

function mostrar(vista) {
  $('vista-login').hidden = vista !== 'login';
  $('vista-app').hidden = vista !== 'app';
}

/* ---------- sesión ---------- */
async function iniciar() {
  try {
    usuario = await Api.get('/api/auth/me');
    entrarAlSistema();
  } catch (e) {
    mostrar('login');
  }
}

$('form-login').addEventListener('submit', async (ev) => {
  ev.preventDefault();
  const error = $('login-error');
  error.hidden = true;
  const codigo = $('codigo').value.trim();
  const password = $('password').value;
  if (!codigo || !password) {
    error.textContent = 'Escribe tu código y tu contraseña.';
    error.hidden = false;
    return;
  }
  try {
    usuario = await Api.post('/api/auth/login', { codigo, password });
    $('password').value = '';
    entrarAlSistema();
  } catch (e) {
    error.textContent = (e.status === 401 || e.status === 403)
      ? 'Código o contraseña incorrectos.' : e.message;
    error.hidden = false;
  }
});

$('btn-salir').addEventListener('click', async () => {
  try { await Api.post('/api/auth/logout'); } catch (e) { /* ya no hay sesión */ }
  salirDelSistema();
});

window.addEventListener('sesion-expirada', salirDelSistema);

function salirDelSistema() {
  if (cerrarWs) cerrarWs();
  cerrarWs = null;
  clearInterval(temporizador);
  usuario = null;
  mostrar('login');
}

/* ---------- aplicación según el rol ---------- */
function entrarAlSistema() {
  $('usuario-nombre').textContent = usuario.nombre + ' ' + usuario.apellido;
  $('usuario-rol').textContent = usuario.rol.charAt(0) + usuario.rol.slice(1).toLowerCase();
  mostrar('app');

  if (usuario.rol === 'MESERO') {
    armarMenu(['Mesas']);
    abrirMesas();
  } else {
    armarMenu([]);
    $('titulo').textContent = 'Bienvenido, ' + usuario.nombre;
    $('resumen').textContent = '';
    $('contenido').replaceChildren(el('p', 'vacio', 'La pantalla de ' + usuario.rol.toLowerCase() +
      ' se agrega en el siguiente módulo. Por ahora, el acceso del mesero ya está disponible.'));
  }
}

function armarMenu(opciones) {
  $('menu').replaceChildren(...opciones.map((o) => {
    const b = el('button', 'menu-item activo', o);
    b.type = 'button';
    return b;
  }));
}

/* ---------- mapa de mesas (mesero) ---------- */
function abrirMesas() {
  $('titulo').textContent = 'Mesas';
  cargarMesas();
  cerrarWs = conectarStomp(['/topic/meseros'], () => cargarMesas(), (ok) => {
    const c = $('conexion');
    c.textContent = ok ? 'En vivo' : 'Sin conexión';
    c.className = 'conexion' + (ok ? ' en-vivo' : '');
  });
  // Respaldo: si el WebSocket se cae, el mapa se actualiza cada 15 s.
  temporizador = setInterval(cargarMesas, 15000);
}

async function cargarMesas() {
  try {
    const [libres, ocupadas] = await Promise.all([
      Api.get('/api/mesero/mesas/libres'),
      Api.get('/api/mesero/mesas/ocupadas')
    ]);
    const todas = [...libres, ...ocupadas]
      .sort((a, b) => a.numero.localeCompare(b.numero, 'es', { numeric: true }));
    $('resumen').textContent = libres.length + ' libres · ' + ocupadas.length + ' ocupadas';
    pintarMesas(todas);
  } catch (e) {
    if (e.status !== 401) avisar('No se pudo cargar el mapa de mesas. ' + e.message, true);
  }
}

function pintarMesas(mesas) {
  const c = $('contenido');
  if (!mesas.length) {
    c.replaceChildren(el('p', 'vacio', 'Todavía no hay mesas registradas. Pide al administrador que las cree.'));
    return;
  }
  const grilla = el('div', 'grilla');
  mesas.forEach((m) => grilla.appendChild(tarjetaMesa(m)));
  c.replaceChildren(grilla);
}

function tarjetaMesa(m) {
  const libre = m.estado === 'LIBRE';
  const card = el('div', 'mesa ' + (libre ? 'libre' : 'ocupada'));

  const cabecera = el('div', 'mesa-cab');
  cabecera.append(el('span', 'mesa-num', 'Mesa ' + m.numero),
                  el('span', 'etiqueta', libre ? 'Libre' : 'Ocupada'));
  card.append(cabecera, el('p', 'mesa-ubic', m.ubicacion || 'Sin ubicación'));

  const accion = el('button', libre ? 'btn btn-chico' : 'btn btn-chico btn-borde',
                    libre ? 'Tomar pedido' : 'Liberar mesa');
  accion.type = 'button';
  accion.addEventListener('click', () => libre ? tomarPedido(m) : liberarMesa(m));
  card.appendChild(accion);
  return card;
}

function tomarPedido(m) {
  // Se completa en el siguiente módulo (carrito de productos).
  avisar('La toma de pedido de la mesa ' + m.numero + ' se agrega en el siguiente módulo.');
}

async function liberarMesa(m) {
  if (!confirm('¿Liberar la mesa ' + m.numero + '?')) return;
  try {
    await Api.post('/api/mesero/mesas/' + m.id + '/liberar');
    avisar('Mesa ' + m.numero + ' liberada.');
    cargarMesas();
  } catch (e) {
    avisar(e.message, true);   // p. ej. "la mesa tiene pedidos activos"
  }
}

iniciar();
