package infraestructura.seguridad;
import java.util.Arrays;
/**
 *
 * @author inici4rsesi0n
 */
public final class UtilLimpieza{
    private UtilLimpieza(){}
    public static void limpiar(char[] arreglo){
        if(arreglo!=null)
            Arrays.fill(arreglo, '\0');
    }
}
