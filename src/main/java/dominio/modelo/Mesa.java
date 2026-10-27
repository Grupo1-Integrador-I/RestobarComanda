package dominio.modelo;
import java.util.Objects;
/**
 *
 * @author inici4rsesi0n
 */
public class Mesa{
    private Long id;
    private String numero;
    private String ubicacion;
    private EstadoMesa estado;
    protected Mesa(){}
    public Mesa(String numero, String ubicacion){
        if(numero==null||numero.isBlank())
            throw new IllegalArgumentException("Número no puede ser nulo/vacío");
        if(ubicacion==null||ubicacion.isBlank())
            throw new IllegalArgumentException("Ubicación no puede ser nula/vacía");
        this.numero=numero;
        this.ubicacion=ubicacion;
        this.estado=EstadoMesa.LIBRE;
    }
    public void ocupar(){
        if(this.estado==EstadoMesa.OCUPADO)
            throw new IllegalStateException("La mesa ya está ocupada");
        this.estado=EstadoMesa.OCUPADO;
    }
    public void liberar(){
        if(this.estado==EstadoMesa.LIBRE)
            throw new IllegalStateException("La mesa ya está libre");
        this.estado=EstadoMesa.LIBRE;
    }
    public boolean estaLibre(){
        return this.estado==EstadoMesa.LIBRE;
    }
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNumero(){return numero;}
    public void setNumero(String numero){this.numero=numero;}
    public String getUbicacion(){return ubicacion;}
    public void setUbicacion(String ubicacion){this.ubicacion=ubicacion;}
    public EstadoMesa getEstado(){return estado;}
    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof Mesa mesa)) return false;
        return Objects.equals(numero, mesa.numero);
    }
    @Override
    public int hashCode(){return Objects.hash(numero);}
    @Override
    public String toString(){return "Mesa [numero=" + numero +
        ", ubicacion=" + ubicacion + ", estado=" + estado + "]";}
}

