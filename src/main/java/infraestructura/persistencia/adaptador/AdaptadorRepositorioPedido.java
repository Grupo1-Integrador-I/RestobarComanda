package infraestructura.persistencia.adaptador;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Mesa;
import dominio.modelo.Pedido;
import dominio.puerto.repositorio.RepositorioPedido;
import infraestructura.persistencia.entidad.EntidadMesa;
import infraestructura.persistencia.entidad.EntidadPedido;
import infraestructura.persistencia.entidad.EntidadProducto;
import infraestructura.persistencia.entidad.EntidadUsuario;
import infraestructura.persistencia.mapper.MesaMapper;
import infraestructura.persistencia.mapper.PedidoMapper;
import infraestructura.persistencia.repositorioJPA.RepositorioMesaJPA;
import infraestructura.persistencia.repositorioJPA.RepositorioPedidoJPA;
import infraestructura.persistencia.repositorioJPA.RepositorioProductoJPA;
import infraestructura.persistencia.repositorioJPA.RepositorioUsuarioJPA;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioPedido implements RepositorioPedido{
    private final RepositorioPedidoJPA jpa;
    private final RepositorioMesaJPA jpaMesa;
    private final RepositorioUsuarioJPA jpaUsuario;
    private final RepositorioProductoJPA jpaProducto;
    public AdaptadorRepositorioPedido(RepositorioPedidoJPA jpa,
                                       RepositorioMesaJPA jpaMesa,
                                       RepositorioUsuarioJPA jpaUsuario,
                                       RepositorioProductoJPA jpaProducto){
        this.jpa=jpa;
        this.jpaMesa=jpaMesa;
        this.jpaUsuario=jpaUsuario;
        this.jpaProducto=jpaProducto;
    }
    @Override
    public void agregar(Pedido pedido){
        EntidadPedido guardada=jpa.save(construirEntidad(pedido));
        pedido.setId(guardada.getId());
    }
    @Override
    public void actualizar(Pedido pedido){
        jpa.save(construirEntidad(pedido));
    }
    @Override
    public Optional<Pedido> buscarPorId(Long id){
        return jpa.findById(id).map(PedidoMapper::aDominio);
    }
    @Override
    public List<Pedido> listarTodos(){
        return jpa.findAll().stream().map(PedidoMapper::aDominio).toList();
    }
    @Override
    public void eliminar(Pedido pedido){
        jpa.deleteById(pedido.getId());
    }
    @Override
    public List<Pedido> listarPorMesa(Mesa mesa){
        EntidadMesa em=MesaMapper.aEntidad(mesa);
        return jpa.findByMesa(em).stream().map(PedidoMapper::aDominio).toList();
    }
    @Override
    public List<Pedido> listarPorEstado(EstadoPedido estado){
        return jpa.findByEstado(estado).stream().map(PedidoMapper::aDominio).toList();
    }
    @Override
    public List<Pedido> listarActivosPorMesa(Mesa mesa){
        EntidadMesa em=MesaMapper.aEntidad(mesa);
        List<EstadoPedido> finalizados=List.of(EstadoPedido.PAGADO, EstadoPedido.CANCELADO);
        return jpa.findByMesaAndEstadoNotIn(em, finalizados).stream()
                .map(PedidoMapper::aDominio).toList();
    }
    @Override
    public List<Pedido> listarPorMesaYEstado(Mesa mesa, EstadoPedido estado){
        EntidadMesa em=MesaMapper.aEntidad(mesa);
        return jpa.findByMesaAndEstado(em, estado).stream()
                .map(PedidoMapper::aDominio).toList();
    }
    private EntidadPedido construirEntidad(Pedido pedido){
        EntidadMesa mesaManaged=jpaMesa.findById(pedido.getMesa().getId())
                .orElseThrow(()->new IllegalStateException("Mesa no encontrada en BD"));
        EntidadUsuario meseroManaged=jpaUsuario.findById(pedido.getMesero().getId())
                .orElseThrow(()->new IllegalStateException("Mesero no encontrado en BD"));
        List<EntidadProducto> productosManaged=pedido.getItems().stream()
                .map(item->jpaProducto.findById(item.getProducto().getId())
                        .orElseThrow(()->new IllegalStateException("Producto no encontrado en BD")))
                .toList();
        return PedidoMapper.aEntidad(pedido, mesaManaged, meseroManaged, productosManaged);
    }
}
