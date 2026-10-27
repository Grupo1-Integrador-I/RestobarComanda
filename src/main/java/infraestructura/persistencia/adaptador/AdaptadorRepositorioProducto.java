package infraestructura.persistencia.adaptador;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Producto;
import dominio.puerto.repositorio.RepositorioProducto;
import infraestructura.persistencia.mapper.ProductoMapper;
import infraestructura.persistencia.repositorioJPA.RepositorioProductoJPA;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioProducto implements RepositorioProducto{
    private final RepositorioProductoJPA jpa;
    public AdaptadorRepositorioProducto(RepositorioProductoJPA jpa){
        this.jpa=jpa;
    }
    @Override
    public void agregar(Producto producto){
        jpa.save(ProductoMapper.aEntidad(producto));
    }
    @Override
    public void actualizar(Producto producto){
        jpa.save(ProductoMapper.aEntidad(producto));
    }
    @Override
    public Optional<Producto> buscarPorId(Long id){
        return jpa.findById(id).map(ProductoMapper::aDominio);
    }
    @Override
    public List<Producto> listarTodos(){
        return jpa.findAll().stream().map(ProductoMapper::aDominio).toList();
    }
    @Override
    public void eliminar(Producto producto){
        jpa.deleteById(producto.getId());
    }
    @Override
    public Optional<Producto> buscarPorNombre(String nombre){
        return jpa.findByNombre(nombre).map(ProductoMapper::aDominio);
    }
    @Override
    public List<Producto> listarDisponibles(){
        return jpa.findByDisponibleTrue().stream()
                .map(ProductoMapper::aDominio).toList();
    }
    @Override
    public List<Producto> listarAgotados(){
        return jpa.findByDisponibleFalse().stream()
                .map(ProductoMapper::aDominio).toList();
    }
    @Override
    public List<Producto> listarPorCategoria(CategoriaProducto categoria){
        return jpa.findByCategoria(categoria).stream()
                .map(ProductoMapper::aDominio).toList();
    }
    @Override
    public List<Producto> buscarPorNombreContiene(String texto){
        return jpa.findByNombreContainingIgnoreCase(texto).stream()
                .map(ProductoMapper::aDominio).toList();
    }
}
