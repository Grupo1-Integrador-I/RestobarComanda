package aplicacion.casosdeuso;
import dominio.modelo.Mesa;
import dominio.modelo.Mesero;
import dominio.modelo.Pedido;
import dominio.modelo.Producto;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioMesa;
import dominio.puerto.repositorio.RepositorioPedido;
import dominio.puerto.repositorio.RepositorioProducto;
import dominio.reglas.ValidadorProducto;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class TomarPedido{
    private final RepositorioPedido repositorioPedido;
    private final RepositorioMesa repositorioMesa;
    private final RepositorioProducto repositorioProducto;
    private final LoggerPort logger;
    public TomarPedido(RepositorioPedido repositorioPedido,
                       RepositorioMesa repositorioMesa,
                       RepositorioProducto repositorioProducto,
                       LoggerPort logger){
        this.repositorioPedido=repositorioPedido;
        this.repositorioMesa=repositorioMesa;
        this.repositorioProducto=repositorioProducto;
        this.logger=logger;
    }
    public Pedido iniciar(Long mesaId, Mesero mesero){
        if(mesaId==null)
            throw new IllegalArgumentException("ID de mesa no puede ser nulo");
        if(mesero==null)
            throw new IllegalArgumentException("Mesero no puede ser nulo");
        Mesa mesa=repositorioMesa.buscarPorId(mesaId)
                .orElseThrow(()->new IllegalStateException("Mesa no encontrada"));
        Pedido pedido=new Pedido(mesa, mesero);
        if(mesa.estaLibre()){
            mesa.ocupar();
            repositorioMesa.actualizar(mesa);
        }
        repositorioPedido.agregar(pedido);
        logger.info("Pedido iniciado: {}", pedido);
        return pedido;
    }
    public void agregarItem(Long pedidoId, Long productoId, int cantidad){
        if(productoId==null)
            throw new IllegalArgumentException("ID de producto no puede ser nulo");
        Pedido pedido=obtenerPedidoOLanzar(pedidoId);
        Producto producto=repositorioProducto.buscarPorId(productoId)
                .orElseThrow(()->new IllegalStateException("Producto no encontrado"));
        ValidadorProducto.validarDisponible(producto);
        pedido.agregarItem(producto, cantidad);
        repositorioPedido.actualizar(pedido);
        logger.info("Item agregado al pedido {}: {} x{}", pedido.getId(), producto.getNombre(), cantidad);
    }
    public void eliminarItem(Long pedidoId, Long productoId){
        if(productoId==null)
            throw new IllegalArgumentException("ID de producto no puede ser nulo");
        Pedido pedido=obtenerPedidoOLanzar(pedidoId);
        Producto producto=repositorioProducto.buscarPorId(productoId)
                .orElseThrow(()->new IllegalStateException("Producto no encontrado"));
        pedido.eliminarItem(producto);
        repositorioPedido.actualizar(pedido);
        logger.info("Item eliminado del pedido {}: {}", pedido.getId(), producto.getNombre());
    }
    public void cancelar(Long pedidoId){
        Pedido pedido=obtenerPedidoOLanzar(pedidoId);
        pedido.cancelar();
        repositorioPedido.actualizar(pedido);
        logger.info("Pedido cancelado: {}", pedido);
    }
    public Pedido obtener(Long pedidoId){
        return obtenerPedidoOLanzar(pedidoId);
    }
    public List<Pedido> listarActivosPorMesa(Long mesaId){
        if(mesaId==null)
            throw new IllegalArgumentException("ID de mesa no puede ser nulo");
        Mesa mesa=repositorioMesa.buscarPorId(mesaId)
                .orElseThrow(()->new IllegalStateException("Mesa no encontrada"));
        return repositorioPedido.listarActivosPorMesa(mesa);
    }
    private Pedido obtenerPedidoOLanzar(Long id){
        if(id==null)
            throw new IllegalArgumentException("ID de pedido no puede ser nulo");
        return repositorioPedido.buscarPorId(id)
                .orElseThrow(()->new IllegalStateException("Pedido no encontrado"));
    }
}
