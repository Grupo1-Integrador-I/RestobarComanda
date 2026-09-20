package dominio.modelo;
import java.util.Objects;
/**
 *
 * @author inici4rsesi0n
 */
public abstract class Usuario{
    private Long id;
    private String codigo;
    private String hashContrasena;
    private String dni;
    private String nombre;
    private String apellido;
    private Rol rol;
    protected Usuario(){}
    public Usuario(String codigo, String hashContrasena, String dni,
            String nombre, String apellido, Rol rol){
        if (codigo == null || codigo.isBlank()) throw new IllegalArgumentException("Código no puede ser nulo/vacío");
        if (hashContrasena == null || hashContrasena.isBlank()) throw new IllegalArgumentException("Hash no puede ser nulo/vacío");
        if (dni == null || dni.isBlank()) throw new IllegalArgumentException("DNI no puede ser nulo/vacío");
        if (nombre == null || nombre.isBlank()) throw new IllegalArgumentException("Nombre no puede ser nulo/vacío");
        if (apellido == null || apellido.isBlank()) throw new IllegalArgumentException("Apellido no puede ser nulo/vacío");
        if (rol == null) throw new IllegalArgumentException("Rol no puede ser nulo");
        this.codigo = codigo;
        this.hashContrasena = hashContrasena;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.rol = rol;
    }
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getCodigo(){return codigo;}
    public void setCodigo(String codigo){this.codigo = codigo;}
    public String getHashContrasena() {return hashContrasena;}
    public void setHashContrasena(String hashContrasena){this.hashContrasena = hashContrasena;}
    public String getDni(){return dni;}
    public void setDni(String dni){this.dni = dni;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre = nombre;}
    public String getApellido(){return apellido;}
    public void setApellido(String apellido){this.apellido = apellido;}
    public Rol getRol(){return rol;}
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(codigo, usuario.codigo);
    }
    @Override
    public int hashCode() {return Objects.hash(codigo);}
    @Override
    public String toString() {
        return "Usuario [codigo=" + codigo + ", dni=" + dni + ", nombre=" + nombre +
               ", apellido=" + apellido + ", rol=" + rol + "]";
    }
}
