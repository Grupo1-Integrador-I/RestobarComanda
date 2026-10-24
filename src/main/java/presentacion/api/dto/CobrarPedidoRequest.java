package presentacion.api.dto;
import dominio.modelo.TipoPago;
import jakarta.validation.constraints.NotNull;
/**
 *
 * @author inici4rsesi0n
 */
public record CobrarPedidoRequest(
        @NotNull(message = "El tipo de pago no puede ser nulo") TipoPago tipo
){}
