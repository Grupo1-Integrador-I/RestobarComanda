
package presentacion.controlador;

import aplicacion.casosdeuso.TomarPedido;
import aplicacion.casosdeuso.EnviarPedido;
import dominio.modelo.Mesero;
import dominio.modelo.Pedido;
import dominio.modelo.Usuario;
import dominio.puerto.repositorio.RepositorioUsuario;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import dominio.puerto.repositorio.RepositorioMesa;
import dominio.puerto.repositorio.RepositorioPedido;
import java.math.BigDecimal;
import java.util.List;
import aplicacion.casosdeuso.ActualizarEstadoPedido;
import dominio.modelo.EstadoPedido;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final TomarPedido tomarPedido;
    private final EnviarPedido enviarPedido;
    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioMesa repositorioMesa;
private final RepositorioPedido repositorioPedido;
private final ActualizarEstadoPedido actualizarEstadoPedido;

    public PedidoController(
        TomarPedido tomarPedido,
        EnviarPedido enviarPedido,
        RepositorioUsuario repositorioUsuario,
      RepositorioMesa repositorioMesa,
RepositorioPedido repositorioPedido,
ActualizarEstadoPedido actualizarEstadoPedido) {

    this.tomarPedido = tomarPedido;
    this.enviarPedido = enviarPedido;
    this.repositorioUsuario = repositorioUsuario;
    this.repositorioMesa = repositorioMesa;
    this.repositorioPedido = repositorioPedido;
    this.actualizarEstadoPedido = actualizarEstadoPedido;
}

    @GetMapping("/prueba")
    public String prueba() {
        return "API de pedidos funcionando correctamente";
    }

    // INICIAR UN PEDIDO
    @PostMapping("/iniciar")
    public Long iniciarPedido(
            @RequestParam Long mesaId,
            Principal principal) {

        Mesero mesero = obtenerMesero(principal);

        Pedido pedido = tomarPedido.iniciar(
                mesaId,
                mesero
        );

        return pedido.getId();
    }

    // ENVIAR UN PEDIDO A COCINA
    @PostMapping("/{pedidoId}/enviar")
    public String enviarPedido(
            @PathVariable Long pedidoId,
            Principal principal) {

        obtenerMesero(principal);

        enviarPedido.ejecutar(pedidoId);

        return "Pedido enviado a cocina correctamente";
    }

    //AGREGRA PRODUCTO
    
@PostMapping("/{pedidoId}/items")
public String agregarProducto(
        @PathVariable Long pedidoId,
        @RequestParam Long productoId,
        @RequestParam int cantidad,
        Principal principal) {

    obtenerMesero(principal);

    if (cantidad <= 0) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "La cantidad debe ser mayor que cero"
        );
    }

    tomarPedido.agregarItem(
            pedidoId,
            productoId,
            cantidad
    );

    return "Producto agregado correctamente al pedido";
}


@GetMapping("/{pedidoId}")
public Pedido consultarPedido(
        @PathVariable Long pedidoId,
        Principal principal) {

    Mesero mesero = obtenerMesero(principal);

    Pedido pedido = tomarPedido.obtener(pedidoId);

    if (!pedido.getMesero().getId().equals(mesero.getId())) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No tiene permiso para consultar este pedido"
        );
    }

    return pedido;
}

@PostMapping("/{pedidoId}/entregar")
public String entregarPedido(
        @PathVariable Long pedidoId,
        Principal principal) {

    Mesero mesero = obtenerMesero(principal);

    Pedido pedido = repositorioPedido.buscarPorId(pedidoId)
            .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Pedido no encontrado"
                    )
            );

    if (!pedido.getMesero().getId().equals(mesero.getId())) {
        throw new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "No tiene permiso para entregar este pedido"
        );
    }

    if (pedido.getEstado() != EstadoPedido.LISTO) {
        throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "El pedido todavía no está listo para entregar"
        );
    }

    actualizarEstadoPedido.marcarEntregado(pedidoId);

    return "Pedido entregado correctamente al cliente";
}

    // CONSULTAR PEDIDOS ACTIVOS DE UNA MESA
    @GetMapping("/mesa/{mesaId}/activos")
    public List<PedidoActivoDTO> listarPedidosActivos(
            @PathVariable Long mesaId,
            Principal principal) {

        Mesero mesero = obtenerMesero(principal);

        var mesa = repositorioMesa.buscarPorId(mesaId)
                .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Mesa no encontrada"
                    )
                );

        return repositorioPedido.listarActivosPorMesa(mesa)
                .stream()
                .filter(p -> p.getMesero().getId()
                        .equals(mesero.getId()))
                
.map(p -> new PedidoActivoDTO(
        p.getId(),
        p.getMesa().getId(),
        p.getMesero().getNombre(),
        p.getEstado().name(),
        p.getTotal().valor(),
        p.getFechaCreacion(),
        p.getItems().stream()
                .map(item -> new ItemPedidoDTO(
                        item.getProducto().getId(),
                        item.getProducto().getNombre(),
                        item.getCantidad(),
                        item.getProducto().getPrecio().valor(),
                        item.getSubtotal().valor(),
                        item.getProducto().getCategoria().name()
                ))
                .toList()
))

                .toList();
    }

    // DTO PARA ENVIAR LOS DATOS AL FRONTEND
 
    // INFORMACIÓN DEL PEDIDO
    public record PedidoActivoDTO(
            Long id,
            Long mesaId,
            String meseroNombre,
            String estado,
            BigDecimal total,
            java.time.LocalDateTime fecha,
            List<ItemPedidoDTO> items
    ) {}

    // INFORMACIÓN DE CADA PRODUCTO DEL PEDIDO
    public record ItemPedidoDTO(
            Long productoId,
            String nombre,
            int cantidad,
            BigDecimal precio,
            BigDecimal subtotal,
            String categoria
    ) {}




    // VALIDAR QUE EL USUARIO SEA MESERO
    private Mesero obtenerMesero(Principal principal) {

        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Debe iniciar sesión"
            );
        }

        Usuario usuario = repositorioUsuario
                .buscarPorCodigo(principal.getName())
                .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Usuario no encontrado"
                    )
                );

        if (!(usuario instanceof Mesero)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "El usuario no es un mesero"
            );
        }

        return (Mesero) usuario;
    }
}
