package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public class Administrador extends Usuario{
    protected Administrador(){super();}
    public Administrador(String codigo, String hashContrasena, String dni,
                         String nombre, String apellido) {
        super(codigo, hashContrasena, dni, nombre, apellido, Rol.ADMINISTRADOR);
    }
    @Override
    public String toString(){return "Administrador ["+super.toString()+"]";}
}
