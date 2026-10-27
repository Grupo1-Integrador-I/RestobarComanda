/**
 * main.js - Punto de entrada de la aplicación.
 * 
 * Arquitectura:
 *   1. RENDER   → funciones puras que pintan el DOM a partir del estado.
 *   2. ACCIONES → mapa de acciones (data-action → handler).
 *   3. INIT     → suscribe al store, delega eventos, arranca.
 */

import { getState, setState, subscribe } from './state/store.js';
import {
    enviarComanda,
    avanzarEstadoPedido,
    cancelarPedido,
    obtenerPedidosEnPreparacion
} from './services/pedidoService.js';
import { registrarPago } from './services/pagoService.js';
import { renderMesaCard } from './components/MesaCard.js';
import { renderPedidoActivo } from './components/PedidoActivoCard.js';
import { renderTicketPreparacion } from './components/TicketPreparacion.js';
// 🔧 FIX: el componente del modal se importa con su nombre original.
import { renderModalPago } from './components/ModalPago.js';
import { renderSelectorRol, vistasPermitidas } from './components/SelectorRol.js';
import {
    renderListaItems,
    calcularTotal,
    getHeaderInfo
} from './components/ModalComanda.js';
import { formatCurrency } from './utils/formatters.js';
import { esPedidoActivo } from './state/domain.js';
import {
    getMesas,
    getPedidosActivosPorMesa,
    getProductosDisponibles,
    entregarPedidoReal
} from './services/httpClient.js';
// ============================================================
// 1. RENDER
// ============================================================

const renderApp = (state) => {
    renderNavPorRol(state.usuario.rol);
    renderPerfilUsuario(state.usuario);
    renderSelectorRolUI(state.usuario.rol);
    renderMesas(state.mesas, state.pedidos);
    renderComandas(state.pedidos);
    renderPreparacion(state.pedidos, state.filtroPreparacion);
    renderBadgePreparacion();
    renderFiltrosActivos(state.filtroPreparacion);
    renderModalComanda(state);
    renderModalPagoUI(state); // 🔧 FIX: antes llamaba a `renderModalPago`
};

// ---------- Navegación y rol ----------

const renderNavPorRol = (rol) => {
    const permitidas = vistasPermitidas(rol);

    document.querySelectorAll('.sidebar-nav .nav-item').forEach(item => {
        const target = item.getAttribute('data-target')?.replace('view-', '');
        item.style.display = permitidas.includes(target) ? '' : 'none';
    });

    const vistaActiva = document.querySelector('.view-section.active')?.id.replace('view-', '');
    if (!permitidas.includes(vistaActiva)) {
        activarVista(permitidas[0]);
    }
};

const renderPerfilUsuario = (usuario) => {
    const nombreEl = document.getElementById('user-name');
    const rolEl = document.getElementById('user-role');
    if (nombreEl) nombreEl.textContent = usuario.nombre;

    const labelRol = {
        MESERO: 'Mesero',
        PREPARADOR: 'Preparador',
        ADMINISTRADOR: 'Administrador'
    }[usuario.rol] || usuario.rol;

    if (rolEl) rolEl.textContent = labelRol;
};

const renderSelectorRolUI = (rolActual) => {
    const cont = document.getElementById('selector-rol-container');
    if (cont) cont.innerHTML = renderSelectorRol(rolActual);
};

// ---------- Vistas ----------

const renderMesas = (mesas, pedidos) => {
    const grid = document.getElementById('mesas-grid');
    if (!grid) return;
    grid.innerHTML = mesas.map(m => renderMesaCard(m, pedidos)).join('');
};

