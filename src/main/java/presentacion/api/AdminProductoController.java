package presentacion.api;
import aplicacion.casosdeuso.GestionProductos;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Monto;
import dominio.modelo.Producto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import presentacion.api.dto.ProductoCreateRequest;
import presentacion.api.dto.ProductoDTO;
import presentacion.api.dto.ProductoUpdateRequest;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@RestController
@RequestMapping("/api/admin/productos")
public class AdminProductoController{
    private final GestionProductos gestionProductos;
    public AdminProductoController(GestionProductos gestionProductos){
        this.gestionProductos=gestionProductos;
    }
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listar(@RequestParam(required=false) CategoriaProducto categoria,
                                                    @RequestParam(required=false) Boolean disponible){
        List<Producto> productos;
        if(categoria!=null)
            productos=gestionProductos.listarPorCategoria(categoria);
        else if(disponible!=null)
            productos=disponible?gestionProductos.listarDisponibles():gestionProductos.listarAgotados();
        else
            productos=gestionProductos.listarTodos();
        return ResponseEntity.ok(productos.stream().map(ProductoDTO::desde).toList());
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoDTO>> buscar(@RequestParam(required=false) String q){
        return ResponseEntity.ok(gestionProductos.buscar(q).stream().map(ProductoDTO::desde).toList());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtener(@PathVariable Long id){
        return ResponseEntity.ok(ProductoDTO.desde(gestionProductos.obtener(id)));
    }
    @PostMapping
    public ResponseEntity<ProductoDTO> crear(@Valid @RequestBody ProductoCreateRequest peticion){
        Producto producto=gestionProductos.agregar(peticion.nombre(),
                new Monto(peticion.precio()), peticion.categoria());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductoDTO.desde(producto));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizar(@PathVariable Long id,
                                                  @Valid @RequestBody ProductoUpdateRequest peticion){
        gestionProductos.actualizar(id, peticion.nombre(),
                new Monto(peticion.precio()), null);
        return ResponseEntity.ok(ProductoDTO.desde(gestionProductos.obtener(id)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        gestionProductos.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/activar")
    public ResponseEntity<ProductoDTO> activar(@PathVariable Long id){
        gestionProductos.activar(id);
        return ResponseEntity.ok(ProductoDTO.desde(gestionProductos.obtener(id)));
    }
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<ProductoDTO> desactivar(@PathVariable Long id){
        gestionProductos.desactivar(id);
        return ResponseEntity.ok(ProductoDTO.desde(gestionProductos.obtener(id)));
    }
}
