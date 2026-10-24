package infraestructura.persistencia.mapper;
import dominio.modelo.ItemPedido;
import dominio.modelo.Producto;
import infraestructura.persistencia.entidad.EntidadItemPedido;
import infraestructura.persistencia.entidad.EntidadPedido;
import infraestructura.persistencia.entidad.EntidadProducto;
/**
 *
 * @author inici4rsesi0n
 */
public class ItemPedidoMapper{
    private ItemPedidoMapper(){}
    public static ItemPedido aDominio(EntidadItemPedido e){
        if(e==null)
            return null;
        Producto producto=ProductoMapper.aDominio(e.getProducto());
        ItemPedido item=new ItemPedido(producto, e.getCantidad());
        item.setId(e.getId());
        return item;
    }
    public static EntidadItemPedido aEntidad(ItemPedido item, EntidadPedido parent,
                                              EntidadProducto productoManaged){
        if(item==null)
            return null;
        EntidadItemPedido e=new EntidadItemPedido();
        e.setId(item.getId());
        e.setPedido(parent);
        e.setProducto(productoManaged);
        e.setCantidad(item.getCantidad());
        e.setSubtotal(item.getSubtotal().valor());
        return e;
    }
}
