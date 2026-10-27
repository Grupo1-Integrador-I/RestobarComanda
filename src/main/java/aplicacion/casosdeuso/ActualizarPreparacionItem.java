
package aplicacion.casosdeuso;

import dominio.modelo.CategoriaProducto;
import dominio.modelo.EstadoPedido;
import dominio.modelo.EstadoPreparacion;
import dominio.modelo.ItemPedido;
import dominio.modelo.Pedido;
import dominio.puerto.repositorio.RepositorioPedido;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActualizarPreparacionItem {

    private final RepositorioPedido repositorioPedido;

    public ActualizarPreparacionItem(
            RepositorioPedido repositorioPedido) {
        this.repositorioPedido = repositorioPedido;
    }

    @Transactional
    public void iniciarPreparacion(
            Long pedidoId, CategoriaProducto area) {

        Pedido pedido = obtenerPedido(pedidoId);
        validarPedidoActivo(pedido);

        List<ItemPedido> items = obtenerItemsArea(pedido, area);

        boolean hayPendientes = items.stream()
                .anyMatch(item -> item.getEstadoPreparacion()
                        == EstadoPreparacion.PENDIENTE);

        if (!hayPendientes) {
            throw new IllegalStateException(
                    "No hay productos pendientes en esta área"
            );
        }

        for (ItemPedido item : items) {
            if (item.getEstadoPreparacion()
                    == EstadoPreparacion.PENDIENTE) {
                item.marcarEnPreparacion();
            }
        }

       sincronizarEstadoGeneral(pedido);
repositorioPedido.actualizar(pedido);
    }

    @Transactional
    public void finalizarPreparacion(
            Long pedidoId, CategoriaProducto area) {

        Pedido pedido = obtenerPedido(pedidoId);
        validarPedidoActivo(pedido);

        List<ItemPedido> items = obtenerItemsArea(pedido, area);

        boolean hayEnPreparacion = items.stream()
                .anyMatch(item -> item.getEstadoPreparacion()
                        == EstadoPreparacion.EN_PREPARACION);

        if (!hayEnPreparacion) {
            throw new IllegalStateException(
                    "No hay productos en preparación en esta área"
            );
        }

        for (ItemPedido item : items) {
            if (item.getEstadoPreparacion()
                    == EstadoPreparacion.EN_PREPARACION) {
                item.marcarListo();
            }
        }

        sincronizarEstadoGeneral(pedido);
repositorioPedido.actualizar(pedido);
    }

   
private Pedido obtenerPedido(Long pedidoId) {

    if (pedidoId == null) {
        throw new IllegalArgumentException(
            "El ID del pedido es obligatorio"
        );
    }

    return repositorioPedido.buscarPorIdConBloqueo(pedidoId)
            .orElseThrow(() -> new IllegalStateException(
                "Pedido no encontrado: " + pedidoId
            ));
}


    private void validarPedidoActivo(Pedido pedido) {
        if (pedido.getEstado() != EstadoPedido.ENVIADO
                && pedido.getEstado()
                        != EstadoPedido.EN_PREPARACION) {
            throw new IllegalStateException(
                    "El pedido no está disponible para preparación"
            );
        }
    }

    private List<ItemPedido> obtenerItemsArea(
            Pedido pedido, CategoriaProducto area) {

        if (area == null) {
            throw new IllegalArgumentException(
                    "El área es obligatoria"
            );
        }

        List<ItemPedido> items = pedido.getItems().stream()
                .filter(item -> item.getProducto().getCategoria()
                        == area)
                .toList();

        if (items.isEmpty()) {
            throw new IllegalStateException(
                    "El pedido no contiene productos de " + area
            );
        }

        return items;
    }
    
private void sincronizarEstadoGeneral(Pedido pedido) {

    boolean todosListos = pedido.getItems().stream()
            .allMatch(item ->
                    item.getEstadoPreparacion()
                    == EstadoPreparacion.LISTO);

    boolean algunoIniciado = pedido.getItems().stream()
            .anyMatch(item ->
                    item.getEstadoPreparacion()
                    != EstadoPreparacion.PENDIENTE);

    if (todosListos
            && pedido.getEstado() == EstadoPedido.EN_PREPARACION) {

        pedido.marcarListo();

    } else if (algunoIniciado
            && pedido.getEstado() == EstadoPedido.ENVIADO) {

        pedido.marcarEnPreparacion();
    }
}

}
