package infraestructura.persistencia.adaptador;
import dominio.modelo.Pago;
import dominio.modelo.Pedido;
import dominio.puerto.repositorio.RepositorioPago;
import infraestructura.persistencia.entidad.EntidadPedido;
import infraestructura.persistencia.mapper.PagoMapper;
import infraestructura.persistencia.repositorioJPA.RepositorioPagoJPA;
import infraestructura.persistencia.repositorioJPA.RepositorioPedidoJPA;
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
    private final RepositorioPedidoJPA jpaPedido;
    public AdaptadorRepositorioPago(RepositorioPagoJPA jpa,
                                     RepositorioPedidoJPA jpaPedido){
        this.jpa=jpa;
        this.jpaPedido=jpaPedido;
    }
    @Override
    public void agregar(Pago pago){
        EntidadPedido pedidoManaged=cargarPedido(pago);
        var guardada=jpa.save(PagoMapper.aEntidad(pago, pedidoManaged));
        pago.setId(guardada.getId());
    }
    @Override
    public void actualizar(Pago pago){
        EntidadPedido pedidoManaged=cargarPedido(pago);
        jpa.save(PagoMapper.aEntidad(pago, pedidoManaged));
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
        EntidadPedido ep=jpaPedido.findById(pedido.getId())
                .orElseThrow(()->new IllegalStateException("Pedido no encontrado"));
        return jpa.findByPedido(ep).map(PagoMapper::aDominio);
    }
    private EntidadPedido cargarPedido(Pago pago){
        if(pago.getPedido()==null||pago.getPedido().getId()==null)
            throw new IllegalStateException("El pago no tiene un pedido con ID asignado");
        return jpaPedido.findById(pago.getPedido().getId())
                .orElseThrow(()->new IllegalStateException("Pedido no encontrado"));
    }
}
