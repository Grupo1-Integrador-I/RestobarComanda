package presentacion.api;
import aplicacion.casosdeuso.GestionUsuarios;
import dominio.modelo.Rol;
import dominio.modelo.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import presentacion.api.dto.PasswordChangeRequest;
import presentacion.api.dto.UsuarioCreateRequest;
import presentacion.api.dto.UsuarioDTO;
import presentacion.api.dto.UsuarioUpdateRequest;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController{
    private final GestionUsuarios gestionUsuarios;
    public AdminUsuarioController(GestionUsuarios gestionUsuarios){
        this.gestionUsuarios=gestionUsuarios;
    }
    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar(@RequestParam(required=false) Rol rol){
        List<Usuario> usuarios=rol!=null
                ?gestionUsuarios.listarPorRol(rol)
                :gestionUsuarios.listarTodos();
        return ResponseEntity.ok(usuarios.stream().map(UsuarioDTO::desde).toList());
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<UsuarioDTO>> buscar(@RequestParam(required=false) String q){
        return ResponseEntity.ok(gestionUsuarios.buscar(q).stream().map(UsuarioDTO::desde).toList());
    }
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> obtener(@PathVariable Long id){
        return ResponseEntity.ok(UsuarioDTO.desde(gestionUsuarios.obtener(id)));
    }
    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@Valid @RequestBody UsuarioCreateRequest peticion){
        Usuario usuario=gestionUsuarios.agregar(peticion.codigo(),
                peticion.password().toCharArray(), peticion.dni(),
                peticion.nombre(), peticion.apellido(), peticion.rol());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDTO.desde(usuario));
    }
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> actualizar(@PathVariable Long id,
                                                 @Valid @RequestBody UsuarioUpdateRequest peticion){
        gestionUsuarios.actualizar(id, peticion.dni(), peticion.nombre(), peticion.apellido());
        return ResponseEntity.ok(UsuarioDTO.desde(gestionUsuarios.obtener(id)));
    }
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> cambiarContrasena(@PathVariable Long id,
                                                  @Valid @RequestBody PasswordChangeRequest peticion){
        gestionUsuarios.cambiarContrasena(id, peticion.password().toCharArray());
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        gestionUsuarios.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
