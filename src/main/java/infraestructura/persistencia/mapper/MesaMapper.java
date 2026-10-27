package infraestructura.persistencia.mapper;
import dominio.modelo.Mesa;
import infraestructura.persistencia.entidad.EntidadMesa;
/**
 *
 * @author inici4rsesi0n
 */
public class MesaMapper{
    private MesaMapper(){}
    public static Mesa aDominio(EntidadMesa e){
        if(e==null)
            return null;
        Mesa m=new Mesa(e.getNumero(), e.getUbicacion());
        m.setId(e.getId());
        if(e.getEstado()==dominio.modelo.EstadoMesa.OCUPADO)
            m.ocupar();
        return m;
    }
    public static EntidadMesa aEntidad(Mesa m){
        if(m==null)
            return null;
        EntidadMesa e=new EntidadMesa();
        e.setId(m.getId());
        e.setNumero(m.getNumero());
        e.setUbicacion(m.getUbicacion());
        e.setEstado(m.getEstado());
        return e;
    }
}
