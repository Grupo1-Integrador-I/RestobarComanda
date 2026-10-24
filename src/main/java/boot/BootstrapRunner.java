package boot;
import aplicacion.casosdeuso.BootstrapUseCase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
@ConditionalOnProperty(name = "app.bootstrap.enabled", havingValue = "true", matchIfMissing = true)
public class BootstrapRunner implements CommandLineRunner{
    private final BootstrapUseCase bootstrapUseCase;
    public BootstrapRunner(BootstrapUseCase bootstrapUseCase){
        this.bootstrapUseCase=bootstrapUseCase;
    }
    @Override
    public void run(String... args){
        bootstrapUseCase.ejecutar();
    }
}
