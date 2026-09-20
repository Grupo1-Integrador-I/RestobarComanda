package infraestructura.persistencia.entidad;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@Table(name="items_pedido")
public class EntidadItemPedido{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="pedido_id", nullable=false)
    private EntidadPedido pedido;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="producto_id", nullable=false)
    private EntidadProducto producto;
    @Column(nullable=false)
    private int cantidad;
    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal subtotal;
    public EntidadItemPedido(){}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public EntidadPedido getPedido(){return pedido;}
    public void setPedido(EntidadPedido pedido){this.pedido=pedido;}
    public EntidadProducto getProducto(){return producto;}
    public void setProducto(EntidadProducto producto){this.producto=producto;}
    public int getCantidad(){return cantidad;}
    public void setCantidad(int cantidad){this.cantidad=cantidad;}
    public BigDecimal getSubtotal(){return subtotal;}
    public void setSubtotal(BigDecimal subtotal){this.subtotal=subtotal;}
}
