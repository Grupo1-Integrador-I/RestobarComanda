package presentacion.api.dto;
import jakarta.validation.constraints.NotBlank;
/**
 *
 * @author inici4rsesi0n
 */
public record LoginRequest(
        @NotBlank(message = "El código no puede ser nulo/vacío") String codigo,
        @NotBlank(message = "La contraseña no puede ser nula/vacía") String password
){}