const renderComandas = (pedidos) => {
    const tbody = document.getElementById('comandas-tbody');
    if (!tbody) return;

    const pedidosOrdenados = [...pedidos].sort((a, b) => b.id - a.id);

    if (pedidosOrdenados.length === 0) {
        tbody.innerHTML = `<tr><td colspan="6" class="empty-state">No hay comandas registradas.</td></tr>`;
        return;
    }

    tbody.innerHTML = pedidosOrdenados.map(p => {
        const hora = p.fecha.toLocaleTimeString('es-PE', {
            hour: '2-digit',
            minute: '2-digit'
        });
        const { label, badgeClass } = CONFIG_BADGE[p.estado] || { label: p.estado, badgeClass: 'badge-draft' };

        return `
            <tr>
                <td><strong>#${p.id}</strong></td>
                <td>Mesa ${p.mesaId}</td>
                <td>${hora}</td>
                <td>
                    <span class="badge ${badgeClass}" style="width: fit-content;">
                        ${label}
                    </span>
                </td>
                <td>${formatCurrency(p.total)}</td>
               
<td>
    <button class="btn-text"
            data-action="ver-pedidos"
            data-mesa-id="${p.mesaId}">
        Ver
    </button>

    ${p.estado === 'LISTO' ? `
        <button class="btn-text"
                data-action="entregar-pedido"
                data-pedido-id="${p.id}">
            Entregar pedido
        </button>
    ` : ''}
</td>

            </tr>
        `;
    }).join('');
};

const renderPreparacion = (pedidos, filtro) => {
    const board = document.getElementById('kitchen-board');
    if (!board) return;

    let pendientes = obtenerPedidosEnPreparacion();

    if (filtro === 'COCINA') {
        pendientes = pendientes.filter(p => p.items.some(i => i.categoria === 'COCINA'));
    } else if (filtro === 'BARRA') {
        pendientes = pendientes.filter(p => p.items.some(i => i.categoria === 'BARRA'));
    }

    if (pendientes.length === 0) {
        board.innerHTML = `<p class="empty-state">No hay tickets pendientes en esta área.</p>`;
        return;
    }

    board.innerHTML = pendientes.map(renderTicketPreparacion).join('');
};

const renderBadgePreparacion = () => {
    const badge = document.getElementById('cocina-badge');
    if (!badge) return;
    const cantidad = obtenerPedidosEnPreparacion().length;
    badge.textContent = cantidad;
    badge.style.display = cantidad > 0 ? 'block' : 'none';
};

const renderFiltrosActivos = (filtroActual) => {
    document.querySelectorAll('.filtro-btn').forEach(btn => {
        btn.classList.toggle('active', btn.dataset.filtro === filtroActual);
    });
};

// ---------- Modales ----------

const renderModalComanda = (state) => {
    const modal = document.getElementById('comanda-modal');
    if (!modal) return;

    const { comandaActiva, pedidos } = state;

    if (!comandaActiva.mesaId) {
        modal.classList.remove('active');
        return;
    }

    const pedidosActivos = pedidos.filter(
        p => p.mesaId === comandaActiva.mesaId && esPedidoActivo(p)
    );

    const btnEnviar = modal.querySelector('[data-action="enviar-comanda"]');
    const addSection = modal.querySelector('.add-item-section');
    const titleEl = document.getElementById('modal-title');
    const timeEl = document.getElementById('modal-time');
    const itemsEl = document.getElementById('order-items-list');
    const totalEl = document.getElementById('modal-total');

    if (pedidosActivos.length > 0) {
        titleEl.textContent = `Mesa ${comandaActiva.mesaId} • ${pedidosActivos.length} pedido(s)`;
        timeEl.textContent = getHeaderInfo(comandaActiva.mesaId).hora;

        itemsEl.innerHTML = pedidosActivos.map(renderPedidoActivo).join('');
        totalEl.textContent = formatCurrency(
            pedidosActivos.reduce((s, p) => s + p.total, 0)
        );

        if (btnEnviar) btnEnviar.style.display = 'none';
        if (addSection) addSection.style.display = 'none';
    } else {
        const { titulo, hora } = getHeaderInfo(comandaActiva.mesaId);
        titleEl.textContent = titulo;
        timeEl.textContent = hora;

        itemsEl.innerHTML = renderListaItems(comandaActiva.items);
        totalEl.textContent = formatCurrency(calcularTotal(comandaActiva.items));

        if (btnEnviar) btnEnviar.style.display = '';
        if (addSection) addSection.style.display = '';
    }

    modal.classList.add('active');
};

