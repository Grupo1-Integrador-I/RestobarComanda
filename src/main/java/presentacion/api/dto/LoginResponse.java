package presentacion.api.dto;
import dominio.modelo.Rol;
import dominio.modelo.Usuario;
/**
 *
 * @author inici4rsesi0n
 */
public record LoginResponse(
        Long id,
        String codigo,
        String nombre,
        String apellido,
        Rol rol,
        String mensaje
){
    public static LoginResponse desde(Usuario usuario, String mensaje){
        return new LoginResponse(usuario.getId(), usuario.getCodigo(),
                usuario.getNombre(), usuario.getApellido(),
                usuario.getRol(), mensaje);
    }
}
