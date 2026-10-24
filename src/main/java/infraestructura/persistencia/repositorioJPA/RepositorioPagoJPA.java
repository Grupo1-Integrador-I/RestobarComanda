package infraestructura.persistencia.repositorioJPA;
import infraestructura.persistencia.entidad.EntidadPago;
import infraestructura.persistencia.entidad.EntidadPedido;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioPagoJPA extends JpaRepository<EntidadPago, Long>{
    @Override
    @EntityGraph(attributePaths={"pedido", "pedido.items", "pedido.items.producto",
                                 "pedido.mesa", "pedido.mesero"})
    Optional<EntidadPago> findById(Long id);
    @Override
    @EntityGraph(attributePaths={"pedido", "pedido.items", "pedido.items.producto",
                                 "pedido.mesa", "pedido.mesero"})
    List<EntidadPago> findAll();
    @EntityGraph(attributePaths={"pedido", "pedido.items", "pedido.items.producto",
                                 "pedido.mesa", "pedido.mesero"})
    Optional<EntidadPago> findByPedido(EntidadPedido pedido);
}