// 🔧 FIX: renombrado de `renderModalPago` → `renderModalPagoUI`
// Ahora no colisiona con el import del componente `renderModalPago`.
const renderModalPagoUI = (state) => {
    const modal = document.getElementById('pago-modal');
    if (!modal) return;

    const pedido = state.pedidos.find(p => p.id === state.pagoActivo.pedidoId);
    if (!pedido) {
        modal.classList.remove('active');
        return;
    }

    const contenido = modal.querySelector('.modal-content');
    if (contenido) contenido.innerHTML = renderModalPago(pedido); // aquí SÍ uso el componente
    modal.classList.add('active');
};

// ---------- Helpers ----------

const activarVista = (vista) => {
    document.querySelectorAll('.view-section').forEach(v => {
        const isTarget = v.id === `view-${vista}`;
        v.classList.toggle('active', isTarget);
        v.style.display = isTarget ? 'flex' : 'none';
    });

    document.querySelectorAll('.sidebar-nav .nav-item').forEach(n => {
        n.classList.toggle('active', n.getAttribute('data-target') === `view-${vista}`);
    });
};

const CONFIG_BADGE = {
    CREADO:         { label: 'Borrador',        badgeClass: 'badge-draft'   },
    ENVIADO:        { label: 'Enviado',         badgeClass: 'badge-warning' },
    EN_PREPARACION: { label: 'En preparación',  badgeClass: 'badge-warning' },
    LISTO:          { label: 'Listo',           badgeClass: 'badge-info'    },
    ENTREGADO:      { label: 'Entregado',       badgeClass: 'badge-success' },
    PAGADO:         { label: 'Pagado',          badgeClass: 'badge-paid'    },
    CANCELADO:      { label: 'Cancelado',       badgeClass: 'badge-danger'  }
};

// ============================================================
// 2. ACCIONES
// ============================================================

