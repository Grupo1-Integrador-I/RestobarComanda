package boot;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Mesa;
import dominio.modelo.Mesero;
import dominio.modelo.Monto;
import dominio.modelo.Pedido;
import dominio.modelo.Producto;
import infraestructura.persistencia.adaptador.AdaptadorRepositorioMesa;
import infraestructura.persistencia.adaptador.AdaptadorRepositorioPedido;
import infraestructura.persistencia.adaptador.AdaptadorRepositorioProducto;
import infraestructura.persistencia.adaptador.AdaptadorRepositorioUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
/**
 *
 * @author inici4rsesi0n
 */
@DataJpaTest
@Import({AdaptadorRepositorioMesa.class,
         AdaptadorRepositorioUsuario.class,
         AdaptadorRepositorioProducto.class,
         AdaptadorRepositorioPedido.class})
class PedidoMapperIT{
    @Autowired AdaptadorRepositorioMesa repoMesa;
    @Autowired AdaptadorRepositorioUsuario repoUsuario;
    @Autowired AdaptadorRepositorioProducto repoProducto;
    @Autowired AdaptadorRepositorioPedido repoPedido;
    @Test
    void roundTripPedidoConItemsYOrphanRemoval(){
        Mesa mesa=new Mesa("M1", "Salón");
        repoMesa.agregar(mesa);
        Mesero mesero=new Mesero("m1", "hash", "11111111", "Juan", "Perez");
        repoUsuario.agregar(mesero);
        Producto producto=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        repoProducto.agregar(producto);
        Mesa mesaPersistida=repoMesa.buscarPorNumero("M1").orElseThrow();
        Mesero meseroPersistido=(Mesero)repoUsuario.buscarPorCodigo("m1").orElseThrow();
        Producto productoPersistido=repoProducto.buscarPorNombre("Ceviche").orElseThrow();
        Pedido pedido=new Pedido(mesaPersistida, meseroPersistido);
        pedido.agregarItem(productoPersistido, 2);
        repoPedido.agregar(pedido);
        assertNotNull(pedido.getId(), "El adaptador debe propagar el ID generado");
        Pedido recargado=repoPedido.buscarPorId(pedido.getId()).orElseThrow();
        assertEquals(1, recargado.cantidadItems());
        assertEquals(Monto.de("51.00"), recargado.getTotal());
        recargado.eliminarItem(productoPersistido);
        repoPedido.actualizar(recargado);
        Pedido sinItems=repoPedido.buscarPorId(pedido.getId()).orElseThrow();
        assertEquals(0, sinItems.cantidadItems());
        assertEquals(Monto.de("0.00"), sinItems.getTotal());
    }
}
