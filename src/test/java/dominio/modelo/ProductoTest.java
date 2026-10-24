package dominio.modelo;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
/**
 *
 * @author inici4rsesi0n
 */
class ProductoTest{
    @Test
    void constructorRechazaNombreNulo(){
        assertThatThrownBy(()->new Producto(null, Monto.de("10.00"), CategoriaProducto.COCINA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Nombre");
    }
    @Test
    void constructorRechazaNombreVacio(){
        assertThatThrownBy(()->new Producto("", Monto.de("10.00"), CategoriaProducto.COCINA))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void constructorRechazaNombreBlank(){
        assertThatThrownBy(()->new Producto("   ", Monto.de("10.00"), CategoriaProducto.COCINA))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void constructorRechazaPrecioNulo(){
        assertThatThrownBy(()->new Producto("Ceviche", null, CategoriaProducto.COCINA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Precio");
    }
    @Test
    void constructorRechazaPrecioCero(){
        assertThatThrownBy(()->new Producto("Ceviche", Monto.cero(), CategoriaProducto.COCINA))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Precio");
    }
    @Test
    void constructorRechazaCategoriaNula(){
        assertThatThrownBy(()->new Producto("Ceviche", Monto.de("10.00"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Categoría");
    }
    @Test
    void productoNuevoEstaDisponible(){
        Producto p=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        assertThat(p.estaDisponible()).isTrue();
    }
    @Test
    void desactivarFunciona(){
        Producto p=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        p.desactivar();
        assertThat(p.estaDisponible()).isFalse();
    }
    @Test
    void desactivarDosVecesFalla(){
        Producto p=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        p.desactivar();
        assertThatThrownBy(p::desactivar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("desactivado");
    }
    @Test
    void activarFunciona(){
        Producto p=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        p.desactivar();
        p.activar();
        assertThat(p.estaDisponible()).isTrue();
    }
    @Test
    void activarDosVecesFalla(){
        Producto p=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        assertThatThrownBy(p::activar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("activo");
    }
    @Test
    void equalsPorNombre(){
        Producto p1=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        Producto p2=new Producto("Ceviche", Monto.de("30.00"), CategoriaProducto.BARRA);
        assertThat(p1).isEqualTo(p2);
    }
    @Test
    void equalsFalsoConDistintoNombre(){
        Producto p1=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        Producto p2=new Producto("Chicha", Monto.de("25.50"), CategoriaProducto.COCINA);
        assertThat(p1).isNotEqualTo(p2);
    }
    @Test
    void hashCodeIgualParaMismoNombre(){
        Producto p1=new Producto("Ceviche", Monto.de("25.50"), CategoriaProducto.COCINA);
        Producto p2=new Producto("Ceviche", Monto.de("30.00"), CategoriaProducto.BARRA);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }
}
