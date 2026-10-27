package dominio.puerto.externo;
/**
 *
 * @author inici4rsesi0n
 */
public interface LoggerPort {
    void info(String mensaje);
    void info(String formato, Object... argumentos);
    void warn(String mensaje);
    void warn(String formato, Object... argumentos);
    void warn(String mensaje, Throwable excepcion);
    void error(String mensaje);
    void error(String formato, Object... argumentos);
    void error(String mensaje, Throwable excepcion);
    void debug(String mensaje);
    void debug(String formato, Object... argumentos);
    void trace(String mensaje);
    void trace(String formato, Object... argumentos);
}
