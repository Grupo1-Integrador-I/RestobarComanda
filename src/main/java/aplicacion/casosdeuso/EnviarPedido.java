package aplicacion.casosdeuso;
import dominio.modelo.Pedido;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.NotificadorPedido;
import dominio.puerto.repositorio.RepositorioPedido;
import org.springframework.stereotype.Service;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class EnviarPedido{
    private final RepositorioPedido repositorioPedido;
    private final NotificadorPedido notificadorPedido;
    private final LoggerPort logger;
    public EnviarPedido(RepositorioPedido repositorioPedido,
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
                .orElseThrow(()->new IllegalStateException("Pedido no encontrado"));
        pedido.enviar();
        repositorioPedido.actualizar(pedido);
        notificadorPedido.notificarNuevoPedido(pedido);
        logger.info("Pedido enviado: {}", pedido);
    }
}
