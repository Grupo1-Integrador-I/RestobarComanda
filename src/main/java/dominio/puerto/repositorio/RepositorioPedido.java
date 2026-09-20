package dominio.puerto.repositorio;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Mesa;
import dominio.modelo.Pedido;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioPedido extends Repositorio<Pedido>{
    List<Pedido> listarPorMesa(Mesa mesa);
    List<Pedido> listarPorEstado(EstadoPedido estado);
    List<Pedido> listarActivosPorMesa(Mesa mesa);
    List<Pedido> listarPorMesaYEstado(Mesa mesa, EstadoPedido estado);
}
