package dominio.reglas;
import dominio.modelo.ItemPedido;
import dominio.modelo.Monto;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
public class CalcularTotal{
    private CalcularTotal(){}
    public static Monto de(List<ItemPedido> items){
        Monto suma=Monto.cero();
        for (ItemPedido item:items){
            suma=suma.sumar(item.getSubtotal());
        }return suma;}
}
