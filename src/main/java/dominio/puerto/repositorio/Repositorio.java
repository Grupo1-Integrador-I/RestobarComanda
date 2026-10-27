package dominio.puerto.repositorio;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface Repositorio<T> {
    void agregar(T entidad);
    void actualizar(T entidad);
    Optional<T> buscarPorId(Long id);
    List<T> listarTodos();
    void eliminar(T entidad);
}
