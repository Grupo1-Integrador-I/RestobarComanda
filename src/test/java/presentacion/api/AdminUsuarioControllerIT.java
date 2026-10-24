package presentacion.api;
import boot.RestobarApp;
import dominio.modelo.Mesero;
import dominio.modelo.Preparador;
import dominio.modelo.Rol;
import dominio.puerto.repositorio.RepositorioUsuario;
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
class AdminUsuarioControllerIT{
    @Autowired MockMvc mockMvc;
    @Autowired RepositorioUsuario repositorioUsuario;
    private Long juanId;
    @BeforeEach
    void seed(){
        Mesero juan=new Mesero("juan", "hashfake123456", "12345678", "Juan", "Pérez");
        Preparador maria=new Preparador("maria", "hashfake654321", "87654321", "María", "García");
        repositorioUsuario.agregar(juan);
        repositorioUsuario.agregar(maria);
        juanId=juan.getId();
    }
    @Test
    void listarTodosIncluyeAdminYSeeds() throws Exception{
        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3));
    }
    @Test
    void listarPorRolMeseroDevuelveUno() throws Exception{
        mockMvc.perform(get("/api/admin/usuarios").param("rol", "MESERO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("juan"));
    }
    @Test
    void listarPorRolAdministradorDevuelveElBootstrap() throws Exception{
        mockMvc.perform(get("/api/admin/usuarios").param("rol", "ADMINISTRADOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("admin"));
    }
    @Test
    void buscarPorNombreApellidoDevuelveCoincidencias() throws Exception{
        mockMvc.perform(get("/api/admin/usuarios/buscar").param("q", "uan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value("juan"));
    }
    @Test
    void obtenerPorIdExistente() throws Exception{
        mockMvc.perform(get("/api/admin/usuarios/" + juanId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("juan"));
    }
    @Test
    void obtenerPorIdInexistenteDevuelve404() throws Exception{
        mockMvc.perform(get("/api/admin/usuarios/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }
    @Test
    void crearUsuarioDevuelve201() throws Exception{
        mockMvc.perform(post("/api/admin/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"pedro\",\"password\":\"pedro123\",\"dni\":\"11223344\",\"nombre\":\"Pedro\",\"apellido\":\"Lopez\",\"rol\":\"MESERO\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("pedro"))
                .andExpect(jsonPath("$.rol").value("MESERO"));
    }
    @Test
    void crearUsuarioCodigoDuplicadoDevuelve409() throws Exception{
        mockMvc.perform(post("/api/admin/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"juan\",\"password\":\"x123456\",\"dni\":\"99999999\",\"nombre\":\"X\",\"apellido\":\"Y\",\"rol\":\"MESERO\"}"))
                .andExpect(status().isConflict());
    }
    @Test
    void crearUsuarioDniDuplicadoDevuelve409() throws Exception{
        mockMvc.perform(post("/api/admin/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"otro\",\"password\":\"x123456\",\"dni\":\"12345678\",\"nombre\":\"X\",\"apellido\":\"Y\",\"rol\":\"MESERO\"}"))
                .andExpect(status().isConflict());
    }
    @Test
    void crearUsuarioPasswordCortaDevuelve400() throws Exception{
        mockMvc.perform(post("/api/admin/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"x\",\"password\":\"123\",\"dni\":\"99999999\",\"nombre\":\"X\",\"apellido\":\"Y\",\"rol\":\"MESERO\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void crearUsuarioSinRolDevuelve400() throws Exception{
        mockMvc.perform(post("/api/admin/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"x\",\"password\":\"123456\",\"dni\":\"99999999\",\"nombre\":\"X\",\"apellido\":\"Y\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void actualizarUsuarioDevuelve200() throws Exception{
        mockMvc.perform(put("/api/admin/usuarios/" + juanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"12345678\",\"nombre\":\"Juan Carlos\",\"apellido\":\"Pérez\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan Carlos"));
    }
    @Test
    void actualizarUsuarioDniDuplicadoDevuelve409() throws Exception{
        mockMvc.perform(put("/api/admin/usuarios/" + juanId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"dni\":\"87654321\",\"nombre\":\"Juan\",\"apellido\":\"Pérez\"}"))
                .andExpect(status().isConflict());
    }
    @Test
    void cambiarPasswordDevuelve204() throws Exception{
        mockMvc.perform(patch("/api/admin/usuarios/" + juanId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"nueva123\"}"))
                .andExpect(status().isNoContent());
    }
    @Test
    void cambiarPasswordCortaDevuelve400() throws Exception{
        mockMvc.perform(patch("/api/admin/usuarios/" + juanId + "/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"123\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void eliminarUsuarioDevuelve204() throws Exception{
        mockMvc.perform(delete("/api/admin/usuarios/" + juanId))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/admin/usuarios/" + juanId))
                .andExpect(status().isNotFound());
    }
}
