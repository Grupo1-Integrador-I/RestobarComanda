package infraestructura.persistencia.repositorioJPA;
import dominio.modelo.EstadoPedido;
import infraestructura.persistencia.entidad.EntidadMesa;
import infraestructura.persistencia.entidad.EntidadPedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioPedidoJPA extends JpaRepository<EntidadPedido, Long>{
    @Override
    @EntityGraph(attributePaths={"items", "items.producto", "mesa", "mesero"})
    Optional<EntidadPedido> findById(Long id);
    @Override
    @EntityGraph(attributePaths={"items", "items.producto", "mesa", "mesero"})
    List<EntidadPedido> findAll();
    @EntityGraph(attributePaths={"items", "items.producto", "mesa", "mesero"})
    List<EntidadPedido> findByMesa(EntidadMesa mesa);
    @EntityGraph(attributePaths={"items", "items.producto", "mesa", "mesero"})
    List<EntidadPedido> findByEstado(EstadoPedido estado);
    @EntityGraph(attributePaths={"items", "items.producto", "mesa", "mesero"})
    List<EntidadPedido> findByMesaAndEstado(EntidadMesa mesa, EstadoPedido estado);
    @EntityGraph(attributePaths={"items", "items.producto", "mesa", "mesero"})
    List<EntidadPedido> findByMesaAndEstadoNotIn(EntidadMesa mesa,
                                                  List<EstadoPedido> estados);
}
