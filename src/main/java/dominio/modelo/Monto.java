package dominio.modelo;
import java.math.BigDecimal;
import java.math.RoundingMode;
/**
 *
 * @author inici4rsesi0n
 */
public record Monto(BigDecimal valor){
    public Monto{
        if (valor==null)
            throw new IllegalArgumentException("Monto no puede ser nulo");
        if (valor.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Monto no puede ser negativo");
        valor = valor.setScale(2, RoundingMode.HALF_UP);
    }
    public static Monto de(String valor){return new Monto(new BigDecimal(valor));}
    public static Monto cero(){return new Monto(BigDecimal.ZERO);}
    public Monto sumar(Monto otro){return new Monto(this.valor.add(otro.valor));}
    public Monto restar(Monto otro){return new Monto(this.valor.subtract(otro.valor));}
    public Monto multiplicar(int cantidad){return new Monto(this.valor.multiply(BigDecimal.valueOf(cantidad)));}
    public boolean esMayorQue(Monto otro){return this.valor.compareTo(otro.valor) > 0;}
    public boolean esCero(){return this.valor.compareTo(BigDecimal.ZERO) == 0;}
    @Override
    public String toString(){return "S/ " + valor;}
}
