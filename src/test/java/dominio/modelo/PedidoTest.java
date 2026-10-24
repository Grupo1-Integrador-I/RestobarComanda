package dominio.modelo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
/**
 *
 * @author inici4rsesi0n
 */
class PedidoTest{
    private Mesa mesa;
    private Mesero mesero;
    private Producto ceviche;
    private Producto chicha;
    private Pedido pedido;
    @BeforeEach
    void setup(){
        mesa=new Mesa("M1", "Salón");
        mesero=new Mesero("juan", "hashfake123456", "12345678", "Juan", "Pérez");
        ceviche=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        chicha=new Producto("Chicha", Monto.de("8.00"), CategoriaProducto.BARRA);
        pedido=new Pedido(mesa, mesero);
    }
    @Test
    void constructorRechazaMesaNula(){
        assertThatThrownBy(()->new Pedido(null, mesero))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mesa");
    }
    @Test
    void constructorRechazaMeseroNulo(){
        assertThatThrownBy(()->new Pedido(mesa, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Mesero");
    }
    @Test
    void pedidoNuevoEstaEnCreado(){
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CREADO);
        assertThat(pedido.getTotal().esCero()).isTrue();
        assertThat(pedido.cantidadItems()).isZero();
    }
    @Test
    void agregarItemRecalculaTotal(){
        pedido.agregarItem(ceviche, 2);
        assertThat(pedido.getTotal().valor()).isEqualByComparingTo("51.00");
        assertThat(pedido.cantidadItems()).isEqualTo(1);
    }
    @Test
    void agregarMismoItemSumaCantidad(){
        pedido.agregarItem(ceviche, 1);
        pedido.agregarItem(ceviche, 2);
        assertThat(pedido.cantidadItems()).isEqualTo(1);
        assertThat(pedido.getItems().get(0).getCantidad()).isEqualTo(3);
        assertThat(pedido.getTotal().valor()).isEqualByComparingTo("76.50");
    }
    @Test
    void agregarVariosItemsSumaTotales(){
        pedido.agregarItem(ceviche, 1);
        pedido.agregarItem(chicha, 2);
        assertThat(pedido.cantidadItems()).isEqualTo(2);
        assertThat(pedido.getTotal().valor()).isEqualByComparingTo("41.50");
    }
    @Test
    void eliminarItemRecalculaTotal(){
        pedido.agregarItem(ceviche, 2);
        pedido.eliminarItem(ceviche);
        assertThat(pedido.cantidadItems()).isZero();
        assertThat(pedido.getTotal().esCero()).isTrue();
    }
    @Test
    void eliminarItemInexistenteFalla(){
        assertThatThrownBy(()->pedido.eliminarItem(ceviche))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no está en el pedido");
    }
    @Test
    void enviarSinItemsFalla(){
        assertThatThrownBy(pedido::enviar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("sin ítems");
    }
    @Test
    void enviarConItemsFunciona(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.ENVIADO);
    }
    @Test
    void agregarItemDespuesDeEnviadoFalla(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        assertThatThrownBy(()->pedido.agregarItem(chicha, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("CREADO");
    }
    @Test
    void eliminarItemDespuesDeEnviadoFalla(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        assertThatThrownBy(()->pedido.eliminarItem(ceviche))
                .isInstanceOf(IllegalStateException.class);
    }
    @Test
    void marcarEnPreparacionSinEnviadoFalla(){
        pedido.agregarItem(ceviche, 1);
        assertThatThrownBy(pedido::marcarEnPreparacion)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ENVIADO");
    }
    @Test
    void flujoCompletoHastaPagado(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        pedido.marcarEnPreparacion();
        pedido.marcarListo();
        pedido.marcarEntregado();
        pedido.marcarPagado();
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.PAGADO);
    }
    @Test
    void marcarListoSinPreparacionFalla(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        assertThatThrownBy(pedido::marcarListo)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EN_PREPARACION");
    }
    @Test
    void marcarEntregadoSinListoFalla(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        pedido.marcarEnPreparacion();
        assertThatThrownBy(pedido::marcarEntregado)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("LISTO");
    }
    @Test
    void marcarPagadoSinEntregadoFalla(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        pedido.marcarEnPreparacion();
        pedido.marcarListo();
        assertThatThrownBy(pedido::marcarPagado)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ENTREGADO");
    }
    @Test
    void cancelarDesdeCreadoFunciona(){
        pedido.agregarItem(ceviche, 1);
        pedido.cancelar();
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
    }
    @Test
    void cancelarDesdeEnviadoFunciona(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        pedido.cancelar();
        assertThat(pedido.getEstado()).isEqualTo(EstadoPedido.CANCELADO);
    }
    @Test
    void cancelarDesdePagadoFalla(){
        pedido.agregarItem(ceviche, 1);
        pedido.enviar();
        pedido.marcarEnPreparacion();
        pedido.marcarListo();
        pedido.marcarEntregado();
        pedido.marcarPagado();
        assertThatThrownBy(pedido::cancelar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pagado");
    }
    @Test
    void getItemsEsInmutable(){
        pedido.agregarItem(ceviche, 1);
        assertThatThrownBy(()->pedido.getItems().add(null))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
