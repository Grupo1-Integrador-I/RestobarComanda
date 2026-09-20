package infraestructura.seguridad;
import dominio.modelo.Usuario;
import dominio.puerto.repositorio.RepositorioUsuario;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class UsuarioDetailsService implements UserDetailsService{
    private final RepositorioUsuario repositorioUsuario;
    public UsuarioDetailsService(RepositorioUsuario repositorioUsuario){
        this.repositorioUsuario=repositorioUsuario;
    }
    @Override
    public UserDetails loadUserByUsername(String codigo) throws UsernameNotFoundException{
        Usuario usuario=repositorioUsuario.buscarPorCodigo(codigo)
                .orElseThrow(()->new UsernameNotFoundException("Usuario no encontrado: "+codigo));
        return new UsuarioDetails(usuario);
    }
}
