package infraestructura.persistencia.mapper;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Monto;
import dominio.modelo.Producto;
import infraestructura.persistencia.entidad.EntidadProducto;
/**
 *
 * @author inici4rsesi0n
 */
public class ProductoMapper{
    private ProductoMapper(){}
    public static Producto aDominio(EntidadProducto e){
        if(e==null)
            return null;
        Producto p=new Producto(e.getNombre(), new Monto(e.getPrecio()),
                e.getCategoria());
        p.setId(e.getId());
        if(!e.isDisponible())
            p.desactivar();
        return p;
    }
    public static EntidadProducto aEntidad(Producto p){
        if(p==null)
            return null;
        EntidadProducto e=new EntidadProducto();
        e.setId(p.getId());
        e.setNombre(p.getNombre());
        e.setPrecio(p.getPrecio().valor());
        e.setCategoria(p.getCategoria());
        e.setDisponible(p.estaDisponible());
        return e;
    }
}
