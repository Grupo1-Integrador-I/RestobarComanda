package aplicacion.casosdeuso;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Pedido;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.NotificadorPedido;
import dominio.puerto.repositorio.RepositorioPedido;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class ActualizarEstadoPedido{
    private final RepositorioPedido repositorioPedido;
    private final NotificadorPedido notificadorPedido;
    private final LoggerPort logger;
    public ActualizarEstadoPedido(RepositorioPedido repositorioPedido,
                                  NotificadorPedido notificadorPedido,
                                  LoggerPort logger){
        this.repositorioPedido=repositorioPedido;
        this.notificadorPedido=notificadorPedido;
        this.logger=logger;
    }
    public void marcarEnPreparacion(Long pedidoId){
        Pedido pedido=obtenerPedidoOLanzar(pedidoId);
        pedido.marcarEnPreparacion();
        repositorioPedido.actualizar(pedido);
        notificadorPedido.notificarCambioEstado(pedido);
        logger.info("Pedido en preparación: {}", pedido);
    }
    public void marcarListo(Long pedidoId){
        Pedido pedido=obtenerPedidoOLanzar(pedidoId);
        pedido.marcarListo();
        repositorioPedido.actualizar(pedido);
        notificadorPedido.notificarCambioEstado(pedido);
        logger.info("Pedido listo para servir: {}", pedido);
    }
    public List<Pedido> listarPendientes(){
        return repositorioPedido.listarPorEstado(EstadoPedido.ENVIADO);
    }
    public List<Pedido> listarEnPreparacion(){
        return repositorioPedido.listarPorEstado(EstadoPedido.EN_PREPARACION);
    }
    private Pedido obtenerPedidoOLanzar(Long id){
        if(id==null)
            throw new IllegalArgumentException("ID de pedido no puede ser nulo");
        return repositorioPedido.buscarPorId(id)
                .orElseThrow(()->new IllegalStateException("Pedido no encontrado"));
    }
}
