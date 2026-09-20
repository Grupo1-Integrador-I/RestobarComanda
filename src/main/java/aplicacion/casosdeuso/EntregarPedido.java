package aplicacion.casosdeuso;
import dominio.modelo.Pedido;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioPedido;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class EntregarPedido{
    private final RepositorioPedido repositorioPedido;
    private final LoggerPort logger;
    public EntregarPedido(RepositorioPedido repositorioPedido, LoggerPort logger){
        this.repositorioPedido=repositorioPedido;
        this.logger=logger;
    }
    public void ejecutar(Long pedidoId){
        if(pedidoId==null)
            throw new IllegalArgumentException("ID de pedido no puede ser nulo");
        Pedido pedido=repositorioPedido.buscarPorId(pedidoId)
                .orElseThrow(()->new IllegalStateException("Pedido no encontrado"));
        pedido.marcarEntregado();
        repositorioPedido.actualizar(pedido);
        logger.info("Pedido entregado: {}", pedido);
    }
    public List<Pedido> listarListos(){
        return repositorioPedido.listarPorEstado(dominio.modelo.EstadoPedido.LISTO);
    }
}
