
package presentacion.controlador;

import aplicacion.casosdeuso.ActualizarPreparacionItem;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.EstadoPedido;
import dominio.puerto.repositorio.RepositorioPedido;
import infraestructura.websocket.dto.MensajePedido;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dominio.modelo.EstadoPreparacion;

@RestController
@RequestMapping("/api/barra")
public class BarraController {

    private final RepositorioPedido repositorioPedido;
    private final ActualizarPreparacionItem actualizarPreparacionItem;

    public BarraController(
            RepositorioPedido repositorioPedido,
            ActualizarPreparacionItem actualizarPreparacionItem) {

        this.repositorioPedido = repositorioPedido;
        this.actualizarPreparacionItem = actualizarPreparacionItem;
    }

    // LISTAR PEDIDOS DE BARRA
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
                        == CategoriaProducto.BARRA
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
                    mensaje.itemsDeCategoria(CategoriaProducto.BARRA)
            );
        })
        .toList();
    }

    // INICIAR PREPARACION DE BEBIDAS
    @PostMapping("/{pedidoId}/iniciar")
    public String iniciarPreparacion(
            @PathVariable Long pedidoId) {

        actualizarPreparacionItem.iniciarPreparacion(
                pedidoId,
                CategoriaProducto.BARRA
        );

        return "Productos de barra marcados EN_PREPARACION correctamente";
    }

    // FINALIZAR PREPARACION DE BEBIDAS
    @PostMapping("/{pedidoId}/listo")
    public String finalizarPreparacion(
            @PathVariable Long pedidoId) {

        actualizarPreparacionItem.finalizarPreparacion(
                pedidoId,
                CategoriaProducto.BARRA
        );

        return "Productos de barra marcados LISTO correctamente";
    }
}
