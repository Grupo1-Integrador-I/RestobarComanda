package infraestructura.persistencia.repositorioJPA;
import dominio.modelo.CategoriaProducto;
import infraestructura.persistencia.entidad.EntidadProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioProductoJPA extends JpaRepository<EntidadProducto, Long>{
    Optional<EntidadProducto> findByNombre(String nombre);
    List<EntidadProducto> findByNombreContainingIgnoreCase(String texto);
    List<EntidadProducto> findByDisponibleTrue();
    List<EntidadProducto> findByDisponibleFalse();
    List<EntidadProducto> findByCategoria(CategoriaProducto categoria);
}
