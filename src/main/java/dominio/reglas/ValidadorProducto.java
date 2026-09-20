package dominio.reglas;
import dominio.modelo.Producto;
/**
 *
 * @author inici4rsesi0n
 */
public class ValidadorProducto{
    private ValidadorProducto(){}
    public static void validarDisponible(Producto producto){
        if(producto==null)
            throw new IllegalArgumentException("Producto no puede ser nulo");
        if(!producto.estaDisponible())
            throw new IllegalStateException("El producto '" + producto.getNombre() + "' está agotado");
    }
}
