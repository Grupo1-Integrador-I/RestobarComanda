package infraestructura.persistencia.entidad;
import dominio.modelo.CategoriaProducto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@Table(name="productos")
public class EntidadProducto{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=100)
    private String nombre;
    @Column(nullable=false, precision=10, scale=2)
    private BigDecimal precio;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private CategoriaProducto categoria;
    @Column(nullable=false)
    private boolean disponible;
    public EntidadProducto(){}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public BigDecimal getPrecio(){return precio;}
    public void setPrecio(BigDecimal precio){this.precio=precio;}
    public CategoriaProducto getCategoria(){return categoria;}
    public void setCategoria(CategoriaProducto categoria){this.categoria=categoria;}
    public boolean isDisponible(){return disponible;}
    public void setDisponible(boolean disponible){this.disponible=disponible;}
}
