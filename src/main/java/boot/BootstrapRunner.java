package boot;
import aplicacion.casosdeuso.BootstrapUseCase;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
/**
 *
 * @author inici4rsesi0n
 */
@Component
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
