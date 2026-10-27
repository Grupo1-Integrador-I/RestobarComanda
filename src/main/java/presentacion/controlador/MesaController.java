
package presentacion.controlador;

import dominio.modelo.Mesa;
import dominio.puerto.repositorio.RepositorioMesa;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mesas")
public class MesaController {

    private final RepositorioMesa repositorioMesa;

    public MesaController(RepositorioMesa repositorioMesa) {
        this.repositorioMesa = repositorioMesa;
    }

    // LISTAR TODAS LAS MESAS
    @GetMapping
    public List<Mesa> listarMesas() {
        return repositorioMesa.listarTodos();
    }

    // LISTAR MESAS LIBRES
    @GetMapping("/libres")
    public List<Mesa> listarMesasLibres() {
        return repositorioMesa.listarLibres();
    }

    // LISTAR MESAS OCUPADAS
    @GetMapping("/ocupadas")
    public List<Mesa> listarMesasOcupadas() {
        return repositorioMesa.listarOcupadas();
    }
}
