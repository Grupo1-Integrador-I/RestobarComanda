package infraestructura.persistencia.repositorioJPA;
import dominio.modelo.EstadoPedido;
import infraestructura.persistencia.entidad.EntidadMesa;
import infraestructura.persistencia.entidad.EntidadPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioPedidoJPA extends JpaRepository<EntidadPedido, Long>{
    List<EntidadPedido> findByMesa(EntidadMesa mesa);
    List<EntidadPedido> findByEstado(EstadoPedido estado);
    List<EntidadPedido> findByMesaAndEstado(EntidadMesa mesa, EstadoPedido estado);
    List<EntidadPedido> findByMesaAndEstadoNotIn(EntidadMesa mesa,
                                                  List<EstadoPedido> estados);
}
