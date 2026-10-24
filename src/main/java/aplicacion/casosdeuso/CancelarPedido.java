package aplicacion.casosdeuso;
import dominio.excepcion.RecursoNoEncontradoException;
import dominio.modelo.Mesa;
import dominio.modelo.Pedido;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.externo.NotificadorPedido;
import dominio.puerto.repositorio.RepositorioMesa;
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
public class CancelarPedido{
    private final RepositorioPedido repositorioPedido;
    private final RepositorioMesa repositorioMesa;
    private final NotificadorPedido notificadorPedido;
    private final LoggerPort logger;
    public CancelarPedido(RepositorioPedido repositorioPedido,
                          RepositorioMesa repositorioMesa,
                          NotificadorPedido notificadorPedido,
                          LoggerPort logger){
        this.repositorioPedido=repositorioPedido;
        this.repositorioMesa=repositorioMesa;
        this.notificadorPedido=notificadorPedido;
        this.logger=logger;
    }
    public void ejecutar(Long pedidoId){
        if(pedidoId==null)
            throw new IllegalArgumentException("ID de pedido no puede ser nulo");
        Pedido pedido=repositorioPedido.buscarPorId(pedidoId)
                .orElseThrow(()->new RecursoNoEncontradoException("Pedido no encontrado"));
        Mesa mesa=pedido.getMesa();
        pedido.cancelar();
        repositorioPedido.actualizar(pedido);
        liberarMesaSiCorresponde(mesa);
        notificadorPedido.notificarCambioEstado(pedido);
        logger.info("Pedido cancelado: {}", pedido);
    }
    private void liberarMesaSiCorresponde(Mesa mesa){
        List<Pedido> activos=repositorioPedido.listarActivosPorMesa(mesa);
        if(activos.isEmpty()&&!mesa.estaLibre()){
            mesa.liberar();
            repositorioMesa.actualizar(mesa);
            logger.info("Mesa liberada tras cancelación: {}", mesa);
        }
    }
}