const ACCIONES = {

    'cambiar-rol': (el) => {
        const rol = el.dataset.rol;
        setState({ usuario: { ...getState().usuario, rol } });
    },

   
'nueva-orden': () => {
    const mesas = getState().mesas;

    const numeroMesa = prompt('¿Qué número de mesa deseas abrir?');

    if (numeroMesa === null) return;

    const mesa = mesas.find(
        m => String(m.numero) === numeroMesa.trim()
    );

    if (!mesa) {
        alert('La mesa indicada no existe.');
        return;
    }

    if (mesa.estado !== 'LIBRE') {
        alert(`La Mesa ${mesa.numero} está ocupada.`);
        return;
    }

    setState({
        comandaActiva: {
            mesaId: mesa.id,
            pedidoId: null,
            items: []
        }
    });
},


 
'agregar-item-demo': () => {
    const state = getState();

    if (!state.comandaActiva.mesaId) {
        alert('Primero selecciona una mesa.');
        return;
    }

    const productos = state.productos;

    if (!productos.length) {
        alert('No hay productos disponibles.');
        return;
    }

    const opciones = productos.map(p =>
        `${p.id} - ${p.nombre} - S/ ${p.precio.toFixed(2)}`
    ).join('\n');

    const seleccion = prompt(
        'PRODUCTOS DISPONIBLES:\n\n' +
        opciones +
        '\n\nIngresa el ID del producto:'
    );

    if (seleccion === null) return;

    const productoId = Number(seleccion);

    const producto = productos.find(p => p.id === productoId);

    if (!producto) {
        alert('El producto seleccionado no existe.');
        return;
    }

    const cantidadTexto = prompt(
        `¿Qué cantidad de ${producto.nombre} deseas agregar?`,
        '1'
    );

    if (cantidadTexto === null) return;

    const cantidad = Number(cantidadTexto);

    if (!Number.isSafeInteger(cantidad) || cantidad <= 0) {
        alert('Ingresa una cantidad válida.');
        return;
    }

    const itemExistente = state.comandaActiva.items.find(
        item => item.productoId === producto.id
    );

    let nuevosItems;

    if (itemExistente) {
        nuevosItems = state.comandaActiva.items.map(item =>
            item.productoId === producto.id
                ? { ...item, cantidad: item.cantidad + cantidad }
                : item
        );
    } else {
        nuevosItems = [
            ...state.comandaActiva.items,
            {
                productoId: producto.id,
                nombre: producto.nombre,
                precio: producto.precio,
                categoria: producto.categoria,
                cantidad
            }
        ];
    }

    setState({
        comandaActiva: {
            ...state.comandaActiva,
            items: nuevosItems
        }
    });
},


    'enviar-comanda': async () => {
        try {
            await enviarComanda();
        } catch (err) {
            alert(err.message);
        }
    },

'entregar-pedido': async (el) => {
    const pedidoId = Number(el.dataset.pedidoId);

    if (!Number.isSafeInteger(pedidoId) || pedidoId <= 0) {
        alert('El número de pedido no es válido.');
        return;
    }

    if (!confirm(
        `¿Confirmas que entregaste el pedido #${pedidoId} al cliente?`
    )) {
        return;
    }

    el.disabled = true;

    try {
        await entregarPedidoReal(pedidoId);

        setState(prev => ({
            pedidos: prev.pedidos.map(p =>
                p.id === pedidoId
                    ? { ...p, estado: 'ENTREGADO' }
                    : p
            )
        }));

        alert(`Pedido #${pedidoId} entregado correctamente.`);

    } catch (error) {
        alert('No se pudo entregar el pedido: ' + error.message);
        el.disabled = false;
    }
},

    'cerrar-comanda': () => {
        setState({ comandaActiva: { mesaId: null, pedidoId: null, items: [] } });
    },

    'abrir-pago': (el) => {
        const pedidoId = Number(el.dataset.pedidoId);
        setState({
            pagoActivo: { pedidoId },
            comandaActiva: { mesaId: null, pedidoId: null, items: [] }
        });
    },

    'confirmar-pago': async (el) => {
        const pedidoId = Number(el.dataset.pedidoId);
        const tipo = document.querySelector('input[name="tipo-pago"]:checked')?.value;
        if (!tipo) {
            alert('Selecciona un método de pago.');
            return;
        }
        try {
            await registrarPago(pedidoId, tipo);
        } catch (err) {
            alert(err.message);
        }
    },

    'cerrar-pago': () => {
        setState({ pagoActivo: { pedidoId: null } });
    },

    'avanzar-pedido': async (el) => {
        const pedidoId = Number(el.dataset.pedidoId);
        const siguiente = el.dataset.siguienteEstado;
        try {
            await avanzarEstadoPedido(pedidoId, siguiente);
        } catch (err) {
            alert(err.message);
        }
    },

    'cancelar-pedido': async (el) => {
        const pedidoId = Number(el.dataset.pedidoId);
        if (!confirm('¿Cancelar este pedido?')) return;
        try {
            await cancelarPedido(pedidoId);
        } catch (err) {
            alert(err.message);
        }
    },

    'filtrar': (el) => {
        setState({ filtroPreparacion: el.dataset.filtro });
    },

    'ver-pedidos': (el) => {
        const mesaId = Number(el.dataset.mesaId);
        setState({
            comandaActiva: { mesaId, pedidoId: null, items: [] }
        });
    }
};

/**
 * Consulta los pedidos reales de PostgreSQL
 * y actualiza el panel del mesero.
 */
let actualizandoPedidos = false;

