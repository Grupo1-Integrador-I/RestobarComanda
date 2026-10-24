package presentacion.api.dto;
import dominio.modelo.Rol;
import dominio.modelo.Usuario;
/**
 *
 * @author inici4rsesi0n
 */
public record UsuarioDTO(
        Long id,
        String codigo,
        String dni,
        String nombre,
        String apellido,
        Rol rol
){
    public static UsuarioDTO desde(Usuario usuario){
        return new UsuarioDTO(usuario.getId(), usuario.getCodigo(),
                usuario.getDni(), usuario.getNombre(),
                usuario.getApellido(), usuario.getRol());
    }
}
