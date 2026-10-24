package presentacion.api.dto;
import jakarta.validation.constraints.NotNull;
/**
 *
 * @author inici4rsesi0n
 */
public record IniciarPedidoRequest(
        @NotNull(message = "El ID de mesa no puede ser nulo") Long mesaId
){}
