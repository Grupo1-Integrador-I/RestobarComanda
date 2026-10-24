package presentacion.api;
import boot.RestobarApp;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Monto;
import dominio.modelo.Producto;
import dominio.puerto.repositorio.RepositorioProducto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 *
 * @author inici4rsesi0n
 */
@SpringBootTest(classes = RestobarApp.class)
@AutoConfigureMockMvc
@Transactional
@WithMockUser(roles = "ADMINISTRADOR")
class AdminProductoControllerIT{
    @Autowired MockMvc mockMvc;
    @Autowired RepositorioProducto repositorioProducto;
    private Long cevicheId;
    @BeforeEach
    void seed(){
        Producto ceviche=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        Producto chicha=new Producto("Chicha", Monto.de("8.00"), CategoriaProducto.BARRA);
        repositorioProducto.agregar(ceviche);
        repositorioProducto.agregar(chicha);
        cevicheId=ceviche.getId();
    }
    @Test
    void listarTodosDevuelveDos() throws Exception{
        mockMvc.perform(get("/api/admin/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    void listarPorCategoriaCocinaDevuelveUno() throws Exception{
        mockMvc.perform(get("/api/admin/productos").param("categoria", "COCINA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ceviche"));
    }
    @Test
    void listarPorCategoriaBarraDevuelveUno() throws Exception{
        mockMvc.perform(get("/api/admin/productos").param("categoria", "BARRA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Chicha"));
    }
    @Test
    void listarDisponiblesDevuelveDos() throws Exception{
        mockMvc.perform(get("/api/admin/productos").param("disponible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    void categoriaTienePrioridadSobreDisponible() throws Exception{
        mockMvc.perform(get("/api/admin/productos")
                        .param("categoria", "BARRA").param("disponible", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].categoria").value("BARRA"));
    }
    @Test
    void buscarPorNombreDevuelveCoincidencias() throws Exception{
        mockMvc.perform(get("/api/admin/productos/buscar").param("q", "evi"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Ceviche"));
    }
    @Test
    void obtenerPorIdExistente() throws Exception{
        mockMvc.perform(get("/api/admin/productos/" + cevicheId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ceviche"));
    }
    @Test
    void obtenerPorIdInexistenteDevuelve404() throws Exception{
        mockMvc.perform(get("/api/admin/productos/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Producto no encontrado"));
    }
    @Test
    void crearProductoDevuelve201() throws Exception{
        mockMvc.perform(post("/api/admin/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Pisco Sour\",\"precio\":22.00,\"categoria\":\"BARRA\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Pisco Sour"))
                .andExpect(jsonPath("$.disponible").value(true));
    }
    @Test
    void crearProductoDuplicadoDevuelve409() throws Exception{
        mockMvc.perform(post("/api/admin/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ceviche\",\"precio\":30.00,\"categoria\":\"COCINA\"}"))
                .andExpect(status().isConflict());
    }
    @Test
    void crearProductoPrecioInvalidoDevuelve400() throws Exception{
        mockMvc.perform(post("/api/admin/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"X\",\"precio\":-1.00,\"categoria\":\"COCINA\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void crearProductoSinCategoriaDevuelve400() throws Exception{
        mockMvc.perform(post("/api/admin/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"X\",\"precio\":10.00}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void actualizarProductoIgnoraCategoria() throws Exception{
        mockMvc.perform(put("/api/admin/productos/" + cevicheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ceviche Mixto\",\"precio\":32.00,\"categoria\":\"BARRA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ceviche Mixto"))
                .andExpect(jsonPath("$.categoria").value("COCINA"));
    }
    @Test
    void actualizarProductoNombreDuplicadoDevuelve409() throws Exception{
        mockMvc.perform(put("/api/admin/productos/" + cevicheId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Chicha\",\"precio\":5.00}"))
                .andExpect(status().isConflict());
    }
    @Test
    void desactivarProductoDevuelve200() throws Exception{
        mockMvc.perform(patch("/api/admin/productos/" + cevicheId + "/desactivar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(false));
    }
    @Test
    void desactivarProductoYaDesactivadoDevuelve409() throws Exception{
        Producto p=new Producto("Agotado", Monto.de("10.00"), CategoriaProducto.COCINA);
        p.desactivar();
        repositorioProducto.agregar(p);
        mockMvc.perform(patch("/api/admin/productos/" + p.getId() + "/desactivar"))
                .andExpect(status().isConflict());
    }
    @Test
    void activarProductoDevuelve200() throws Exception{
        Producto p=new Producto("Agotado", Monto.de("10.00"), CategoriaProducto.COCINA);
        p.desactivar();
        repositorioProducto.agregar(p);
        mockMvc.perform(patch("/api/admin/productos/" + p.getId() + "/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.disponible").value(true));
    }
    @Test
    void eliminarProductoDevuelve204() throws Exception{
        mockMvc.perform(delete("/api/admin/productos/" + cevicheId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/admin/productos/" + cevicheId))
                .andExpect(status().isNotFound());
    }
}
