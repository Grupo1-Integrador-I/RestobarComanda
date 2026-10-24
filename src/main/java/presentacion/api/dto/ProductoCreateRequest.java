package presentacion.api.dto;
import dominio.modelo.CategoriaProducto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
/**
 *
 * @author inici4rsesi0n
 */
public record ProductoCreateRequest(
        @NotBlank(message = "El nombre no puede ser nulo/vacío") String nombre,
        @NotNull(message = "El precio no puede ser nulo")
        @DecimalMin(value = "0.01", message = "El precio debe ser mayor a cero") BigDecimal precio,
        @NotNull(message = "La categoría no puede ser nula") CategoriaProducto categoria
){}
