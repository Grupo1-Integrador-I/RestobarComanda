package presentacion.api.dto;
import dominio.modelo.EstadoMesa;
import dominio.modelo.Mesa;
/**
 *
 * @author inici4rsesi0n
 */
public record MesaDTO(
        Long id,
        String numero,
        String ubicacion,
        EstadoMesa estado
){
    public static MesaDTO desde(Mesa mesa){
        return new MesaDTO(mesa.getId(), mesa.getNumero(),
                mesa.getUbicacion(), mesa.getEstado());
    }
}
