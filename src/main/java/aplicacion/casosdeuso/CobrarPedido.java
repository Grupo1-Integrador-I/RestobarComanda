package aplicacion.casosdeuso;
import dominio.excepcion.RecursoNoEncontradoException;
import dominio.modelo.Pago;
import dominio.modelo.Pedido;
import dominio.modelo.TipoPago;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.NotificadorPedido;
import dominio.puerto.repositorio.RepositorioPago;
import dominio.puerto.repositorio.RepositorioPedido;
import dominio.reglas.ValidadorPedido;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 *
 * @author inici4rsesi0n
 */
@Service
@Transactional
public class CobrarPedido{
    private final RepositorioPedido repositorioPedido;
    private final RepositorioPago repositorioPago;
    private final NotificadorPedido notificadorPedido;
    private final LoggerPort logger;
    public CobrarPedido(RepositorioPedido repositorioPedido,
                        RepositorioPago repositorioPago,
                        NotificadorPedido notificadorPedido,
                        LoggerPort logger){
        this.repositorioPedido=repositorioPedido;
        this.repositorioPago=repositorioPago;
        this.notificadorPedido=notificadorPedido;
        this.logger=logger;
    }
    public Pago ejecutar(Long pedidoId, TipoPago tipo){
        if(pedidoId==null)
            throw new IllegalArgumentException("ID de pedido no puede ser nulo");
        if(tipo==null)
            throw new IllegalArgumentException("Tipo de pago no puede ser nulo");
        Pedido pedido=repositorioPedido.buscarPorId(pedidoId)
                .orElseThrow(()->new RecursoNoEncontradoException("Pedido no encontrado"));
        ValidadorPedido.validarPuedeSerPagado(pedido);
        if(repositorioPago.buscarPorPedido(pedido).isPresent())
            throw new IllegalStateException("El pedido ya tiene un pago registrado");
        Pago pago=new Pago(pedido, tipo);
        pedido.marcarPagado();
        repositorioPedido.actualizar(pedido);
        repositorioPago.agregar(pago);
        notificadorPedido.notificarCambioEstado(pedido);
        logger.info("Pedido cobrado: {}", pedido);
        return pago;
    }
}
