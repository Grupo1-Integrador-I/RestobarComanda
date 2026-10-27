package infraestructura.seguridad;
import java.nio.CharBuffer;
import dominio.puerto.externo.HashProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
public class BCryptHashProvider implements HashProvider{
    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    @Override
    public String generarHash(char[] contrasena){
        if(contrasena==null||contrasena.length==0)
            throw new IllegalArgumentException("Contraseña no puede ser nula/vacía");
        return encoder.encode(CharBuffer.wrap(contrasena));
    }
    @Override
    public boolean verificarHash(String hash, char[] contrasena){
        if(hash==null||hash.isBlank())
            throw new IllegalArgumentException("Hash no puede ser nulo/vacío");
        if(contrasena==null||contrasena.length==0)
            return false;
        return encoder.matches(CharBuffer.wrap(contrasena), hash);
    }
}
