package infraestructura.persistencia.entidad;
import dominio.modelo.TipoPago;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@Table(name="pagos")
public class EntidadPago{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="pedido_id", nullable=false, unique=true)
    private EntidadPedido pedido;
    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal monto;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private TipoPago tipo;
    @Column(nullable=false)
    private LocalDateTime fecha;
    public EntidadPago(){}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public EntidadPedido getPedido(){return pedido;}
    public void setPedido(EntidadPedido pedido){this.pedido=pedido;}
    public BigDecimal getMonto(){return monto;}
    public void setMonto(BigDecimal monto){this.monto=monto;}
    public TipoPago getTipo(){return tipo;}
    public void setTipo(TipoPago tipo){this.tipo=tipo;}
    public LocalDateTime getFecha(){return fecha;}
    public void setFecha(LocalDateTime fecha){this.fecha=fecha;}
}
