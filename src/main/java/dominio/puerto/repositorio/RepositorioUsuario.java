package dominio.puerto.repositorio;
import dominio.modelo.Usuario;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioUsuario extends Repositorio<Usuario>{
    Optional<Usuario> buscarPorCodigo(String codigo);
    Optional<Usuario> buscarPorDni(String dni);
    List<Usuario> buscarPorNombreContiene(String texto);
    List<Usuario> buscarPorApellidoContiene(String texto);
    List<Usuario> buscarPorNombreOApellidoContiene(String texto);
}
