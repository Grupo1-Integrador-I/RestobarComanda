package presentacion.api.dto;
import jakarta.validation.constraints.NotBlank;
/**
 *
 * @author inici4rsesi0n
 */
public record UsuarioUpdateRequest(
        @NotBlank(message = "El DNI no puede ser nulo/vacío") String dni,
        @NotBlank(message = "El nombre no puede ser nulo/vacío") String nombre,
        @NotBlank(message = "El apellido no puede ser nulo/vacío") String apellido
){}
