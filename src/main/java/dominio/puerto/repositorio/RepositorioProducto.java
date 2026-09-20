package dominio.puerto.repositorio;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Producto;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
public interface RepositorioProducto extends Repositorio<Producto>{
    Optional<Producto> buscarPorNombre(String nombre);
    List<Producto> buscarPorNombreContiene(String texto);
    List<Producto> listarDisponibles();
    List<Producto> listarAgotados();
    List<Producto> listarPorCategoria(CategoriaProducto categoria);
}
