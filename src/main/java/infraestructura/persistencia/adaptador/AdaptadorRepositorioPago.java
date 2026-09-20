package infraestructura.persistencia.adaptador;
import dominio.modelo.Pago;
import dominio.modelo.Pedido;
import dominio.puerto.repositorio.RepositorioPago;
import infraestructura.persistencia.mapper.PagoMapper;
import infraestructura.persistencia.mapper.PedidoMapper;
import infraestructura.persistencia.repositorioJPA.RepositorioPagoJPA;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioPago implements RepositorioPago{
    private final RepositorioPagoJPA jpa;
    public AdaptadorRepositorioPago(RepositorioPagoJPA jpa){
        this.jpa=jpa;
    }
    @Override
    public void agregar(Pago pago){
        jpa.save(PagoMapper.aEntidad(pago));
    }
    @Override
    public void actualizar(Pago pago){
        jpa.save(PagoMapper.aEntidad(pago));
    }
    @Override
    public Optional<Pago> buscarPorId(Long id){
        return jpa.findById(id).map(PagoMapper::aDominio);
    }
    @Override
    public List<Pago> listarTodos(){
        return jpa.findAll().stream().map(PagoMapper::aDominio).toList();
    }
    @Override
    public void eliminar(Pago pago){
        jpa.deleteById(pago.getId());
    }
    @Override
    public Optional<Pago> buscarPorPedido(Pedido pedido){
        return jpa.findByPedido(PedidoMapper.aEntidad(pedido)).map(PagoMapper::aDominio);
    }
}
