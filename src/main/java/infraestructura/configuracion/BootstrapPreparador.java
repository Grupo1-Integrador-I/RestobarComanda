
package infraestructura.configuracion;

import aplicacion.casosdeuso.GestionUsuarios;
import dominio.modelo.Preparador;
import dominio.puerto.repositorio.RepositorioUsuario;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class BootstrapPreparador {

    @Bean
    public CommandLineRunner crearPreparadorInicial(
            GestionUsuarios gestionUsuarios,
            RepositorioUsuario repositorioUsuario,
            @Value("${bootstrap.preparador.contrasena}")
            String contrasena) {

        return args -> {

            if (repositorioUsuario
                    .buscarPorCodigo("preparador01")
                    .isPresent()) {

                System.out.println(
                    "El preparador01 ya existe."
                );
                return;
            }

            Preparador preparador = new Preparador(
                    "preparador01",
                    "PENDIENTE",
                    "87654321",
                    "Carlos",
                    "Ramirez"
            );

            gestionUsuarios.agregar(
                    preparador,
                    contrasena.toCharArray()
            );

            System.out.println(
                "Preparador inicial creado correctamente."
            );
        };
    }
}
