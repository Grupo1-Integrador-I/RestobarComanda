package dominio.modelo;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
/**
 *
 * @author inici4rsesi0n
 */
class ItemPedidoTest{
    private Producto productoValido(){
        return new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
    }
    @Test
    void constructorRechazaProductoNulo(){
        assertThatThrownBy(()->new ItemPedido(null, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Producto");
    }
    @Test
    void constructorRechazaCantidadCero(){
        assertThatThrownBy(()->new ItemPedido(productoValido(), 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cantidad");
    }
    @Test
    void constructorRechazaCantidadNegativa(){
        assertThatThrownBy(()->new ItemPedido(productoValido(), -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Cantidad");
    }
    @Test
    void subtotalSeCalculaAutomaticamente(){
        ItemPedido item=new ItemPedido(productoValido(), 3);
        assertThat(item.getSubtotal().valor()).isEqualByComparingTo("76.50");
        assertThat(item.getCantidad()).isEqualTo(3);
    }
    @Test
    void actualizarCantidadRecalculaSubtotal(){
        ItemPedido item=new ItemPedido(productoValido(), 2);
        item.actualizarCantidad(5);
        assertThat(item.getCantidad()).isEqualTo(5);
        assertThat(item.getSubtotal().valor()).isEqualByComparingTo("127.50");
    }
    @Test
    void actualizarCantidadCeroFalla(){
        ItemPedido item=new ItemPedido(productoValido(), 2);
        assertThatThrownBy(()->item.actualizarCantidad(0))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void actualizarCantidadNegativaFalla(){
        ItemPedido item=new ItemPedido(productoValido(), 2);
        assertThatThrownBy(()->item.actualizarCantidad(-3))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void getIdYSetIdFuncionan(){
        ItemPedido item=new ItemPedido(productoValido(), 1);
        assertThat(item.getId()).isNull();
        item.setId(42L);
        assertThat(item.getId()).isEqualTo(42L);
    }
}
