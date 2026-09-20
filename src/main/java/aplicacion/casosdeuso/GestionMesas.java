package aplicacion.casosdeuso;
import dominio.modelo.Mesa;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioMesa;
import org.springframework.stereotype.Service;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class GestionMesas{
    private final RepositorioMesa repositorioMesa;
    private final LoggerPort logger;
    public GestionMesas(RepositorioMesa repositorioMesa, LoggerPort logger){
        this.repositorioMesa=repositorioMesa;
        this.logger=logger;
    }
    public Mesa agregar(String numero, String ubicacion){
        if(numero==null||numero.isBlank())
            throw new IllegalArgumentException("Número no puede ser nulo/vacío");
        if(repositorioMesa.buscarPorNumero(numero).isPresent())
            throw new IllegalStateException("Ya existe una mesa con el número " + numero);
        Mesa mesa=new Mesa(numero, ubicacion);
        repositorioMesa.agregar(mesa);
        logger.info("Mesa creada: {}", mesa);
        return mesa;
    }
    public void actualizar(Long id, String numero, String ubicacion){
        Mesa mesa=obtenerMesaOLanzar(id);
        if(numero!=null&&!numero.isBlank()&&!numero.equals(mesa.getNumero())){
            if(repositorioMesa.buscarPorNumero(numero).isPresent())
                throw new IllegalStateException("Ya existe una mesa con el número " + numero);
            mesa.setNumero(numero);
        }
        if(ubicacion!=null&&!ubicacion.isBlank())
            mesa.setUbicacion(ubicacion);
        repositorioMesa.actualizar(mesa);
        logger.info("Mesa actualizada: {}", mesa);
    }
    public void eliminar(Long id){
        Mesa mesa=obtenerMesaOLanzar(id);
        if(!mesa.estaLibre())
            throw new IllegalStateException("No se puede eliminar una mesa ocupada");
        repositorioMesa.eliminar(mesa);
        logger.info("Mesa eliminada: {}", mesa);
    }
    public void ocupar(Long id){
        Mesa mesa=obtenerMesaOLanzar(id);
        mesa.ocupar();
        repositorioMesa.actualizar(mesa);
        logger.info("Mesa ocupada: {}", mesa);
    }
    public void liberar(Long id){
        Mesa mesa=obtenerMesaOLanzar(id);
        mesa.liberar();
        repositorioMesa.actualizar(mesa);
        logger.info("Mesa liberada: {}", mesa);
    }
    public List<Mesa> listarTodas(){
        return repositorioMesa.listarTodos();
    }
    public List<Mesa> listarLibres(){
        return repositorioMesa.listarLibres();
    }
    public List<Mesa> listarOcupadas(){
        return repositorioMesa.listarOcupadas();
    }
    public List<Mesa> buscar(String texto){
        if(texto==null||texto.isBlank())
            return repositorioMesa.listarTodos();
        return repositorioMesa.buscarPorNumeroContiene(texto);
    }
    private Mesa obtenerMesaOLanzar(Long id){
        if(id==null)
            throw new IllegalArgumentException("ID no puede ser nulo");
        return repositorioMesa.buscarPorId(id)
                .orElseThrow(()->new IllegalStateException("Mesa no encontrada"));
    }
}
