package dominio.modelo;
import java.time.LocalDateTime;
/**
 *
 * @author inici4rsesi0n
 */
public class Pago{
    private Long id;
    private Pedido pedido;
    private Monto monto;
    private TipoPago tipo;
    private LocalDateTime fecha;
    protected Pago(){}
    public static Pago reconstruir(Pedido pedido, Monto monto, TipoPago tipo,
                                   LocalDateTime fecha, Long id){
        Pago p=new Pago(pedido, tipo);
        p.monto=monto;
        p.fecha=fecha;
        p.id=id;
        return p;
    }
    public Pago(Pedido pedido, TipoPago tipo){
        if(pedido==null)
            throw new IllegalArgumentException("Pedido no puede ser nulo");
        if(tipo==null)
            throw new IllegalArgumentException("Tipo de pago no puede ser nulo");
        this.pedido=pedido;
        this.monto=pedido.getTotal();
        this.tipo=tipo;
        this.fecha=LocalDateTime.now();
    }
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Pedido getPedido(){return pedido;}
    public Monto getMonto(){return monto;}
    public TipoPago getTipo(){return tipo;}
    public LocalDateTime getFecha(){return fecha;}
    @Override
    public String toString() {
        return "Pago [pedido=" + pedido.getMesa().getNumero() +
               ", monto=" + monto + ", tipo=" + tipo + "]";
    }
}
