package dominio.excepcion;
/**
 *
 * @author inici4rsesi0n
 */
public class RecursoNoEncontradoException extends IllegalStateException{
    public RecursoNoEncontradoException(String mensaje){
        super(mensaje);
    }
}
