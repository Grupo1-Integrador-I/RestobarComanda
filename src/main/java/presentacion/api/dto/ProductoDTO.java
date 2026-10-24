package presentacion.api.dto;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Producto;
import java.math.BigDecimal;
/**
 *
 * @author inici4rsesi0n
 */
public record ProductoDTO(
        Long id,
        String nombre,
        BigDecimal precio,
        CategoriaProducto categoria,
        boolean disponible
){
    public static ProductoDTO desde(Producto producto){
        return new ProductoDTO(producto.getId(), producto.getNombre(),
                producto.getPrecio().valor(), producto.getCategoria(),
                producto.estaDisponible());
    }
}
