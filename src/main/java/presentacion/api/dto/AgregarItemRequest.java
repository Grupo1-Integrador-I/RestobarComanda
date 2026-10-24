package presentacion.api.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
/**
 *
 * @author inici4rsesi0n
 */
public record AgregarItemRequest(
        @NotNull(message = "El ID de producto no puede ser nulo") Long productoId,
        @Min(value = 1, message = "La cantidad debe ser mayor a cero") int cantidad
){}
