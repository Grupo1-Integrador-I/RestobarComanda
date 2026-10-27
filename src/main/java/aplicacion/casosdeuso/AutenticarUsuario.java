package aplicacion.casosdeuso;
import dominio.modelo.Usuario;
import dominio.puerto.externo.HashProvider;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioUsuario;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class AutenticarUsuario{
    private final RepositorioUsuario repositorioUsuario;
    private final HashProvider hashProvider;
    private final LoggerPort logger;
    public AutenticarUsuario(RepositorioUsuario repositorioUsuario,
                             HashProvider hashProvider,
                             LoggerPort logger){
        this.repositorioUsuario=repositorioUsuario;
        this.hashProvider=hashProvider;
        this.logger=logger;
    }
    public Usuario ejecutar(String codigo, char[] contrasena){
        if(codigo==null||codigo.isBlank())
            throw new IllegalArgumentException("Código no puede ser nulo/vacío");
        if(contrasena==null||contrasena.length==0)
            throw new IllegalArgumentException("Contraseña no puede ser nula/vacía");
        try{
            Optional<Usuario> encontrado=repositorioUsuario.buscarPorCodigo(codigo);
            if(encontrado.isEmpty()){
                logger.warn("Intento de login fallido: código '{}' no existe", codigo);
                throw new IllegalStateException("Credenciales inválidas");
            }
            Usuario usuario=encontrado.get();
            boolean valido=hashProvider.verificarHash(usuario.getHashContrasena(), contrasena);
            if(!valido){
                logger.warn("Intento de login fallido para usuario '{}'", codigo);
                throw new IllegalStateException("Credenciales inválidas");
            }
            logger.info("Login exitoso: {}", usuario);
            return usuario;
        }finally{
            Arrays.fill(contrasena, '\0');
        }
    }
}
