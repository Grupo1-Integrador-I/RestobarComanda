package infraestructura.persistencia.entidad;
import dominio.modelo.Rol;
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
@Table(name="usuarios")
public class EntidadUsuario{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    @Column(nullable=false, unique=true, length=50)
    private String codigo;
    @Column(nullable=false, length=100)
    private String hashContrasena;
    @Column(nullable=false, unique=true, length=20)
    private String dni;
    @Column(nullable=false, length=100)
    private String nombre;
    @Column(nullable=false, length=100)
    private String apellido;
    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private Rol rol;
    public EntidadUsuario(){}
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getCodigo(){return codigo;}
    public void setCodigo(String codigo){this.codigo=codigo;}
    public String getHashContrasena(){return hashContrasena;}
    public void setHashContrasena(String hashContrasena){this.hashContrasena=hashContrasena;}
    public String getDni(){return dni;}
    public void setDni(String dni){this.dni=dni;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public String getApellido(){return apellido;}
    public void setApellido(String apellido){this.apellido=apellido;}
    public Rol getRol(){return rol;}
    public void setRol(Rol rol){this.rol=rol;}
}
