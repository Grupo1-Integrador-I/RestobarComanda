package aplicacion.casosdeuso;
import dominio.modelo.Mesa;
import dominio.modelo.Pedido;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioMesa;
import dominio.puerto.repositorio.RepositorioPedido;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class LiberarMesa{
    private final RepositorioMesa repositorioMesa;
    private final RepositorioPedido repositorioPedido;
    private final LoggerPort logger;
    public LiberarMesa(RepositorioMesa repositorioMesa,
                       RepositorioPedido repositorioPedido,
                       LoggerPort logger){
        this.repositorioMesa=repositorioMesa;
        this.repositorioPedido=repositorioPedido;
        this.logger=logger;
    }
    public void ejecutar(Long mesaId){
        if(mesaId==null)
            throw new IllegalArgumentException("ID de mesa no puede ser nulo");
        Mesa mesa=repositorioMesa.buscarPorId(mesaId)
                .orElseThrow(()->new IllegalStateException("Mesa no encontrada"));
        List<Pedido> pedidosActivos=repositorioPedido.listarActivosPorMesa(mesa);
        if(!pedidosActivos.isEmpty())
            throw new IllegalStateException("No se puede liberar la mesa: tiene " 
                    + pedidosActivos.size() + " pedido(s) activo(s)");
        mesa.liberar();
        repositorioMesa.actualizar(mesa);
        logger.info("Mesa liberada: {}", mesa);
    }
}
