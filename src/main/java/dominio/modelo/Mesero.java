package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public class Mesero extends Usuario{
    protected Mesero(){super();}
    public Mesero(String codigo, String hashContrasena, 
            String dni, String nombre, String apellido){
        super(codigo, hashContrasena, dni, nombre, apellido, Rol.MESERO);
    }
    @Override
    public String toString(){return "Mesero ["+super.toString()+"]";}
}
