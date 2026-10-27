package dominio.modelo;
/**
 *
 * @author inici4rsesi0n
 */
public class ItemPedido{
    private Long id;
    private Producto producto;
    private int cantidad;
    private Monto subtotal;
    private EstadoPreparacion estadoPreparacion;
    protected ItemPedido(){}
    public ItemPedido(Producto producto, int cantidad){
        if (producto==null)
            throw new IllegalArgumentException("Producto no puede ser nulo");
        if (cantidad<=0)
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        this.producto=producto;
        this.cantidad=cantidad;
        this.subtotal=producto.getPrecio().multiplicar(cantidad);
        this.estadoPreparacion=EstadoPreparacion.PENDIENTE;
    }
    public void actualizarCantidad(int nuevaCantidad){
        if (nuevaCantidad<=0)
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        this.cantidad=nuevaCantidad;
        this.subtotal=producto.getPrecio().multiplicar(nuevaCantidad);
    }
    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}
    public Producto getProducto(){return producto;}
    public int getCantidad(){return cantidad;}
    public Monto getSubtotal(){return subtotal;}
   
    
public EstadoPreparacion getEstadoPreparacion() {
    return estadoPreparacion;
}

public void marcarEnPreparacion() {
    if (estadoPreparacion != EstadoPreparacion.PENDIENTE) {
        throw new IllegalStateException(
            "Solo se puede iniciar un producto PENDIENTE"
        );
    }
    this.estadoPreparacion = EstadoPreparacion.EN_PREPARACION;
}

public void marcarListo() {
    if (estadoPreparacion != EstadoPreparacion.EN_PREPARACION) {
        throw new IllegalStateException(
            "Solo se puede marcar LISTO un producto EN_PREPARACION"
        );
    }
    this.estadoPreparacion = EstadoPreparacion.LISTO;
}

public void restaurarEstadoPreparacion(EstadoPreparacion estado) {
    if (estado == null) {
        throw new IllegalArgumentException(
            "El estado de preparación no puede ser nulo"
        );
    }
    this.estadoPreparacion = estado;
}
@Override
    public String toString() {
        return "ItemPedido [producto=" + producto.getNombre() +
               ", cantidad=" + cantidad + ", subtotal=" + subtotal + "]";
    }
}
