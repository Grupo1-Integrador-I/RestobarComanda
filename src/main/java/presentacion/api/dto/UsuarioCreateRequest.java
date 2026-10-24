package presentacion.api.dto;
import dominio.modelo.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
/**
 *
 * @author inici4rsesi0n
 */
public record UsuarioCreateRequest(
        @NotBlank(message = "El código no puede ser nulo/vacío") String codigo,
        @NotBlank(message = "La contraseña no puede ser nula/vacía")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres") String password,
        @NotBlank(message = "El DNI no puede ser nulo/vacío") String dni,
        @NotBlank(message = "El nombre no puede ser nulo/vacío") String nombre,
        @NotBlank(message = "El apellido no puede ser nulo/vacío") String apellido,
        @NotNull(message = "El rol no puede ser nulo") Rol rol
){}
