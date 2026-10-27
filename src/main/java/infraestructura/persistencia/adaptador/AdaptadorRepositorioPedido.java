package infraestructura.persistencia.adaptador;
import dominio.modelo.EstadoPedido;
import dominio.modelo.Mesa;
import dominio.modelo.Pedido;
import dominio.puerto.repositorio.RepositorioPedido;
import infraestructura.persistencia.entidad.EntidadMesa;
import infraestructura.persistencia.mapper.MesaMapper;
import infraestructura.persistencia.mapper.PedidoMapper;
import infraestructura.persistencia.repositorioJPA.RepositorioPedidoJPA;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import infraestructura.persistencia.entidad.EntidadPedido;
import infraestructura.persistencia.entidad.EntidadItemPedido;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import infraestructura.persistencia.mapper.ItemPedidoMapper;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioPedido implements RepositorioPedido{
    private final RepositorioPedidoJPA jpa;
    public AdaptadorRepositorioPedido(RepositorioPedidoJPA jpa){
        this.jpa=jpa;
    }
   @Override
public void agregar(Pedido pedido) {
    var entidadGuardada = jpa.save(
        PedidoMapper.aEntidad(pedido)
    );

    pedido.setId(entidadGuardada.getId());
}
   

@Override
@Transactional
public void actualizar(Pedido pedido) {

    if (pedido == null || pedido.getId() == null) {
        throw new IllegalArgumentException(
            "El pedido debe tener un ID válido"
        );
    }

    EntidadPedido entidad = jpa.findById(pedido.getId())
            .orElseThrow(() -> new IllegalStateException(
                "Pedido no encontrado: " + pedido.getId()
            ));

    // Guardar el estado original antes de modificarlo
    EstadoPedido estadoAnterior = entidad.getEstado();

    // Recuperar los productos existentes
    Map<Long, EntidadItemPedido> itemsExistentes =
            entidad.getItems().stream()
                .collect(Collectors.toMap(
                    EntidadItemPedido::getId,
                    Function.identity()
                ));

    for (var item : pedido.getItems()) {

        if (item.getId() == null) {

            if (estadoAnterior != EstadoPedido.CREADO
                    || pedido.getEstado() != EstadoPedido.CREADO) {

                throw new IllegalStateException(
                    "Solo se pueden agregar productos a pedidos en estado CREADO"
                );
            }

            EntidadItemPedido nuevoItem =
                    ItemPedidoMapper.aEntidad(item, entidad);

            entidad.getItems().add(nuevoItem);

            continue;
        }

        EntidadItemPedido entidadItem =
                itemsExistentes.get(item.getId());

        if (entidadItem == null) {
            throw new IllegalStateException(
                "Ítem no encontrado: " + item.getId()
            );
        }

       
if (estadoAnterior == EstadoPedido.CREADO
        && pedido.getEstado() == EstadoPedido.CREADO) {

    entidadItem.setCantidad(item.getCantidad());
    entidadItem.setSubtotal(
            item.getSubtotal().valor()
    );
}

entidadItem.setEstadoPreparacion(
        item.getEstadoPreparacion()
);

    }
    

 // Eliminar solamente productos que ya existían
 // antes de comenzar esta actualización
if (estadoAnterior == EstadoPedido.CREADO
        && pedido.getEstado() == EstadoPedido.CREADO) {

    java.util.Set<Long> idsConservados =
            pedido.getItems().stream()
                    .map(item -> item.getId())
                    .filter(id -> id != null)
                    .collect(Collectors.toSet());

    java.util.Set<Long> idsEliminar =
            itemsExistentes.keySet().stream()
                    .filter(id -> !idsConservados.contains(id))
                    .collect(Collectors.toSet());

    entidad.getItems().removeIf(
            entidadItem ->
                    idsEliminar.contains(entidadItem.getId())
    );
}



    // Actualizar los datos generales
    entidad.setEstado(pedido.getEstado());
    entidad.setTotal(pedido.getTotal().valor());


    // Hibernate guarda los cambios al finalizar la transacción.
}

@Override
public Optional<Pedido> buscarPorId(Long id) {
    return jpa.findById(id)
            .map(PedidoMapper::aDominio);
}

@Override
public List<Pedido> listarTodos() {
    return jpa.findAll()
            .stream()
            .map(PedidoMapper::aDominio)
            .toList();
}

@Override
public void eliminar(Pedido pedido) {
    jpa.deleteById(pedido.getId());
}

@Override
public List<Pedido> listarPorMesa(Mesa mesa) {
    EntidadMesa em = MesaMapper.aEntidad(mesa);

    return jpa.findByMesa(em)
            .stream()
            .map(PedidoMapper::aDominio)
            .toList();
}

@Override
@Transactional(readOnly = true)
public List<Pedido> listarPorEstado(EstadoPedido estado) {

    return jpa.findByEstado(estado)
            .stream()
            .map(PedidoMapper::aDominio)
            .toList();
}

@Override
@Transactional(readOnly = true)
public List<Pedido> listarActivosPorMesa(Mesa mesa) {

    EntidadMesa em = MesaMapper.aEntidad(mesa);

    List<EstadoPedido> finalizados = List.of(
            EstadoPedido.PAGADO,
            EstadoPedido.CANCELADO
    );

    return jpa.findByMesaAndEstadoNotIn(em, finalizados)
            .stream()
            .map(PedidoMapper::aDominio)
            .toList();
}

@Override
public List<Pedido> listarPorMesaYEstado(
        Mesa mesa, EstadoPedido estado) {

    EntidadMesa em = MesaMapper.aEntidad(mesa);

    return jpa.findByMesaAndEstado(em, estado)
            .stream()
            .map(PedidoMapper::aDominio)
            .toList();
}

@Override
@Transactional
public Optional<Pedido> buscarPorIdConBloqueo(Long id) {

    if (id == null) {
        throw new IllegalArgumentException(
                "El ID del pedido no puede ser nulo"
        );
    }

    return jpa.buscarPorIdConBloqueo(id)
            .map(PedidoMapper::aDominio);
}

}

