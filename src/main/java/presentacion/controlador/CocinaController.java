
package presentacion.controlador;

import dominio.modelo.CategoriaProducto;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Pedido;
import dominio.puerto.repositorio.RepositorioPedido;

import infraestructura.websocket.dto.MensajePedido;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import aplicacion.casosdeuso.ActualizarPreparacionItem;
import dominio.modelo.EstadoPreparacion;

@RestController
@RequestMapping("/api/cocina")
public class CocinaController {

   
private final RepositorioPedido repositorioPedido;
private final ActualizarPreparacionItem actualizarPreparacionItem;


public CocinaController(
        RepositorioPedido repositorioPedido,
        ActualizarPreparacionItem actualizarPreparacionItem) {

    this.repositorioPedido = repositorioPedido;
    this.actualizarPreparacionItem = actualizarPreparacionItem;
}


    @GetMapping("/pendientes")
    public List<MensajePedido> listarPendientes() {

        return Stream.concat(
                repositorioPedido
                    .listarPorEstado(EstadoPedido.ENVIADO)
                    .stream(),
                repositorioPedido
                    .listarPorEstado(EstadoPedido.EN_PREPARACION)
                    .stream()
        )
     
.filter(pedido -> pedido.getItems().stream()
        .anyMatch(item ->
                item.getProducto().getCategoria()
                        == CategoriaProducto.COCINA
                && item.getEstadoPreparacion()
                        != EstadoPreparacion.LISTO))

        .map(pedido -> {
            MensajePedido mensaje = MensajePedido.desde(pedido);

            return new MensajePedido(
                    mensaje.id(),
                    mensaje.numeroMesa(),
                    mensaje.nombreMesero(),
                    mensaje.estado(),
                    mensaje.total(),
                    mensaje.fechaCreacion(),
                    mensaje.itemsDeCategoria(CategoriaProducto.COCINA)
            );
        })
        .toList();
    }
    

// INICIAR PREPARACIÓN DE PRODUCTOS DE COCINA
@PostMapping("/{pedidoId}/iniciar")
public String iniciarPreparacion(@PathVariable Long pedidoId) {

    actualizarPreparacionItem.iniciarPreparacion(
            pedidoId,
            CategoriaProducto.COCINA
    );

    return "Productos de cocina marcados EN_PREPARACION correctamente";
}

// FINALIZAR PREPARACIÓN DE PRODUCTOS DE COCINA
@PostMapping("/{pedidoId}/listo")
public String marcarPedidoListo(@PathVariable Long pedidoId) {

    actualizarPreparacionItem.finalizarPreparacion(
            pedidoId,
            CategoriaProducto.COCINA
    );

    return "Productos de cocina marcados LISTO correctamente";
}

}
