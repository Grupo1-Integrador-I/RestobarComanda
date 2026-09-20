package dominio.puerto.repositorio;
import dominio.modelo.Pago;
import dominio.modelo.Pedido;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioPago extends Repositorio<Pago>{
    Optional<Pago> buscarPorPedido(Pedido pedido);
}
