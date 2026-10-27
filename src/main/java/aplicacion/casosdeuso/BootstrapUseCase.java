package aplicacion.casosdeuso;
import dominio.modelo.Administrador;
import dominio.modelo.Rol;
import dominio.puerto.externo.HashProvider;
import dominio.puerto.externo.LoggerPort;
import dominio.puerto.repositorio.RepositorioUsuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Arrays;
/**
 *
 * @author inici4rsesi0n
 */
@Service
public class BootstrapUseCase{
    private final RepositorioUsuario repositorioUsuario;
    private final HashProvider hashProvider;
    private final LoggerPort logger;
    private final String adminCodigo;
    private final String adminContrasena;
    private final String adminDni;
    private final String adminNombre;
    private final String adminApellido;
    public BootstrapUseCase(RepositorioUsuario repositorioUsuario,
                            HashProvider hashProvider,
                            LoggerPort logger,
                            @Value("${bootstrap.admin.codigo}") String adminCodigo,
                            @Value("${bootstrap.admin.contrasena}") String adminContrasena,
                            @Value("${bootstrap.admin.dni}") String adminDni,
                            @Value("${bootstrap.admin.nombre}") String adminNombre,
                            @Value("${bootstrap.admin.apellido}") String adminApellido){
        this.repositorioUsuario=repositorioUsuario;
        this.hashProvider=hashProvider;
        this.logger=logger;
        this.adminCodigo=adminCodigo;
        this.adminContrasena=adminContrasena;
        this.adminDni=adminDni;
        this.adminNombre=adminNombre;
        this.adminApellido=adminApellido;
    }
    public void ejecutar(){
        boolean existeAdmin=repositorioUsuario.listarTodos().stream()
                .anyMatch(u->u.getRol()==Rol.ADMINISTRADOR);
        if(existeAdmin){
            logger.info("Bootstrap omitido: ya existe un administrador");
            return;
        }
        if(adminContrasena==null||adminContrasena.isBlank())
            throw new IllegalStateException("Debe definir bootstrap.admin.contrasena");
        char[] contrasena=adminContrasena.toCharArray();
        String hash;
        try{
            hash=hashProvider.generarHash(contrasena);
        }finally{
            Arrays.fill(contrasena, '\0');
        }
        Administrador admin=new Administrador(adminCodigo, hash, adminDni,
                                              adminNombre, adminApellido);
        repositorioUsuario.agregar(admin);
        logger.info("Bootstrap completado: administrador inicial creado");
    }
}
