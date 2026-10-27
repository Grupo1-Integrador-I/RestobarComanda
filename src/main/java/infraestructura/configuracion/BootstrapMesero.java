
package infraestructura.configuracion;

import aplicacion.casosdeuso.GestionUsuarios;
import dominio.modelo.Mesero;
import dominio.puerto.repositorio.RepositorioUsuario;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class BootstrapMesero {

    @Bean
    public CommandLineRunner crearMeseroInicial(
            GestionUsuarios gestionUsuarios,
            RepositorioUsuario repositorioUsuario,
            @Value("${bootstrap.mesero.contrasena}")
            String contrasena) {

        return args -> {

            if (repositorioUsuario
                    .buscarPorCodigo("mesero01")
                    .isPresent()) {
                System.out.println(
                    "El mesero01 ya existe."
                );
                return;
            }

            Mesero mesero = new Mesero(
                    "mesero01",
                    "PENDIENTE",
                    "12345678",
                    "Juan",
                    "Perez"
            );

            gestionUsuarios.agregar(
                    mesero,
                    contrasena.toCharArray()
            );

            System.out.println(
                "Mesero inicial creado correctamente."
            );
        };
    }
}
