package aplicacion.casosdeuso;
import dominio.modelo.Rol;
import dominio.modelo.Usuario;
import dominio.puerto.externo.HashProvider;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioUsuario;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionUsuarios{
    private final RepositorioUsuario repositorioUsuario;
    private final HashProvider hashProvider;
    private final LoggerPort logger;
    public GestionUsuarios(RepositorioUsuario repositorioUsuario,
                           HashProvider hashProvider,
                           LoggerPort logger){
        this.repositorioUsuario=repositorioUsuario;
        this.hashProvider=hashProvider;
        this.logger=logger;
    }
    public void agregar(Usuario usuario, char[] contrasena){
        if(usuario==null)
            throw new IllegalArgumentException("Usuario no puede ser nulo");
        if(contrasena==null||contrasena.length==0)
            throw new IllegalArgumentException("Contraseña no puede ser nula/vacía");
        if(repositorioUsuario.buscarPorCodigo(usuario.getCodigo()).isPresent())
            throw new IllegalStateException("Ya existe un usuario con el código " + usuario.getCodigo());
        if(repositorioUsuario.buscarPorDni(usuario.getDni()).isPresent())
            throw new IllegalStateException("Ya existe un usuario con el DNI " + usuario.getDni());
        try{
            String hash=hashProvider.generarHash(contrasena);
            usuario.setHashContrasena(hash);
            repositorioUsuario.agregar(usuario);
            logger.info("Usuario creado: {}", usuario);
        }finally{
            Arrays.fill(contrasena, '\0');
        }
    }
    public void actualizar(Usuario usuario){
        if(usuario==null||usuario.getId()==null)
            throw new IllegalArgumentException("Usuario o ID no pueden ser nulos");
        repositorioUsuario.buscarPorId(usuario.getId())
                .orElseThrow(()->new IllegalStateException("Usuario no encontrado"));
        repositorioUsuario.actualizar(usuario);
        logger.info("Usuario actualizado: {}", usuario);
    }
    public void cambiarContrasena(Long id, char[] nuevaContrasena){
        if(nuevaContrasena==null||nuevaContrasena.length==0)
            throw new IllegalArgumentException("Contraseña no puede ser nula/vacía");
        Usuario usuario=repositorioUsuario.buscarPorId(id)
                .orElseThrow(()->new IllegalStateException("Usuario no encontrado"));
        try{
            String hash=hashProvider.generarHash(nuevaContrasena);
            usuario.setHashContrasena(hash);
            repositorioUsuario.actualizar(usuario);
            logger.info("Contraseña cambiada para usuario: {}", usuario);
        }finally{
            Arrays.fill(nuevaContrasena, '\0');
        }
    }
    public void eliminar(Long id){
        if(id==null)
            throw new IllegalArgumentException("ID no puede ser nulo");
        Usuario usuario=repositorioUsuario.buscarPorId(id)
                .orElseThrow(()->new IllegalStateException("Usuario no encontrado"));
        repositorioUsuario.eliminar(usuario);
        logger.info("Usuario eliminado: {}", usuario);
    }
    public List<Usuario> listarTodos(){
        return repositorioUsuario.listarTodos();
    }
    public List<Usuario> listarPorRol(Rol rol){
        if(rol==null)
            throw new IllegalArgumentException("Rol no puede ser nulo");
        return repositorioUsuario.listarTodos().stream()
                .filter(u->u.getRol()==rol)
                .toList();
    }
    public List<Usuario> buscar(String texto){
        if(texto==null||texto.isBlank())
            return repositorioUsuario.listarTodos();
        return repositorioUsuario.buscarPorNombreOApellidoContiene(texto);
    }
}
