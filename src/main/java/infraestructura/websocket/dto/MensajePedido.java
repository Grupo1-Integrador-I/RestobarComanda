package infraestructura.websocket.dto;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.EstadoPedido;
import dominio.modelo.ItemPedido;
import dominio.modelo.Pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
public record MensajePedido(
        Long id,
        String numeroMesa,
        String nombreMesero,
        EstadoPedido estado,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        List<ItemMensaje> items
){
    public record ItemMensaje(
            String nombreProducto,
            CategoriaProducto categoria,
            int cantidad,
            BigDecimal subtotal
    ){}
    public static MensajePedido desde(Pedido p){
        List<ItemMensaje> items=new ArrayList<>();
        for(ItemPedido i:p.getItems()){
            items.add(new ItemMensaje(i.getProducto().getNombre(),
                    i.getProducto().getCategoria(),
                    i.getCantidad(),
                    i.getSubtotal().valor()));
        }
        return new MensajePedido(p.getId(), p.getMesa().getNumero(),
                p.getMesero().getNombre()+" "+p.getMesero().getApellido(),
                p.getEstado(), p.getTotal().valor(),
                p.getFechaCreacion(), items);
    }
    public List<ItemMensaje> itemsDeCategoria(CategoriaProducto categoria){
        List<ItemMensaje> filtrados=new ArrayList<>();
        for(ItemMensaje i:items){
            if(i.categoria()==categoria)
                filtrados.add(i);
        }
        return filtrados;
    }
}
