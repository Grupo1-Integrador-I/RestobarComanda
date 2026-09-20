package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public class Preparador extends Usuario{
    protected Preparador(){super();}
    public Preparador(String codigo, String hashContrasena, String dni,
            String nombre, String apellido){
        super(codigo, hashContrasena, dni, nombre, apellido, Rol.PREPARADOR);
    }
    @Override
    public String toString(){return "Preparador ["+super.toString()+"]";}
}
