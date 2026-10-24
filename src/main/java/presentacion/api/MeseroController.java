package presentacion.api;
import aplicacion.casosdeuso.CancelarPedido;
import aplicacion.casosdeuso.CobrarPedido;
import aplicacion.casosdeuso.EntregarPedido;
import aplicacion.casosdeuso.EnviarPedido;
import aplicacion.casosdeuso.GestionMesas;
import aplicacion.casosdeuso.GestionProductos;
import aplicacion.casosdeuso.LiberarMesa;
import aplicacion.casosdeuso.TomarPedido;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Mesa;
import dominio.modelo.Mesero;
import dominio.modelo.Pago;
import dominio.modelo.Producto;
import infraestructura.seguridad.UsuarioDetails;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import presentacion.api.dto.AgregarItemRequest;
import presentacion.api.dto.CobrarPedidoRequest;
import presentacion.api.dto.IniciarPedidoRequest;
import presentacion.api.dto.MesaDTO;
import presentacion.api.dto.PagoDTO;
import presentacion.api.dto.PedidoDTO;
import presentacion.api.dto.ProductoDTO;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@RestController
@RequestMapping("/api/mesero")
public class MeseroController{
    private final GestionMesas gestionMesas;
    private final GestionProductos gestionProductos;
    private final TomarPedido tomarPedido;
    private final EnviarPedido enviarPedido;
    private final EntregarPedido entregarPedido;
    private final CobrarPedido cobrarPedido;
    private final CancelarPedido cancelarPedido;
    private final LiberarMesa liberarMesa;
    public MeseroController(GestionMesas gestionMesas,
                            GestionProductos gestionProductos,
                            TomarPedido tomarPedido,
                            EnviarPedido enviarPedido,
                            EntregarPedido entregarPedido,
                            CobrarPedido cobrarPedido,
                            CancelarPedido cancelarPedido,
                            LiberarMesa liberarMesa){
        this.gestionMesas=gestionMesas;
        this.gestionProductos=gestionProductos;
        this.tomarPedido=tomarPedido;
        this.enviarPedido=enviarPedido;
        this.entregarPedido=entregarPedido;
        this.cobrarPedido=cobrarPedido;
        this.cancelarPedido=cancelarPedido;
        this.liberarMesa=liberarMesa;
    }
    @GetMapping("/mesas/libres")
    public ResponseEntity<List<MesaDTO>> listarMesasLibres(){
        return ResponseEntity.ok(gestionMesas.listarLibres().stream().map(MesaDTO::desde).toList());
    }
    @GetMapping("/mesas/ocupadas")
    public ResponseEntity<List<MesaDTO>> listarMesasOcupadas(){
        return ResponseEntity.ok(gestionMesas.listarOcupadas().stream().map(MesaDTO::desde).toList());
    }
    @GetMapping("/mesas/{id}")
    public ResponseEntity<MesaDTO> obtenerMesa(@PathVariable Long id){
        return ResponseEntity.ok(MesaDTO.desde(gestionMesas.obtener(id)));
    }
    @PostMapping("/mesas/{id}/liberar")
    public ResponseEntity<MesaDTO> liberarMesa(@PathVariable Long id){
        liberarMesa.ejecutar(id);
        return ResponseEntity.ok(MesaDTO.desde(gestionMesas.obtener(id)));
    }
    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDTO>> listarProductos(@RequestParam(required=false) CategoriaProducto categoria){
        List<Producto> productos=categoria!=null
                ?gestionProductos.listarPorCategoria(categoria)
                :gestionProductos.listarDisponibles();
        return ResponseEntity.ok(productos.stream().map(ProductoDTO::desde).toList());
    }
    @PostMapping("/pedidos")
    public ResponseEntity<PedidoDTO> iniciarPedido(@Valid @RequestBody IniciarPedidoRequest peticion,
                                                   @AuthenticationPrincipal UsuarioDetails details){
        Mesero mesero=meseroAutenticado(details);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PedidoDTO.desde(tomarPedido.iniciar(peticion.mesaId(), mesero)));
    }
    @GetMapping("/pedidos/{id}")
    public ResponseEntity<PedidoDTO> obtenerPedido(@PathVariable Long id){
        return ResponseEntity.ok(PedidoDTO.desde(tomarPedido.obtener(id)));
    }
    @GetMapping("/pedidos")
    public ResponseEntity<List<PedidoDTO>> listarActivosPorMesa(@RequestParam Long mesaId){
        return ResponseEntity.ok(tomarPedido.listarActivosPorMesa(mesaId).stream()
                .map(PedidoDTO::desde).toList());
    }
    @PostMapping("/pedidos/{id}/items")
    public ResponseEntity<PedidoDTO> agregarItem(@PathVariable Long id,
                                                 @Valid @RequestBody AgregarItemRequest peticion){
        tomarPedido.agregarItem(id, peticion.productoId(), peticion.cantidad());
        return ResponseEntity.ok(PedidoDTO.desde(tomarPedido.obtener(id)));
    }
    @DeleteMapping("/pedidos/{id}/items/{productoId}")
    public ResponseEntity<PedidoDTO> eliminarItem(@PathVariable Long id,
                                                  @PathVariable Long productoId){
        tomarPedido.eliminarItem(id, productoId);
        return ResponseEntity.ok(PedidoDTO.desde(tomarPedido.obtener(id)));
    }
    @PostMapping("/pedidos/{id}/enviar")
    public ResponseEntity<PedidoDTO> enviarPedido(@PathVariable Long id){
        enviarPedido.ejecutar(id);
        return ResponseEntity.ok(PedidoDTO.desde(tomarPedido.obtener(id)));
    }
    @PostMapping("/pedidos/{id}/entregar")
    public ResponseEntity<PedidoDTO> entregarPedido(@PathVariable Long id){
        entregarPedido.ejecutar(id);
        return ResponseEntity.ok(PedidoDTO.desde(tomarPedido.obtener(id)));
    }
    @PostMapping("/pedidos/{id}/pagar")
    public ResponseEntity<PagoDTO> pagarPedido(@PathVariable Long id,
                                               @Valid @RequestBody CobrarPedidoRequest peticion){
        Pago pago=cobrarPedido.ejecutar(id, peticion.tipo());
        return ResponseEntity.status(HttpStatus.CREATED).body(PagoDTO.desde(pago));
    }
    @PostMapping("/pedidos/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id){
        cancelarPedido.ejecutar(id);
        return ResponseEntity.ok(PedidoDTO.desde(tomarPedido.obtener(id)));
    }
    private Mesero meseroAutenticado(UsuarioDetails details){
        if(details==null||!(details.getUsuario() instanceof Mesero mesero))
            throw new IllegalStateException("Se requiere un mesero autenticado");
        return mesero;
    }
}
