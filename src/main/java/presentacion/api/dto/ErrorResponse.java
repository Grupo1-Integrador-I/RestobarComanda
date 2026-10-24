package presentacion.api.dto;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
/**
 *
 * @author inici4rsesi0n
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String mensaje,
        String path
){
    public static ErrorResponse de(HttpStatus status, String mensaje, String path){
        return new ErrorResponse(LocalDateTime.now(), status.value(),
                status.getReasonPhrase(), mensaje, path);
    }
}
