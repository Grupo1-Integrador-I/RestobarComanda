package aplicacion.casosdeuso;
import dominio.excepcion.RecursoNoEncontradoException;
import dominio.modelo.Administrador;
import dominio.modelo.Mesero;
import dominio.modelo.Preparador;
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
    public Usuario agregar(String codigo, char[] contrasena, String dni,
                           String nombre, String apellido, Rol rol){
        if(codigo==null||codigo.isBlank())
            throw new IllegalArgumentException("Código no puede ser nulo/vacío");
        if(contrasena==null||contrasena.length==0)
            throw new IllegalArgumentException("Contraseña no puede ser nula/vacía");
        if(dni==null||dni.isBlank())
            throw new IllegalArgumentException("DNI no puede ser nulo/vacío");
        if(nombre==null||nombre.isBlank())
            throw new IllegalArgumentException("Nombre no puede ser nulo/vacío");
        if(apellido==null||apellido.isBlank())
            throw new IllegalArgumentException("Apellido no puede ser nulo/vacío");
        if(rol==null)
            throw new IllegalArgumentException("Rol no puede ser nulo");
        if(repositorioUsuario.buscarPorCodigo(codigo).isPresent())
            throw new IllegalStateException("Ya existe un usuario con el código " + codigo);
        if(repositorioUsuario.buscarPorDni(dni).isPresent())
            throw new IllegalStateException("Ya existe un usuario con el DNI " + dni);
        try{
            String hash=hashProvider.generarHash(contrasena);
            Usuario usuario=construirUsuario(codigo, hash, dni, nombre, apellido, rol);
            repositorioUsuario.agregar(usuario);
            logger.info("Usuario creado: {}", usuario);
            return usuario;
        }finally{
            Arrays.fill(contrasena, '\0');
        }
    }
    public void actualizar(Long id, String dni, String nombre, String apellido){
        Usuario usuario=obtener(id);
        if(dni!=null&&!dni.isBlank()&&!dni.equals(usuario.getDni())){
            if(repositorioUsuario.buscarPorDni(dni).isPresent())
                throw new IllegalStateException("Ya existe un usuario con el DNI " + dni);
            usuario.setDni(dni);
        }
        if(nombre!=null&&!nombre.isBlank())
            usuario.setNombre(nombre);
        if(apellido!=null&&!apellido.isBlank())
            usuario.setApellido(apellido);
        repositorioUsuario.actualizar(usuario);
        logger.info("Usuario actualizado: {}", usuario);
    }
    public void cambiarContrasena(Long id, char[] nuevaContrasena){
        if(nuevaContrasena==null||nuevaContrasena.length==0)
            throw new IllegalArgumentException("Contraseña no puede ser nula/vacía");
        Usuario usuario=obtener(id);
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
        Usuario usuario=obtener(id);
        repositorioUsuario.eliminar(usuario);
        logger.info("Usuario eliminado: {}", usuario);
    }
    public Usuario obtener(Long id){
        if(id==null)
            throw new IllegalArgumentException("ID no puede ser nulo");
        return repositorioUsuario.buscarPorId(id)
                .orElseThrow(()->new RecursoNoEncontradoException("Usuario no encontrado"));
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
    private Usuario construirUsuario(String codigo, String hash, String dni,
                                     String nombre, String apellido, Rol rol){
        return switch(rol){
            case MESERO -> new Mesero(codigo, hash, dni, nombre, apellido);
            case PREPARADOR -> new Preparador(codigo, hash, dni, nombre, apellido);
            case ADMINISTRADOR -> new Administrador(codigo, hash, dni, nombre, apellido);
        };
    }
}
