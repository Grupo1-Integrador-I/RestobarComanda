package infraestructura.persistencia.repositorioJPA;
import dominio.modelo.EstadoMesa;
import infraestructura.persistencia.entidad.EntidadMesa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioMesaJPA extends JpaRepository<EntidadMesa, Long>{
    Optional<EntidadMesa> findByNumero(String numero);
    List<EntidadMesa> findByNumeroContainingIgnoreCase(String texto);
    List<EntidadMesa> findByEstado(EstadoMesa estado);
}
