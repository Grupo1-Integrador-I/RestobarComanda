
package presentacion.controlador;

import dominio.modelo.Usuario;
import dominio.puerto.repositorio.RepositorioUsuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RepositorioUsuario repositorioUsuario;

    public AuthController(
            AuthenticationManager authenticationManager,
            RepositorioUsuario repositorioUsuario) {

        this.authenticationManager = authenticationManager;
        this.repositorioUsuario = repositorioUsuario;
    }

    public record LoginRequest(
            String codigo,
            String contrasena
    ) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest datos,
            HttpServletRequest request) {

        if (datos == null
                || datos.codigo() == null
                || datos.codigo().isBlank()
                || datos.contrasena() == null
                || datos.contrasena().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "mensaje",
                            "Ingrese código y contraseña"
                    ));
        }

        try {
            Authentication autenticacion =
                    authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    datos.codigo(),
                                    datos.contrasena()
                            )
                    );

            // Obtener o crear la sesión HTTP
HttpSession sesion = request.getSession(true);

// Cambiar el ID de sesión por seguridad
request.changeSessionId();

SecurityContext contexto =
        SecurityContextHolder.createEmptyContext();

            contexto.setAuthentication(autenticacion);
            SecurityContextHolder.setContext(contexto);

            

            sesion.setAttribute(
                    HttpSessionSecurityContextRepository
                            .SPRING_SECURITY_CONTEXT_KEY,
                    contexto
            );

            Usuario usuario = repositorioUsuario
                    .buscarPorCodigo(autenticacion.getName())
                    .orElseThrow();

            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "codigo", usuario.getCodigo(),
                    "nombre", usuario.getNombre(),
                    "apellido", usuario.getApellido(),
                    "rol", usuario.getRol().name()
            ));

        } catch (org.springframework.security.core
                .AuthenticationException ex) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje",
                            "Código o contraseña incorrectos"
                    ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> usuarioActual(
            Authentication autenticacion) {

        if (autenticacion == null
                || !autenticacion.isAuthenticated()
                || "anonymousUser".equals(
                        autenticacion.getPrincipal())) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensaje",
                            "No ha iniciado sesión"
                    ));
        }

        return repositorioUsuario
                .buscarPorCodigo(autenticacion.getName())
                .<ResponseEntity<?>>map(usuario ->
                        ResponseEntity.ok(Map.of(
                                "id", usuario.getId(),
                                "codigo", usuario.getCodigo(),
                                "nombre", usuario.getNombre(),
                                "apellido", usuario.getApellido(),
                                "rol", usuario.getRol().name()
                        ))
                )
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(Map.of(
                                        "mensaje",
                                        "Usuario no encontrado"
                                ))
                );
    }
}
