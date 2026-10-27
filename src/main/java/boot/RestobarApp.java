package boot;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
/**
 *
 * @author inici4rsesi0n
 */
@SpringBootApplication(scanBasePackages={"boot","aplicacion","infraestructura","presentacion"})
@EntityScan(basePackages="infraestructura.persistencia.entidad")
@EnableJpaRepositories(basePackages="infraestructura.persistencia.repositorioJPA")
public class RestobarApp{
    public static void main(String[] args){
        SpringApplication.run(RestobarApp.class, args);
    }
}
