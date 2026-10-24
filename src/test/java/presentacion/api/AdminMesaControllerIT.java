package presentacion.api;
import boot.RestobarApp;
import dominio.modelo.Mesa;
import dominio.puerto.repositorio.RepositorioMesa;
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
class AdminMesaControllerIT{
    @Autowired MockMvc mockMvc;
    @Autowired RepositorioMesa repositorioMesa;
    private Long mesa1Id;
    private Long mesa2Id;
    @BeforeEach
    void seed(){
        Mesa m1=new Mesa("M1", "Salón");
        Mesa m2=new Mesa("M2", "Terraza");
        repositorioMesa.agregar(m1);
        repositorioMesa.agregar(m2);
        mesa1Id=m1.getId();
        mesa2Id=m2.getId();
    }
    @Test
    void listarTodasDevuelveDosMesas() throws Exception{
        mockMvc.perform(get("/api/admin/mesas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    void listarFiltroLibreDevuelveDos() throws Exception{
        mockMvc.perform(get("/api/admin/mesas").param("estado", "LIBRE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    void listarFiltroOcupadoSinMesasDevuelveCero() throws Exception{
        mockMvc.perform(get("/api/admin/mesas").param("estado", "OCUPADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
    @Test
    void listarFiltroOcupadoConMesasDevuelveLasOcupadas() throws Exception{
        Mesa m3=new Mesa("M3", "Barra");
        m3.ocupar();
        repositorioMesa.agregar(m3);
        mockMvc.perform(get("/api/admin/mesas").param("estado", "OCUPADO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].numero").value("M3"));
    }
    @Test
    void buscarPorNumeroDevuelveCoincidencias() throws Exception{
        mockMvc.perform(get("/api/admin/mesas/buscar").param("q", "M1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].numero").value("M1"));
    }
    @Test
    void buscarSinQueryDevuelveTodas() throws Exception{
        mockMvc.perform(get("/api/admin/mesas/buscar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
    @Test
    void obtenerPorIdExistente() throws Exception{
        mockMvc.perform(get("/api/admin/mesas/" + mesa1Id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("M1"));
    }
    @Test
    void obtenerPorIdInexistenteDevuelve404() throws Exception{
        mockMvc.perform(get("/api/admin/mesas/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Mesa no encontrada"));
    }
    @Test
    void crearMesaDevuelve201() throws Exception{
        mockMvc.perform(post("/api/admin/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"M3\",\"ubicacion\":\"Barra\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.numero").value("M3"))
                .andExpect(jsonPath("$.estado").value("LIBRE"));
    }
    @Test
    void crearMesaDuplicadaDevuelve409() throws Exception{
        mockMvc.perform(post("/api/admin/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"M1\",\"ubicacion\":\"Otra\"}"))
                .andExpect(status().isConflict());
    }
    @Test
    void crearMesaNumeroVacioDevuelve400() throws Exception{
        mockMvc.perform(post("/api/admin/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"\",\"ubicacion\":\"X\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void crearMesaUbicacionVaciaDevuelve400() throws Exception{
        mockMvc.perform(post("/api/admin/mesas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"M9\",\"ubicacion\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void actualizarMesaDevuelve200() throws Exception{
        mockMvc.perform(put("/api/admin/mesas/" + mesa1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"M1bis\",\"ubicacion\":\"Salón\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("M1bis"));
    }
    @Test
    void actualizarMesaNumeroDuplicadoDevuelve409() throws Exception{
        mockMvc.perform(put("/api/admin/mesas/" + mesa1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"numero\":\"M2\",\"ubicacion\":\"Salón\"}"))
                .andExpect(status().isConflict());
    }
    @Test
    void eliminarMesaDevuelve204() throws Exception{
        mockMvc.perform(delete("/api/admin/mesas/" + mesa1Id))
                .andExpect(status().isNoContent());
    }
    @Test
    void eliminarMesaOcupadaDevuelve409() throws Exception{
        Mesa m3=new Mesa("M3", "Barra");
        m3.ocupar();
        repositorioMesa.agregar(m3);
        mockMvc.perform(delete("/api/admin/mesas/" + m3.getId()))
                .andExpect(status().isConflict());
    }
    @Test
    void ocuparMesaDevuelve200() throws Exception{
        mockMvc.perform(patch("/api/admin/mesas/" + mesa1Id + "/ocupar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("OCUPADO"));
    }
    @Test
    void ocuparMesaYaOcupadaDevuelve409() throws Exception{
        Mesa m3=new Mesa("M3", "Barra");
        m3.ocupar();
        repositorioMesa.agregar(m3);
        mockMvc.perform(patch("/api/admin/mesas/" + m3.getId() + "/ocupar"))
                .andExpect(status().isConflict());
    }
    @Test
    void liberarMesaDevuelve200() throws Exception{
        Mesa m3=new Mesa("M3", "Barra");
        m3.ocupar();
        repositorioMesa.agregar(m3);
        mockMvc.perform(patch("/api/admin/mesas/" + m3.getId() + "/liberar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("LIBRE"));
    }
    @Test
    void liberarMesaYaLibreDevuelve409() throws Exception{
        mockMvc.perform(patch("/api/admin/mesas/" + mesa1Id + "/liberar"))
                .andExpect(status().isConflict());
    }
}
