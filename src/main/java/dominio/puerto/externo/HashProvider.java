package dominio.puerto.externo;
/**
 * Puerto para el servicio de hash de contraseñas
 * Implementado en infraestructura (por ejemplo, con BCrypt)
 *
 * @author inici4rsesi0n
 */
public interface HashProvider {
    /**
     * Genera el hash de una contraseña
     * @param contrasena contraseña en texto plano
     * @return hash resultante
     */
    String generarHash(char[] contrasena);
    /**
     * Verifica si una contraseña coincide con un hash almacenado
     * @param hash hash previamente generado
     * @param contrasena contraseña a verificar
     * @return true si coincide, false en caso contrario
     */
    boolean verificarHash(String hash, char[] contrasena);
}
