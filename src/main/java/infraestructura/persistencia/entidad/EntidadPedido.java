package infraestructura.persistencia.entidad;
import dominio.modelo.EstadoPedido;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@Table(name="pedidos")
public class EntidadPedido{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="mesa_id", nullable=false)
    private EntidadMesa mesa;
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="mesero_id", nullable=false)
    private EntidadUsuario mesero;
    @OneToMany(mappedBy="pedido", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<EntidadItemPedido> items=new ArrayList<>();
    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal total;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private EstadoPedido estado;
    @Column(nullable=false)
    private LocalDateTime fechaCreacion;
    public EntidadPedido(){}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public EntidadMesa getMesa(){return mesa;}
    public void setMesa(EntidadMesa mesa){this.mesa=mesa;}
    public EntidadUsuario getMesero(){return mesero;}
    public void setMesero(EntidadUsuario mesero){this.mesero=mesero;}
    public List<EntidadItemPedido> getItems(){return items;}
    public void setItems(List<EntidadItemPedido> items){this.items=items;}
    public BigDecimal getTotal(){return total;}
    public void setTotal(BigDecimal total){this.total=total;}
    public EstadoPedido getEstado(){return estado;}
    public void setEstado(EstadoPedido estado){this.estado=estado;}
    public LocalDateTime getFechaCreacion(){return fechaCreacion;}
    public void setFechaCreacion(LocalDateTime fechaCreacion){this.fechaCreacion=fechaCreacion;}
}
