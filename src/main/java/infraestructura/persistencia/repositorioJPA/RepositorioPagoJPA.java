package infraestructura.persistencia.repositorioJPA;
import infraestructura.persistencia.entidad.EntidadPago;
import infraestructura.persistencia.entidad.EntidadPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioPagoJPA extends JpaRepository<EntidadPago, Long>{
    Optional<EntidadPago> findByPedido(EntidadPedido pedido);
}
