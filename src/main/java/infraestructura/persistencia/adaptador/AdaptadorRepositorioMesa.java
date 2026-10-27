package infraestructura.persistencia.adaptador;
import dominio.modelo.EstadoMesa;
import dominio.modelo.Mesa;
import dominio.puerto.repositorio.RepositorioMesa;
import infraestructura.persistencia.mapper.MesaMapper;
import infraestructura.persistencia.repositorioJPA.RepositorioMesaJPA;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioMesa implements RepositorioMesa{
    private final RepositorioMesaJPA jpa;
    public AdaptadorRepositorioMesa(RepositorioMesaJPA jpa){
        this.jpa=jpa;
    }
    @Override
    public void agregar(Mesa mesa){
        jpa.save(MesaMapper.aEntidad(mesa));
    }
    @Override
    public void actualizar(Mesa mesa){
        jpa.save(MesaMapper.aEntidad(mesa));
    }
    @Override
    public Optional<Mesa> buscarPorId(Long id){
        return jpa.findById(id).map(MesaMapper::aDominio);
    }
    @Override
    public List<Mesa> listarTodos(){
        return jpa.findAll().stream().map(MesaMapper::aDominio).toList();
    }
    @Override
    public void eliminar(Mesa mesa){
        jpa.deleteById(mesa.getId());
    }
    @Override
    public Optional<Mesa> buscarPorNumero(String numero){
        return jpa.findByNumero(numero).map(MesaMapper::aDominio);
    }
    @Override
    public List<Mesa> listarLibres(){
        return jpa.findByEstado(EstadoMesa.LIBRE).stream()
                .map(MesaMapper::aDominio).toList();
    }
    @Override
    public List<Mesa> listarOcupadas(){
        return jpa.findByEstado(EstadoMesa.OCUPADO).stream()
                .map(MesaMapper::aDominio).toList();
    }
    @Override
    public List<Mesa> buscarPorNumeroContiene(String texto){
        return jpa.findByNumeroContainingIgnoreCase(texto).stream()
                .map(MesaMapper::aDominio).toList();
    }
}
