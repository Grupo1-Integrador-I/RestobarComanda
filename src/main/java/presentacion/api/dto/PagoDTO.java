package presentacion.api.dto;
import dominio.modelo.Pago;
import dominio.modelo.TipoPago;
import java.math.BigDecimal;
import java.time.LocalDateTime;
/**
 *
 * @author inici4rsesi0n
 */
public record PagoDTO(
        Long id,
        Long pedidoId,
        BigDecimal monto,
        TipoPago tipo,
        LocalDateTime fecha
){
    public static PagoDTO desde(Pago pago){
        return new PagoDTO(pago.getId(), pago.getPedido().getId(),
                pago.getMonto().valor(), pago.getTipo(), pago.getFecha());
    }
}
