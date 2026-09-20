package infraestructura.persistencia.repositorioJPA;
import infraestructura.persistencia.entidad.EntidadUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioUsuarioJPA extends JpaRepository<EntidadUsuario, Long>{
    Optional<EntidadUsuario> findByCodigo(String codigo);
    Optional<EntidadUsuario> findByDni(String dni);
    List<EntidadUsuario> findByNombreContainingIgnoreCase(String texto);
    List<EntidadUsuario> findByApellidoContainingIgnoreCase(String texto);
    List<EntidadUsuario> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(
            String nombre, String apellido);
}
