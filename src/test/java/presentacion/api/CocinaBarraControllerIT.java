package presentacion.api;
import boot.RestobarApp;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.EstadoPedido;
import dominio.modelo.ItemPedido;
import dominio.modelo.Mesa;
import dominio.modelo.Mesero;
import dominio.modelo.Monto;
import dominio.modelo.Pedido;
import dominio.modelo.Producto;
import dominio.puerto.repositorio.RepositorioMesa;
import dominio.puerto.repositorio.RepositorioPedido;
import dominio.puerto.repositorio.RepositorioProducto;
import dominio.puerto.repositorio.RepositorioUsuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
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
@WithMockUser(roles = "PREPARADOR")
class CocinaBarraControllerIT{
    @Autowired MockMvc mockMvc;
    @Autowired RepositorioMesa repositorioMesa;
    @Autowired RepositorioProducto repositorioProducto;
    @Autowired RepositorioUsuario repositorioUsuario;
    @Autowired RepositorioPedido repositorioPedido;
    private Long pedidoId;
    @BeforeEach
    void seed(){
        Mesa mesa=new Mesa("M1", "Salón");
        repositorioMesa.agregar(mesa);
        Mesero mesero=new Mesero("juan", "hashfake123456", "12345678", "Juan", "Pérez");
        repositorioUsuario.agregar(mesero);
        Producto ceviche=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        Producto pisco=new Producto("Pisco Sour", Monto.de("22.00"), CategoriaProducto.BARRA);
        repositorioProducto.agregar(ceviche);
        repositorioProducto.agregar(pisco);
        ItemPedido i1=new ItemPedido(ceviche, 1);
        ItemPedido i2=new ItemPedido(pisco, 2);
        Monto total=i1.getSubtotal().sumar(i2.getSubtotal());
        Pedido pedido=Pedido.reconstruir(mesa, mesero, List.of(i1, i2),
                total, EstadoPedido.ENVIADO, LocalDateTime.now(), null);
        repositorioPedido.agregar(pedido);
        pedidoId=pedido.getId();
    }
    @Test
    void cocinaPendientesDevuelveSoloCeviche() throws Exception{
        mockMvc.perform(get("/api/cocina/pedidos/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].nombreProducto").value("Ceviche"));
    }
    @Test
    void barraPendientesDevuelveSoloPisco() throws Exception{
        mockMvc.perform(get("/api/barra/pedidos/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].nombreProducto").value("Pisco Sour"));
    }
    @Test
    void cocinaMarcarPreparacionCambiaEstadoGlobal() throws Exception{
        mockMvc.perform(post("/api/cocina/pedidos/" + pedidoId + "/preparacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_PREPARACION"))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].nombreProducto").value("Ceviche"));
    }
    @Test
    void barraEnPreparacionVeSuItem() throws Exception{
        mockMvc.perform(post("/api/cocina/pedidos/" + pedidoId + "/preparacion"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/barra/pedidos/en-preparacion"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].nombreProducto").value("Pisco Sour"));
    }
    @Test
    void cocinaPendientesVacioTrasPreparacion() throws Exception{
        mockMvc.perform(post("/api/cocina/pedidos/" + pedidoId + "/preparacion"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/cocina/pedidos/pendientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
    @Test
    void cocinaMarcarListoDevuelve200() throws Exception{
        mockMvc.perform(post("/api/cocina/pedidos/" + pedidoId + "/preparacion"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/cocina/pedidos/" + pedidoId + "/listo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("LISTO"));
    }
    @Test
    void prepararPedidoEnviadoDosVecesDevuelve409() throws Exception{
        mockMvc.perform(post("/api/cocina/pedidos/" + pedidoId + "/preparacion"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/cocina/pedidos/" + pedidoId + "/preparacion"))
                .andExpect(status().isConflict());
    }
    @Test
    void marcarListoSinPreparacionDevuelve409() throws Exception{
        mockMvc.perform(post("/api/cocina/pedidos/" + pedidoId + "/listo"))
                .andExpect(status().isConflict());
    }
    @Test
    void pedidoInexistenteDevuelve404() throws Exception{
        mockMvc.perform(post("/api/cocina/pedidos/99999/preparacion"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Pedido no encontrado"));
    }
}
