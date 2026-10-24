package presentacion.api.dto;
import jakarta.validation.constraints.NotBlank;
/**
 *
 * @author inici4rsesi0n
 */
public record MesaRequest(
        @NotBlank(message = "El número no puede ser nulo/vacío") String numero,
        @NotBlank(message = "La ubicación no puede ser nula/vacía") String ubicacion
){}
