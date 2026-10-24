package aplicacion.casosdeuso;
import dominio.excepcion.RecursoNoEncontradoException;
import dominio.modelo.Pedido;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.NotificadorPedido;
import dominio.puerto.repositorio.RepositorioPedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
@Transactional
public class EntregarPedido{
    private final RepositorioPedido repositorioPedido;
    private final NotificadorPedido notificadorPedido;
    private final LoggerPort logger;
    public EntregarPedido(RepositorioPedido repositorioPedido,
                          NotificadorPedido notificadorPedido,
                          LoggerPort logger){
        this.repositorioPedido=repositorioPedido;
        this.notificadorPedido=notificadorPedido;
        this.logger=logger;
    }
    public void ejecutar(Long pedidoId){
        if(pedidoId==null)
            throw new IllegalArgumentException("ID de pedido no puede ser nulo");
        Pedido pedido=repositorioPedido.buscarPorId(pedidoId)
                .orElseThrow(()->new RecursoNoEncontradoException("Pedido no encontrado"));
        pedido.marcarEntregado();
        repositorioPedido.actualizar(pedido);
        notificadorPedido.notificarCambioEstado(pedido);
        logger.info("Pedido entregado: {}", pedido);
    }
    public List<Pedido> listarListos(){
        return repositorioPedido.listarPorEstado(dominio.modelo.EstadoPedido.LISTO);
    }
}