const actualizarPedidosAutomaticamente = async () => {

    if (actualizandoPedidos) return;

    if (getState().usuario.rol !== 'MESERO') return;

    actualizandoPedidos = true;

    try {
        const mesas = getState().mesas;

        if (mesas.length === 0) return;

        const resultados = await Promise.all(
            mesas.map(mesa =>
                getPedidosActivosPorMesa(mesa.id)
            )
        );

        const pedidosActualizados = resultados
            .flat()
            .map(pedido => ({
                ...pedido,
                fecha: new Date(pedido.fecha)
            }));

        setState({
            pedidos: pedidosActualizados
        });

    } catch (error) {
        console.error(
            'Error al actualizar pedidos automáticamente:',
            error
        );
    } finally {
        actualizandoPedidos = false;
    }
};

// ============================================================
// 3. INIT
// ============================================================

const initNavegacionSidebar = () => {
    document.querySelectorAll('.sidebar-nav .nav-item').forEach(item => {
        item.addEventListener('click', (e) => {
            e.preventDefault();
            const target = item.getAttribute('data-target')?.replace('view-', '');
            if (target) activarVista(target);
        });
    });
};

const initEventDelegation = () => {
    document.body.addEventListener('click', async (e) => {
        const accionEl = e.target.closest('[data-action]');
        if (accionEl) {
            const accion = accionEl.dataset.action;
            const handler = ACCIONES[accion];
            if (handler) {
                e.preventDefault();
                await handler(accionEl);
            }
            return;
        }

        const cardMesa = e.target.closest('.table-card[data-mesa-id]');
        if (cardMesa) {
            const mesaId = Number(cardMesa.dataset.mesaId);
            setState({
                comandaActiva: { mesaId, pedidoId: null, items: [] }
            });
        }
    });
};



const init = async () => {

    // Inicializar navegación y eventos
    initNavegacionSidebar();
    initEventDelegation();

    // Escuchar cambios en el estado
    subscribe(renderApp);

    // Dibujar interfaz inicial
    renderApp(getState());

    try {
        // Consultar usuario autenticado
        const respuestaUsuario = await fetch('/api/auth/me', {
            credentials: 'same-origin'
        });

        if (!respuestaUsuario.ok) {
            throw new Error(
                'Error al obtener usuario: HTTP ' +
                respuestaUsuario.status
            );
        }

        const usuarioReal = await respuestaUsuario.json();

        // Actualizar usuario del panel
        setState({
            usuario: {
                id: usuarioReal.id,
                nombre: usuarioReal.nombre + ' ' + usuarioReal.apellido,
                rol: usuarioReal.rol
            }
        });

        // Consultar mesas reales
       
const mesasReales = await getMesas();
setState({ mesas: mesasReales });

// Consultar pedidos activos de cada mesa
const resultados = await Promise.all(
    mesasReales.map(mesa =>
        getPedidosActivosPorMesa(mesa.id)
    )
);

// Unir todos los pedidos en un solo arreglo
const pedidosReales = resultados.flat();

// Convertir las fechas recibidas del backend
const pedidosFormateados = pedidosReales.map(p => ({
    ...p,
    fecha: new Date(p.fecha)
}));

// Actualizar el estado global
setState({ pedidos: pedidosFormateados });

console.log('Pedidos reales cargados:', pedidosFormateados);

// Consultar catálogo real de productos
const productosReales = await getProductosDisponibles();

// Guardar los productos en el estado global
setState({ productos: productosReales });

console.log('Productos reales cargados:', productosReales);



        console.log('Usuario autenticado:', usuarioReal);
        console.log('Mesas cargadas:', mesasReales);

    } catch (error) {
        console.error(
            'Error al inicializar la aplicación:',
            error
        );
    }
    
    // Actualizar los pedidos cada 5 segundos
    setInterval(() => {
        actualizarPedidosAutomaticamente();
    }, 5000);

};

// Ejecutar al cargar la página
document.addEventListener('DOMContentLoaded', init);

