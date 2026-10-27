package infraestructura.persistencia.adaptador;
import dominio.modelo.Usuario;
import dominio.puerto.repositorio.RepositorioUsuario;
import infraestructura.persistencia.mapper.UsuarioMapper;
import infraestructura.persistencia.repositorioJPA.RepositorioUsuarioJPA;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
/**
 *
 * @author inici4rsesi0n
 */
@Repository
public class AdaptadorRepositorioUsuario implements RepositorioUsuario{
    private final RepositorioUsuarioJPA jpa;
    public AdaptadorRepositorioUsuario(RepositorioUsuarioJPA jpa){
        this.jpa=jpa;
    }
    @Override
    public void agregar(Usuario usuario){
        jpa.save(UsuarioMapper.aEntidad(usuario));
    }
    @Override
    public void actualizar(Usuario usuario){
        jpa.save(UsuarioMapper.aEntidad(usuario));
    }
    @Override
    public Optional<Usuario> buscarPorId(Long id){
        return jpa.findById(id).map(UsuarioMapper::aDominio);
    }
    @Override
    public List<Usuario> listarTodos(){
        return jpa.findAll().stream().map(UsuarioMapper::aDominio).toList();
    }
    @Override
    public void eliminar(Usuario usuario){
        jpa.deleteById(usuario.getId());
    }
    @Override
    public Optional<Usuario> buscarPorCodigo(String codigo){
        return jpa.findByCodigo(codigo).map(UsuarioMapper::aDominio);
    }
    @Override
    public Optional<Usuario> buscarPorDni(String dni){
        return jpa.findByDni(dni).map(UsuarioMapper::aDominio);
    }
    @Override
    public List<Usuario> buscarPorNombreContiene(String texto){
        return jpa.findByNombreContainingIgnoreCase(texto).stream()
                .map(UsuarioMapper::aDominio).toList();
    }
    @Override
    public List<Usuario> buscarPorApellidoContiene(String texto){
        return jpa.findByApellidoContainingIgnoreCase(texto).stream()
                .map(UsuarioMapper::aDominio).toList();
    }
    @Override
    public List<Usuario> buscarPorNombreOApellidoContiene(String texto){
        return jpa.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(texto, texto)
                .stream().map(UsuarioMapper::aDominio).toList();
    }
}
