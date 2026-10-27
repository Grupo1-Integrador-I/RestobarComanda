
package infraestructura.logging;

import dominio.puerto.externo.LoggerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class Slf4jLoggerAdapter implements LoggerPort {

    private static final Logger log =
            LoggerFactory.getLogger(Slf4jLoggerAdapter.class);

    @Override
    public void info(String mensaje) {
        log.info(mensaje);
    }

    @Override
    public void info(String formato, Object... argumentos) {
        log.info(formato, argumentos);
    }

    @Override
    public void warn(String mensaje) {
        log.warn(mensaje);
    }

    @Override
    public void warn(String formato, Object... argumentos) {
        log.warn(formato, argumentos);
    }

    @Override
    public void warn(String mensaje, Throwable excepcion) {
        log.warn(mensaje, excepcion);
    }

    @Override
    public void error(String mensaje) {
        log.error(mensaje);
    }

    @Override
    public void error(String formato, Object... argumentos) {
        log.error(formato, argumentos);
    }

    @Override
    public void error(String mensaje, Throwable excepcion) {
        log.error(mensaje, excepcion);
    }

    @Override
    public void debug(String mensaje) {
        log.debug(mensaje);
    }

    @Override
    public void debug(String formato, Object... argumentos) {
        log.debug(formato, argumentos);
    }

    @Override
    public void trace(String mensaje) {
        log.trace(mensaje);
    }

    @Override
    public void trace(String formato, Object... argumentos) {
        log.trace(formato, argumentos);
    }
}
