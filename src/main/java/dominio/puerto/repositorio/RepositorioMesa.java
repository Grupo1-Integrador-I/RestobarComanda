package dominio.puerto.repositorio;
import dominio.modelo.Mesa;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioMesa extends Repositorio<Mesa>{
    Optional<Mesa> buscarPorNumero(String numero);
    List<Mesa> buscarPorNumeroContiene(String texto);
    List<Mesa> listarLibres();
    List<Mesa> listarOcupadas();
}
