package aplicacion.casosdeuso;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Monto;
import dominio.modelo.Producto;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioProducto;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionProductos{
    private final RepositorioProducto repositorioProducto;
    private final LoggerPort logger;
    public GestionProductos(RepositorioProducto repositorioProducto, LoggerPort logger){
        this.repositorioProducto=repositorioProducto;
        this.logger=logger;
    }
    public Producto agregar(String nombre, Monto precio, CategoriaProducto categoria){
        if(nombre==null||nombre.isBlank())
            throw new IllegalArgumentException("Nombre no puede ser nulo/vacío");
        if(precio==null||precio.esCero())
            throw new IllegalArgumentException("Precio debe ser mayor a cero");
        if(categoria==null)
            throw new IllegalArgumentException("Categoría no puede ser nula");
        if(repositorioProducto.buscarPorNombre(nombre).isPresent())
            throw new IllegalStateException("Ya existe un producto con el nombre " + nombre);
        Producto producto=new Producto(nombre, precio, categoria);
        repositorioProducto.agregar(producto);
        logger.info("Producto creado: {}", producto);
        return producto;
    }
    public void actualizar(Long id, String nombre, Monto precio, CategoriaProducto categoria){
        Producto producto=obtenerProductoOLanzar(id);
        if(nombre!=null&&!nombre.isBlank()&&!nombre.equals(producto.getNombre())){
            if(repositorioProducto.buscarPorNombre(nombre).isPresent())
                throw new IllegalStateException("Ya existe un producto con el nombre " + nombre);
            producto.setNombre(nombre);
        }
        if(precio!=null&&!precio.esCero())
            producto.setPrecio(precio);
        if(categoria!=null)
            throw new UnsupportedOperationException("La categoría no puede modificarse");
        repositorioProducto.actualizar(producto);
        logger.info("Producto actualizado: {}", producto);
    }
    public void desactivar(Long id){
        Producto producto=obtenerProductoOLanzar(id);
        producto.desactivar();
        repositorioProducto.actualizar(producto);
        logger.info("Producto desactivado: {}", producto);
    }
    public void activar(Long id){
        Producto producto=obtenerProductoOLanzar(id);
        producto.activar();
        repositorioProducto.actualizar(producto);
        logger.info("Producto activado: {}", producto);
    }
    public void eliminar(Long id){
        Producto producto=obtenerProductoOLanzar(id);
        repositorioProducto.eliminar(producto);
        logger.info("Producto eliminado: {}", producto);
    }
    public List<Producto> listarTodos(){
        return repositorioProducto.listarTodos();
    }
    public List<Producto> listarDisponibles(){
        return repositorioProducto.listarDisponibles();
    }
    public List<Producto> listarAgotados(){
        return repositorioProducto.listarAgotados();
    }
    public List<Producto> listarPorCategoria(CategoriaProducto categoria){
        if(categoria==null)
            throw new IllegalArgumentException("Categoría no puede ser nula");
        return repositorioProducto.listarPorCategoria(categoria);
    }
    public List<Producto> buscar(String texto){
        if(texto==null||texto.isBlank())
            return repositorioProducto.listarTodos();
        return repositorioProducto.buscarPorNombreContiene(texto);
    }
    private Producto obtenerProductoOLanzar(Long id){
        if(id==null)
            throw new IllegalArgumentException("ID no puede ser nulo");
        return repositorioProducto.buscarPorId(id)
                .orElseThrow(()->new IllegalStateException("Producto no encontrado"));
    }
}
