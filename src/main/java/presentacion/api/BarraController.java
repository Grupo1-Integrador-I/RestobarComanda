package presentacion.api;
import aplicacion.casosdeuso.ActualizarEstadoPedido;
import aplicacion.casosdeuso.TomarPedido;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Pedido;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import presentacion.api.dto.PedidoDTO;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@RestController
@RequestMapping("/api/barra")
public class BarraController{
    private static final CategoriaProducto CATEGORIA=CategoriaProducto.BARRA;
    private final ActualizarEstadoPedido actualizarEstadoPedido;
    private final TomarPedido tomarPedido;
    public BarraController(ActualizarEstadoPedido actualizarEstadoPedido,
                           TomarPedido tomarPedido){
        this.actualizarEstadoPedido=actualizarEstadoPedido;
        this.tomarPedido=tomarPedido;
    }
    @GetMapping("/pedidos/pendientes")
    public ResponseEntity<List<PedidoDTO>> listarPendientes(){
        return ResponseEntity.ok(filtrar(actualizarEstadoPedido.listarPendientes()));
    }
    @GetMapping("/pedidos/en-preparacion")
    public ResponseEntity<List<PedidoDTO>> listarEnPreparacion(){
        return ResponseEntity.ok(filtrar(actualizarEstadoPedido.listarEnPreparacion()));
    }
    @PostMapping("/pedidos/{id}/preparacion")
    public ResponseEntity<PedidoDTO> marcarEnPreparacion(@PathVariable Long id){
        actualizarEstadoPedido.marcarEnPreparacion(id);
        return ResponseEntity.ok(PedidoDTO.desdeFiltradoPorCategoria(tomarPedido.obtener(id), CATEGORIA));
    }
    @PostMapping("/pedidos/{id}/listo")
    public ResponseEntity<PedidoDTO> marcarListo(@PathVariable Long id){
        actualizarEstadoPedido.marcarListo(id);
        return ResponseEntity.ok(PedidoDTO.desdeFiltradoPorCategoria(tomarPedido.obtener(id), CATEGORIA));
    }
    private List<PedidoDTO> filtrar(List<Pedido> pedidos){
        List<PedidoDTO> resultado=new ArrayList<>();
        for(Pedido p:pedidos){
            PedidoDTO dto=PedidoDTO.desdeFiltradoPorCategoria(p, CATEGORIA);
            if(!dto.items().isEmpty())
                resultado.add(dto);
        }
        return resultado;
    }
}
