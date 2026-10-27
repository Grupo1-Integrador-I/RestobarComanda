
package presentacion.controlador;

import aplicacion.casosdeuso.GestionProductos;
import dominio.modelo.Producto;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final GestionProductos gestionProductos;

    public ProductoController(GestionProductos gestionProductos) {
        this.gestionProductos = gestionProductos;
    }

    // LISTAR TODOS LOS PRODUCTOS
    @GetMapping
    public List<Producto> listarProductos() {
        return gestionProductos.listarTodos();
    }

    // LISTAR PRODUCTOS DISPONIBLES
    @GetMapping("/disponibles")
    public List<Producto> listarDisponibles() {
        return gestionProductos.listarDisponibles();
    }
}
