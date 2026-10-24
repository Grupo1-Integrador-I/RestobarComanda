package dominio.modelo;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
/**
 *
 * @author inici4rsesi0n
 */
class MontoTest{
    @Test
    void constructorAceptaValorPositivo(){
        Monto m=new Monto(new BigDecimal("25.50"));
        assertThat(m.valor()).isEqualByComparingTo("25.50");
    }
    @Test
    void constructorAceptaCero(){
        Monto m=new Monto(BigDecimal.ZERO);
        assertThat(m.esCero()).isTrue();
        assertThat(m.valor()).isEqualByComparingTo("0.00");
    }
    @Test
    void constructorRechazaNulo(){
        assertThatThrownBy(()->new Monto(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nulo");
    }
    @Test
    void constructorRechazaNegativo(){
        assertThatThrownBy(()->new Monto(new BigDecimal("-1.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativo");
    }
    @Test
    void constructorRedondeaHalfUpHaciaArriba(){
        Monto m=new Monto(new BigDecimal("10.005"));
        assertThat(m.valor()).isEqualByComparingTo("10.01");
        assertThat(m.valor().scale()).isEqualTo(2);
    }
    @Test
    void constructorRedondeaHalfUpHaciaAbajo(){
        Monto m=new Monto(new BigDecimal("10.004"));
        assertThat(m.valor()).isEqualByComparingTo("10.00");
    }
    @Test
    void constructorNormalizaEscala(){
        Monto m=new Monto(new BigDecimal("5"));
        assertThat(m.valor().scale()).isEqualTo(2);
    }
    @Test
    void montoCeroEsCeroConEscalaDos(){
        Monto m=Monto.cero();
        assertThat(m.esCero()).isTrue();
        assertThat(m.valor().scale()).isEqualTo(2);
    }
    @Test
    void deParseaString(){
        assertThat(Monto.de("25.50").valor()).isEqualByComparingTo("25.50");
    }
    @Test
    void deStringInvalidoLanzaNumberFormatException(){
        assertThatThrownBy(()->Monto.de("abc"))
                .isInstanceOf(NumberFormatException.class);
    }
    @Test
    void sumarDosMontos(){
        Monto resultado=Monto.de("10.50").sumar(Monto.de("5.25"));
        assertThat(resultado.valor()).isEqualByComparingTo("15.75");
    }
    @Test
    void restarDosMontos(){
        Monto resultado=Monto.de("10.00").restar(Monto.de("3.50"));
        assertThat(resultado.valor()).isEqualByComparingTo("6.50");
    }
    @Test
    void restarConResultadoNegativoFalla(){
        assertThatThrownBy(()->Monto.de("3.00").restar(Monto.de("5.00")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativo");
    }
    @Test
    void multiplicarPorEntero(){
        Monto resultado=Monto.de("25.50").multiplicar(3);
        assertThat(resultado.valor()).isEqualByComparingTo("76.50");
    }
    @Test
    void multiplicarPorCeroDaCero(){
        assertThat(Monto.de("25.50").multiplicar(0).esCero()).isTrue();
    }
    @Test
    void multiplicarPorNegativoFalla(){
        assertThatThrownBy(()->Monto.de("25.50").multiplicar(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("negativo");
    }
    @Test
    void esMayorQueVerdadero(){
        assertThat(Monto.de("10.00").esMayorQue(Monto.de("5.00"))).isTrue();
    }
    @Test
    void esMayorQueFalsoSiIguales(){
        assertThat(Monto.de("10.00").esMayorQue(Monto.de("10.00"))).isFalse();
    }
    @Test
    void esMayorQueFalsoSiMenor(){
        assertThat(Monto.de("5.00").esMayorQue(Monto.de("10.00"))).isFalse();
    }
    @Test
    void igualdadConDistintaEscalaNormalizada(){
        Monto m1=Monto.de("10.0");
        Monto m2=Monto.de("10.00");
        assertThat(m1).isEqualTo(m2);
    }
    @Test
    void toStringIncluyeSimboloYValor(){
        assertThat(Monto.de("25.50").toString()).contains("25.50");
    }
}
