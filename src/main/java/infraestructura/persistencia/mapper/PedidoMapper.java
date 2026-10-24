package infraestructura.persistencia.mapper;
import dominio.modelo.ItemPedido;
import dominio.modelo.Mesa;
import dominio.modelo.Mesero;
import dominio.modelo.Monto;
import dominio.modelo.Pedido;
import dominio.modelo.Usuario;
import infraestructura.persistencia.entidad.EntidadItemPedido;
import infraestructura.persistencia.entidad.EntidadMesa;
import infraestructura.persistencia.entidad.EntidadPedido;
import infraestructura.persistencia.entidad.EntidadProducto;
import infraestructura.persistencia.entidad.EntidadUsuario;
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
    public static EntidadPedido aEntidad(Pedido p, EntidadMesa mesaManaged,
                                          EntidadUsuario meseroManaged,
                                          List<EntidadProducto> productosManaged){
        if(p==null)
            return null;
        EntidadPedido e=new EntidadPedido();
        e.setId(p.getId());
        e.setMesa(mesaManaged);
        e.setMesero(meseroManaged);
        e.setTotal(p.getTotal().valor());
        e.setEstado(p.getEstado());
        e.setFechaCreacion(p.getFechaCreacion());
        List<ItemPedido> items=p.getItems();
        for(int i=0;i<items.size();i++){
            e.getItems().add(ItemPedidoMapper.aEntidad(items.get(i), e, productosManaged.get(i)));
        }
        return e;
    }
}
