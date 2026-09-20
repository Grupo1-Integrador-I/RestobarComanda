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
    protected ItemPedido(){}
    public ItemPedido(Producto producto, int cantidad){
        if (producto==null)
            throw new IllegalArgumentException("Producto no puede ser nulo");
        if (cantidad<=0)
            throw new IllegalArgumentException("Cantidad debe ser mayor a cero");
        this.producto=producto;
        this.cantidad=cantidad;
        this.subtotal=producto.getPrecio().multiplicar(cantidad);
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
    @Override
    public String toString() {
        return "ItemPedido [producto=" + producto.getNombre() +
               ", cantidad=" + cantidad + ", subtotal=" + subtotal + "]";
    }
}
