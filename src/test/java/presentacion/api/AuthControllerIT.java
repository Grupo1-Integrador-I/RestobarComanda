package presentacion.api;
import boot.RestobarApp;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
class AuthControllerIT{
    @Autowired MockMvc mockMvc;
    @Test
    void loginExitosoDevuelveBodyYSesionConSecurityContext() throws Exception{
        MvcResult result=mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("admin"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.mensaje").value("Login exitoso"))
                .andReturn();
        MockHttpSession session=(MockHttpSession)result.getRequest().getSession(false);
        assertNotNull(session);
        Object contexto=session.getAttribute("SPRING_SECURITY_CONTEXT");
        assertNotNull(contexto);
        assertTrue(contexto instanceof SecurityContext);
    }
    @Test
    void loginPasswordIncorrectaDevuelve401() throws Exception{
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"admin\",\"password\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }
    @Test
    void loginUsuarioInexistenteDevuelve401() throws Exception{
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"nadie\",\"password\":\"cualquiera\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }
    @Test
    void loginBodyVacioDevuelve400() throws Exception{
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
    @Test
    void loginJsonMalformadoDevuelve400() throws Exception{
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("JSON malformado"));
    }
    @Test
    void logoutTrasLoginEsAceptado() throws Exception{
        MvcResult loginResult=mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session=(MockHttpSession)loginResult.getRequest().getSession();
        assertNotNull(session);
        mockMvc.perform(post("/api/auth/logout").session(session))
                .andExpect(status().is3xxRedirection());
    }
    @Test
    void meConSesionDevuelveUsuarioActual() throws Exception{
        MvcResult loginResult=mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        MockHttpSession session=(MockHttpSession)loginResult.getRequest().getSession();
        assertNotNull(session);
        mockMvc.perform(get("/api/auth/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value("admin"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"))
                .andExpect(jsonPath("$.mensaje").value("Sesión activa"));
    }
    @Test
    void meSinSesionDevuelve401() throws Exception{
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
