package dominio.reglas;
import dominio.modelo.Pedido;
/**
 *
 * @author inici4rsesi0n
 */
public class ValidadorPedido{
    private ValidadorPedido(){}
    public static void validarPuedeSerPagado(Pedido pedido){
        if(pedido==null)
            throw new IllegalArgumentException("Pedido no puede ser nulo");
        if(pedido.cantidadItems()==0)
            throw new IllegalStateException("No se puede pagar un pedido sin ítems");
    }
}
