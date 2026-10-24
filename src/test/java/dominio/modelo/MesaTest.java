package dominio.modelo;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
/**
 *
 * @author inici4rsesi0n
 */
class MesaTest{
    @Test
    void constructorRechazaNumeroNulo(){
        assertThatThrownBy(()->new Mesa(null, "Salón"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Número");
    }
    @Test
    void constructorRechazaNumeroVacio(){
        assertThatThrownBy(()->new Mesa("", "Salón"))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void constructorRechazaUbicacionNula(){
        assertThatThrownBy(()->new Mesa("M1", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ubicación");
    }
    @Test
    void constructorRechazaUbicacionVacia(){
        assertThatThrownBy(()->new Mesa("M1", ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
    @Test
    void mesaNuevaEstaLibre(){
        Mesa m=new Mesa("M1", "Salón");
        assertThat(m.estaLibre()).isTrue();
        assertThat(m.getEstado()).isEqualTo(EstadoMesa.LIBRE);
    }
    @Test
    void ocuparFunciona(){
        Mesa m=new Mesa("M1", "Salón");
        m.ocupar();
        assertThat(m.estaLibre()).isFalse();
        assertThat(m.getEstado()).isEqualTo(EstadoMesa.OCUPADO);
    }
    @Test
    void ocuparDosVecesFalla(){
        Mesa m=new Mesa("M1", "Salón");
        m.ocupar();
        assertThatThrownBy(m::ocupar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ocupada");
    }
    @Test
    void liberarFunciona(){
        Mesa m=new Mesa("M1", "Salón");
        m.ocupar();
        m.liberar();
        assertThat(m.estaLibre()).isTrue();
    }
    @Test
    void liberarDosVecesFalla(){
        Mesa m=new Mesa("M1", "Salón");
        assertThatThrownBy(m::liberar)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("libre");
    }
    @Test
    void equalsPorNumero(){
        Mesa m1=new Mesa("M1", "Salón");
        Mesa m2=new Mesa("M1", "Terraza");
        assertThat(m1).isEqualTo(m2);
    }
    @Test
    void equalsFalsoConDistintoNumero(){
        Mesa m1=new Mesa("M1", "Salón");
        Mesa m2=new Mesa("M2", "Salón");
        assertThat(m1).isNotEqualTo(m2);
    }
    @Test
    void hashCodeIgualParaMismoNumero(){
        Mesa m1=new Mesa("M1", "Salón");
        Mesa m2=new Mesa("M1", "Terraza");
        assertThat(m1.hashCode()).isEqualTo(m2.hashCode());
    }
    @Test
    void settersFuncionan(){
        Mesa m=new Mesa("M1", "Salón");
        m.setNumero("M2");
        m.setUbicacion("Terraza");
        assertThat(m.getNumero()).isEqualTo("M2");
        assertThat(m.getUbicacion()).isEqualTo("Terraza");
    }
}
