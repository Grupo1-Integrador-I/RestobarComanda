package infraestructura.persistencia.adaptador;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Mesa;
import dominio.modelo.Pedido;
import dominio.puerto.repositorio.RepositorioPedido;
import infraestructura.persistencia.entidad.EntidadMesa;
import infraestructura.persistencia.mapper.MesaMapper;
import infraestructura.persistencia.mapper.PedidoMapper;
import infraestructura.persistencia.repositorioJPA.RepositorioPedidoJPA;
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
    public AdaptadorRepositorioPedido(RepositorioPedidoJPA jpa){
        this.jpa=jpa;
    }
    @Override
    public void agregar(Pedido pedido){
        jpa.save(PedidoMapper.aEntidad(pedido));
    }
    @Override
    public void actualizar(Pedido pedido){
        jpa.save(PedidoMapper.aEntidad(pedido));
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
}
