package presentacion.api.dto;
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
public record PedidoDTO(
        Long id,
        Long mesaId,
        String numeroMesa,
        Long meseroId,
        String nombreMesero,
        EstadoPedido estado,
        BigDecimal total,
        LocalDateTime fechaCreacion,
        List<ItemDTO> items
){
    public record ItemDTO(
            Long productoId,
            String nombreProducto,
            CategoriaProducto categoria,
            int cantidad,
            BigDecimal subtotal
    ){}
    public static PedidoDTO desde(Pedido pedido){
        List<ItemDTO> items=new ArrayList<>();
        for(ItemPedido item:pedido.getItems()){
            items.add(new ItemDTO(item.getProducto().getId(),
                    item.getProducto().getNombre(),
                    item.getProducto().getCategoria(),
                    item.getCantidad(),
                    item.getSubtotal().valor()));
        }
        return new PedidoDTO(pedido.getId(), pedido.getMesa().getId(),
                pedido.getMesa().getNumero(), pedido.getMesero().getId(),
                pedido.getMesero().getNombre()+" "+pedido.getMesero().getApellido(),
                pedido.getEstado(), pedido.getTotal().valor(),
                pedido.getFechaCreacion(), items);
    }
    public static PedidoDTO desdeFiltradoPorCategoria(Pedido pedido, CategoriaProducto categoria){
        PedidoDTO completo=desde(pedido);
        List<ItemDTO> filtrados=new ArrayList<>();
        for(ItemDTO item:completo.items()){
            if(item.categoria()==categoria)
                filtrados.add(item);
        }
        return new PedidoDTO(completo.id(), completo.mesaId(), completo.numeroMesa(),
                completo.meseroId(), completo.nombreMesero(), completo.estado(),
                completo.total(), completo.fechaCreacion(), filtrados);
    }
}
