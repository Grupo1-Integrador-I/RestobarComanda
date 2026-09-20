package dominio.puerto.externo;
import dominio.modelo.Pedido;
/**
 * Puerto para notificar cambios en pedidos a las pantallas conectadas en tiempo real.
 * Implementado en infraestructura mediante WebSocket (STOMP).
 *
 * @author inici4rsesi0n
 */
public interface NotificadorPedido {
    /**
     * Notifica que un nuevo pedido fue enviado a preparación.
     * El adaptador se encarga de enrutar los ítems a cocina o barra según su categoría.
     * @param pedido pedido recién enviado
     */
    void notificarNuevoPedido(Pedido pedido);
    /**
     * Notifica que un pedido cambió de estado (en preparación, listo, entregado, pagado).
     * Permite que las pantallas de los meseros reflejen el avance en tiempo real.
     * @param pedido pedido actualizado
     */
    void notificarCambioEstado(Pedido pedido);
}
