package infraestructura.persistencia.entidad;
import dominio.modelo.EstadoMesa;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
/**
 *
 * @author inici4rsesi0n
 */
@Entity
@Table(name="mesas")
public class EntidadMesa{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=20)
    private String numero;
    @Column(nullable=false, length=100)
    private String ubicacion;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private EstadoMesa estado;
    public EntidadMesa(){}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNumero(){return numero;}
    public void setNumero(String numero){this.numero=numero;}
    public String getUbicacion(){return ubicacion;}
    public void setUbicacion(String ubicacion){this.ubicacion=ubicacion;}
    public EstadoMesa getEstado(){return estado;}
    public void setEstado(EstadoMesa estado){this.estado=estado;}
}
