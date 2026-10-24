package presentacion.api;
import aplicacion.casosdeuso.GestionMesas;
import dominio.modelo.EstadoMesa;
import dominio.modelo.Mesa;
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
import presentacion.api.dto.MesaDTO;
import presentacion.api.dto.MesaRequest;
import java.util.List;
/**
 *
 * @author inici4rsesi0n
 */
@RestController
@RequestMapping("/api/admin/mesas")
public class AdminMesaController{
    private final GestionMesas gestionMesas;
    public AdminMesaController(GestionMesas gestionMesas){
        this.gestionMesas=gestionMesas;
    }
    @GetMapping
    public ResponseEntity<List<MesaDTO>> listar(@RequestParam(required=false) EstadoMesa estado){
        List<Mesa> mesas;
        if(estado==EstadoMesa.LIBRE)
            mesas=gestionMesas.listarLibres();
        else if(estado==EstadoMesa.OCUPADO)
            mesas=gestionMesas.listarOcupadas();
        else
            mesas=gestionMesas.listarTodas();
        return ResponseEntity.ok(mesas.stream().map(MesaDTO::desde).toList());
    }
    @GetMapping("/buscar")
    public ResponseEntity<List<MesaDTO>> buscar(@RequestParam(required=false) String q){
        return ResponseEntity.ok(gestionMesas.buscar(q).stream().map(MesaDTO::desde).toList());
    }
    @GetMapping("/{id}")
    public ResponseEntity<MesaDTO> obtener(@PathVariable Long id){
        return ResponseEntity.ok(MesaDTO.desde(gestionMesas.obtener(id)));
    }
    @PostMapping
    public ResponseEntity<MesaDTO> crear(@Valid @RequestBody MesaRequest peticion){
        Mesa mesa=gestionMesas.agregar(peticion.numero(), peticion.ubicacion());
        return ResponseEntity.status(HttpStatus.CREATED).body(MesaDTO.desde(mesa));
    }
    @PutMapping("/{id}")
    public ResponseEntity<MesaDTO> actualizar(@PathVariable Long id,
                                              @Valid @RequestBody MesaRequest peticion){
        gestionMesas.actualizar(id, peticion.numero(), peticion.ubicacion());
        return ResponseEntity.ok(MesaDTO.desde(gestionMesas.obtener(id)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id){
        gestionMesas.eliminar(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/ocupar")
    public ResponseEntity<MesaDTO> ocupar(@PathVariable Long id){
        gestionMesas.ocupar(id);
        return ResponseEntity.ok(MesaDTO.desde(gestionMesas.obtener(id)));
    }
    @PatchMapping("/{id}/liberar")
    public ResponseEntity<MesaDTO> liberar(@PathVariable Long id){
        gestionMesas.liberar(id);
        return ResponseEntity.ok(MesaDTO.desde(gestionMesas.obtener(id)));
    }
}
