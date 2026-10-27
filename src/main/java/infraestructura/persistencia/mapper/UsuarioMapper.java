package infraestructura.persistencia.mapper;
import dominio.modelo.Administrador;
import dominio.modelo.Mesero;
import dominio.modelo.Preparador;
import dominio.modelo.Usuario;
import infraestructura.persistencia.entidad.EntidadUsuario;
/**
 *
 * @author inici4rsesi0n
 */
public class UsuarioMapper{
    private UsuarioMapper(){}
    public static Usuario aDominio(EntidadUsuario e){
        if(e==null)
            return null;
        Usuario u=switch(e.getRol()){
            case MESERO -> new Mesero(e.getCodigo(), e.getHashContrasena(),
                    e.getDni(), e.getNombre(), e.getApellido());
            case PREPARADOR -> new Preparador(e.getCodigo(), e.getHashContrasena(),
                    e.getDni(), e.getNombre(), e.getApellido());
            case ADMINISTRADOR -> new Administrador(e.getCodigo(), e.getHashContrasena(),
                    e.getDni(), e.getNombre(), e.getApellido());
        };
        u.setId(e.getId());
        return u;
    }
    public static EntidadUsuario aEntidad(Usuario u){
        if(u==null)
            return null;
        EntidadUsuario e=new EntidadUsuario();
        e.setId(u.getId());
        e.setCodigo(u.getCodigo());
        e.setHashContrasena(u.getHashContrasena());
        e.setDni(u.getDni());
        e.setNombre(u.getNombre());
        e.setApellido(u.getApellido());
        e.setRol(u.getRol());
        return e;
    }
}
