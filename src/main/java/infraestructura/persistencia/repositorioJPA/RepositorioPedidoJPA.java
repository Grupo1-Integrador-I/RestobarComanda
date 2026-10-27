
package infraestructura.persistencia.repositorioJPA;

import dominio.modelo.EstadoPedido;
import infraestructura.persistencia.entidad.EntidadMesa;
import infraestructura.persistencia.entidad.EntidadPedido;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositorioPedidoJPA
        extends JpaRepository<EntidadPedido, Long> {

    @Override
    @EntityGraph(attributePaths = {
        "mesa",
        "mesero",
        "items",
        "items.producto"
    })
    Optional<EntidadPedido> findById(Long id);

    List<EntidadPedido> findByMesa(EntidadMesa mesa);

    List<EntidadPedido> findByEstado(EstadoPedido estado);

    List<EntidadPedido> findByMesaAndEstado(
            EntidadMesa mesa,
            EstadoPedido estado
    );

    List<EntidadPedido> findByMesaAndEstadoNotIn(
            EntidadMesa mesa,
            List<EstadoPedido> estados
    );
    
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT p FROM EntidadPedido p WHERE p.id = :id")
Optional<EntidadPedido> buscarPorIdConBloqueo(
        @Param("id") Long id
);

}
