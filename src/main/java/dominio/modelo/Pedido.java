package dominio.modelo;
import dominio.reglas.CalcularTotal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
public class Pedido{
    private Long id;
    private Mesa mesa;
    private Mesero mesero;
    private final List<ItemPedido> items=new ArrayList<>();
    private Monto total;
    private EstadoPedido estado;
    private LocalDateTime fechaCreacion;
    protected Pedido(){}
    public Pedido(Mesa mesa, Mesero mesero){
        if(mesa==null)
            throw new IllegalArgumentException("Mesa no puede ser nula");
        if(mesero==null)
            throw new IllegalArgumentException("Mesero no puede ser nulo");
        this.mesa=mesa;
        this.mesero=mesero;
        this.total=Monto.cero();
        this.estado=EstadoPedido.CREADO;
        this.fechaCreacion=LocalDateTime.now();
    }
    public static Pedido reconstruir(Mesa mesa, Mesero mesero, List<ItemPedido> items,
        Monto total, EstadoPedido estado, LocalDateTime fechaCreacion, Long id){
        Pedido p=new Pedido(mesa, mesero);
        p.items.clear();
        p.items.addAll(items);
        p.total=total;
        p.estado=estado;
        p.fechaCreacion=fechaCreacion;
        p.id=id;
        return p;
    }
    public void agregarItem(Producto producto, int cantidad){
        if(estado!=EstadoPedido.CREADO)
            throw new IllegalStateException("Solo se pueden agregar ítems a un pedido en estado CREADO");
        for (ItemPedido item:items){
            if(item.getProducto().equals(producto)){
                item.actualizarCantidad(item.getCantidad()+cantidad);
                recalcularTotal();
                return;
            }
        }
        items.add(new ItemPedido(producto, cantidad));
        recalcularTotal();
    }
    public void eliminarItem(Producto producto){
        if(estado!=EstadoPedido.CREADO)
            throw new IllegalStateException("Solo se pueden eliminar ítems de un pedido en estado CREADO");
        boolean eliminado=items.removeIf(i->i.getProducto().equals(producto));
        if(!eliminado)
            throw new IllegalArgumentException("El producto no está en el pedido");
        recalcularTotal();
    }
    public void enviar(){
        if(estado!=EstadoPedido.CREADO)
            throw new IllegalStateException("Solo se puede enviar un pedido en estado CREADO");
        if(items.isEmpty())
            throw new IllegalStateException("No se puede enviar un pedido sin ítems");
        this.estado=EstadoPedido.ENVIADO;
    }
    public void marcarEnPreparacion(){
        if(estado!=EstadoPedido.ENVIADO)
            throw new IllegalStateException("Solo se puede marcar en preparación un pedido ENVIADO");
        this.estado=EstadoPedido.EN_PREPARACION;
    }
    public void marcarListo(){
        if(estado!=EstadoPedido.EN_PREPARACION)
            throw new IllegalStateException("Solo se puede marcar listo un pedido EN_PREPARACION");
        this.estado=EstadoPedido.LISTO;
    }
    public void marcarEntregado(){
        if(estado!=EstadoPedido.LISTO)
            throw new IllegalStateException("Solo se puede marcar entregado un pedido LISTO");
        this.estado=EstadoPedido.ENTREGADO;
    }
    public void marcarPagado(){
        if(estado!=EstadoPedido.ENTREGADO)
            throw new IllegalStateException("Solo se puede marcar pagado un pedido ENTREGADO");
        this.estado=EstadoPedido.PAGADO;
    }
    public void cancelar(){
        if(estado==EstadoPedido.PAGADO)
            throw new IllegalStateException("No se puede cancelar un pedido ya pagado");
        this.estado=EstadoPedido.CANCELADO;
    }
    private void recalcularTotal(){
        this.total=CalcularTotal.de(items);
    }
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Mesa getMesa(){return mesa;}
    public Mesero getMesero(){return mesero;}
    public int cantidadItems(){return items.size();}
    public List<ItemPedido> getItems(){return List.copyOf(items);}
    public Monto getTotal(){return total;}
    public EstadoPedido getEstado(){return estado;}
    public LocalDateTime getFechaCreacion(){return fechaCreacion;}
    @Override
    public String toString() {
        return "Pedido [mesa=" + mesa.getNumero() +
               ", mesero=" + mesero.getNombre() +
               ", items=" + cantidadItems() +
               ", total=" + total + ", estado=" + estado + "]";
    }
}
