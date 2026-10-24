package presentacion.api.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
/**
 *
 * @author inici4rsesi0n
 */
public record PasswordChangeRequest(
        @NotBlank(message = "La contraseña no puede ser nula/vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password
){}
