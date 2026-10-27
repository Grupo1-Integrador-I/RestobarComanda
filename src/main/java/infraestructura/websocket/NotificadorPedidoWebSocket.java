package infraestructura.websocket;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Pedido;
import dominio.puerto.externo.NotificadorPedido;
import infraestructura.websocket.dto.MensajePedido;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
public class NotificadorPedidoWebSocket implements NotificadorPedido{
    private final SimpMessagingTemplate messagingTemplate;
    public NotificadorPedidoWebSocket(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate=messagingTemplate;
    }
    @Override
    public void notificarNuevoPedido(Pedido pedido){
        MensajePedido cocina=filtrarPorCategoria(pedido, CategoriaProducto.COCINA);
        if(!cocina.items().isEmpty())
            messagingTemplate.convertAndSend("/topic/cocina", cocina);
        MensajePedido barra=filtrarPorCategoria(pedido, CategoriaProducto.BARRA);
        if(!barra.items().isEmpty())
            messagingTemplate.convertAndSend("/topic/barra", barra);
    }
    @Override
    public void notificarCambioEstado(Pedido pedido){
        messagingTemplate.convertAndSend("/topic/meseros", MensajePedido.desde(pedido));
    }
    private MensajePedido filtrarPorCategoria(Pedido pedido, CategoriaProducto categoria){
        MensajePedido completo=MensajePedido.desde(pedido);
        return new MensajePedido(completo.id(), completo.numeroMesa(),
                completo.nombreMesero(), completo.estado(), completo.total(),
                completo.fechaCreacion(), completo.itemsDeCategoria(categoria));
    }
}
