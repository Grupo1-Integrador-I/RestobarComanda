package infraestructura.persistencia.mapper;
import dominio.modelo.Monto;
import dominio.modelo.Pago;
import dominio.modelo.Pedido;
import infraestructura.persistencia.entidad.EntidadPago;
/**
 *
 * @author inici4rsesi0n
 */
public class PagoMapper{
    private PagoMapper(){}
    public static Pago aDominio(EntidadPago e){
        if(e==null)
            return null;
        Pedido pedido=PedidoMapper.aDominio(e.getPedido());
        return Pago.reconstruir(pedido, new Monto(e.getMonto()),
                e.getTipo(), e.getFecha(), e.getId());
    }
    public static EntidadPago aEntidad(Pago p){
        if(p==null)
            return null;
        EntidadPago e=new EntidadPago();
        e.setId(p.getId());
        e.setPedido(PedidoMapper.aEntidad(p.getPedido()));
        e.setMonto(p.getMonto().valor());
        e.setTipo(p.getTipo());
        e.setFecha(p.getFecha());
        return e;
    }
}
