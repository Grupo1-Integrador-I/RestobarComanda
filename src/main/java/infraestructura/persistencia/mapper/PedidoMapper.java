package infraestructura.persistencia.mapper;
import dominio.modelo.ItemPedido;
import dominio.modelo.Mesa;
import dominio.modelo.Mesero;
import dominio.modelo.Monto;
import dominio.modelo.Pedido;
import dominio.modelo.Usuario;
import infraestructura.persistencia.entidad.EntidadItemPedido;
import infraestructura.persistencia.entidad.EntidadPedido;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
public class PedidoMapper{
    private PedidoMapper(){}
    public static Pedido aDominio(EntidadPedido e){
        if(e==null)
            return null;
        Mesa mesa=MesaMapper.aDominio(e.getMesa());
        Usuario usuario=UsuarioMapper.aDominio(e.getMesero());
        Mesero mesero=(Mesero)usuario;
        List<ItemPedido> items=new ArrayList<>();
        for(EntidadItemPedido ei:e.getItems()){
            items.add(ItemPedidoMapper.aDominio(ei));
        }
        return Pedido.reconstruir(mesa, mesero, items,
                new Monto(e.getTotal()), e.getEstado(),
                e.getFechaCreacion(), e.getId());
    }
    public static EntidadPedido aEntidad(Pedido p){
        if(p==null)
            return null;
        EntidadPedido e=new EntidadPedido();
        e.setId(p.getId());
        e.setMesa(MesaMapper.aEntidad(p.getMesa()));
        e.setMesero(UsuarioMapper.aEntidad(p.getMesero()));
        e.setTotal(p.getTotal().valor());
        e.setEstado(p.getEstado());
        e.setFechaCreacion(p.getFechaCreacion());
        for(ItemPedido item:p.getItems()){
            e.getItems().add(ItemPedidoMapper.aEntidad(item, e));
        }
        return e;
    }
}
