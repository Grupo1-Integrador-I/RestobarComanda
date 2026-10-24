package presentacion.api;
import aplicacion.casosdeuso.ActualizarEstadoPedido;
import aplicacion.casosdeuso.GestionUsuarios;
import boot.RestobarApp;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Mesa;
import dominio.modelo.Monto;
import dominio.modelo.Producto;
import dominio.modelo.Rol;
import dominio.puerto.repositorio.RepositorioMesa;
import dominio.puerto.repositorio.RepositorioProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 *
 * @author inici4rsesi0n
 */
@SpringBootTest(classes = RestobarApp.class)
@AutoConfigureMockMvc
@Transactional
class MeseroControllerIT{
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired GestionUsuarios gestionUsuarios;
    @Autowired RepositorioMesa repositorioMesa;
    @Autowired RepositorioProducto repositorioProducto;
    @Autowired ActualizarEstadoPedido actualizarEstadoPedido;
    private MockHttpSession session;
    private Long mesaId;
    private Long productoId;
    @BeforeEach
    void seed() throws Exception{
        gestionUsuarios.agregar("juan", "juan123".toCharArray(), "12345678",
                "Juan", "Pérez", Rol.MESERO);
        Mesa mesa=new Mesa("M1", "Salón");
        repositorioMesa.agregar(mesa);
        mesaId=mesa.getId();
        Producto producto=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        repositorioProducto.agregar(producto);
        productoId=producto.getId();
        MvcResult loginResult=mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"juan\",\"password\":\"juan123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        session=(MockHttpSession)loginResult.getRequest().getSession();
    }
    private Long crearPedidoYExtraerId() throws Exception{
        MvcResult result=mockMvc.perform(post("/api/mesero/pedidos").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mesaId\":" + mesaId + "}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
    @Test
    void listarMesasLibresDevuelveUna() throws Exception{
        mockMvc.perform(get("/api/mesero/mesas/libres").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].numero").value("M1"));
    }
    @Test
    void listarMesasOcupadasSinPedidosDevuelveCero() throws Exception{
        mockMvc.perform(get("/api/mesero/mesas/ocupadas").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
    @Test
    void listarProductosDisponibles() throws Exception{
        mockMvc.perform(get("/api/mesero/productos").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ceviche"));
    }
    @Test
    void listarProductosPorCategoriaCocina() throws Exception{
        mockMvc.perform(get("/api/mesero/productos").param("categoria", "COCINA").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    void iniciarPedidoDevuelve201() throws Exception{
        mockMvc.perform(post("/api/mesero/pedidos").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"mesaId\":" + mesaId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("CREADO"))
                .andExpect(jsonPath("$.mesaId").value(mesaId))
                .andExpect(jsonPath("$.nombreMesero").value("Juan Pérez"));
    }
    @Test
    void obtenerPedidoExistente() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(get("/api/mesero/pedidos/" + id).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }
    @Test
    void obtenerPedidoInexistenteDevuelve404() throws Exception{
        mockMvc.perform(get("/api/mesero/pedidos/99999").session(session))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Pedido no encontrado"));
    }
    @Test
    void listarActivosPorMesa() throws Exception{
        crearPedidoYExtraerId();
        mockMvc.perform(get("/api/mesero/pedidos").param("mesaId", mesaId.toString()).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
    @Test
    void agregarItemDevuelve200() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/items").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":2}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(51.00))
                .andExpect(jsonPath("$.items.length()").value(1));
    }
    @Test
    void eliminarItemDevuelve200() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/items").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":1}"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/mesero/pedidos/" + id + "/items/" + productoId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0))
                .andExpect(jsonPath("$.total").value(0.00));
    }
    @Test
    void enviarPedidoDevuelve200() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/items").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":1}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/enviar").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADO"));
    }
    @Test
    void enviarPedidoSinItemsDevuelve409() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/enviar").session(session))
                .andExpect(status().isConflict());
    }
    @Test
    void entregarPedidoSinListoDevuelve409() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/entregar").session(session))
                .andExpect(status().isConflict());
    }
    @Test
    void pagarPedidoSinEntregadoDevuelve409() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/pagar").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"EFECTIVO\"}"))
                .andExpect(status().isConflict());
    }
    @Test
    void flujoCompletoPagarPedidoDevuelve201() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/items").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":2}"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/enviar").session(session))
                .andExpect(status().isOk());
        actualizarEstadoPedido.marcarEnPreparacion(id);
        actualizarEstadoPedido.marcarListo(id);
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/entregar").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENTREGADO"));
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/pagar").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\":\"EFECTIVO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.monto").value(51.00))
                .andExpect(jsonPath("$.tipo").value("EFECTIVO"));
    }
    @Test
    void cancelarPedidoDevuelve200YLiberaMesa() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/cancelar").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CANCELADO"));
        mockMvc.perform(get("/api/mesero/mesas/" + mesaId).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("LIBRE"));
    }
    @Test
    void liberarMesaConPedidoActivoDevuelve409() throws Exception{
        crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/mesas/" + mesaId + "/liberar").session(session))
                .andExpect(status().isConflict());
    }
    @Test
    void liberarMesaLibreDevuelve409() throws Exception{
        mockMvc.perform(post("/api/mesero/mesas/" + mesaId + "/liberar").session(session))
                .andExpect(status().isConflict());
    }
    @Test
    void iniciarPedidoMesaIdNuloDevuelve400() throws Exception{
        mockMvc.perform(post("/api/mesero/pedidos").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void agregarItemCantidadCeroDevuelve400() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/items").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productoId\":" + productoId + ",\"cantidad\":0}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void pagarTipoNuloDevuelve400() throws Exception{
        Long id=crearPedidoYExtraerId();
        mockMvc.perform(post("/api/mesero/pedidos/" + id + "/pagar").session(session)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void accesoAdminAMeseroDevuelve403() throws Exception{
        MvcResult adminLogin=mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession adminSession=(MockHttpSession)adminLogin.getRequest().getSession();
        mockMvc.perform(get("/api/mesero/mesas/libres").session(adminSession))
                .andExpect(status().isForbidden());
    }
    @Test
    void sinSesionDevuelve401() throws Exception{
        mockMvc.perform(get("/api/mesero/pedidos/1"))
                .andExpect(status().isUnauthorized());
    }
}
