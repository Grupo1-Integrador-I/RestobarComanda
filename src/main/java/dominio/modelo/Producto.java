package dominio.modelo;
import java.util.Objects;
/**
 *
 * @author inici4rsesi0n
 */
public class Producto{
    private Long id;
    private String nombre;
    private Monto precio;
    private CategoriaProducto categoria;
    private boolean disponible;
    protected Producto(){}
    public Producto(String nombre, Monto precio, CategoriaProducto categoria){
        if(nombre==null||nombre.isBlank())
            throw new IllegalArgumentException("Nombre no puede ser nulo/vacío");
        if(precio==null||precio.esCero())
            throw new IllegalArgumentException("Precio debe ser mayor a cero");
        if(categoria==null)
            throw new IllegalArgumentException("Categoría no puede ser nula");
        this.nombre=nombre;
        this.precio=precio;
        this.categoria=categoria;
        this.disponible=true;
    }
    public void desactivar(){
        if(!this.disponible)
            throw new IllegalStateException("El producto ya está desactivado");
        this.disponible=false;
    }
    public void activar(){
        if(this.disponible)
            throw new IllegalStateException("El producto ya está activo");
        this.disponible=true;
    }
    public boolean estaDisponible(){
        return this.disponible;
    }
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public String getNombre(){return nombre;}
    public void setNombre(String nombre){this.nombre=nombre;}
    public Monto getPrecio(){return precio;}
    public void setPrecio(Monto precio){this.precio=precio;}
    public CategoriaProducto getCategoria(){return categoria;}
    @Override
    public boolean equals(Object o){
        if(this==o) return true;
        if(!(o instanceof Producto producto)) return false;
        return Objects.equals(nombre, producto.nombre);
    }
    @Override
    public int hashCode(){return Objects.hash(nombre);}
    @Override
    public String toString(){return "Producto [nombre=" + nombre +
        ", precio=" + precio + ", categoria=" + categoria + ", disponible=" + disponible + "]";}
}

