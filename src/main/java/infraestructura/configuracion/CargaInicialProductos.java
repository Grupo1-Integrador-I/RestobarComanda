
package infraestructura.configuracion;

import aplicacion.casosdeuso.GestionProductos;
import dominio.modelo.CategoriaProducto;
import dominio.modelo.Monto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CargaInicialProductos {

    @Bean
    public CommandLineRunner cargarProductos(
            GestionProductos gestionProductos) {

        return args -> {

            if (!gestionProductos.listarTodos().isEmpty()) {
                System.out.println(
                    "Ya existen productos registrados."
                );
                return;
            }

            // PRODUCTOS DE COCINA
            gestionProductos.agregar(
                "Hamburguesa clasica",
                Monto.de("18.00"),
                CategoriaProducto.COCINA
            );

            gestionProductos.agregar(
                "Hamburguesa Royal",
                Monto.de("22.00"),
                CategoriaProducto.COCINA
            );

            gestionProductos.agregar(
                "Hamburguesa a lo pobre",
                Monto.de("25.00"),
                CategoriaProducto.COCINA
            );

            gestionProductos.agregar(
                "Salchipapa",
                Monto.de("15.00"),
                CategoriaProducto.COCINA
            );

            gestionProductos.agregar(
                "Pollo broaster",
                Monto.de("22.00"),
                CategoriaProducto.COCINA
            );

            // PRODUCTOS DE BARRA
            gestionProductos.agregar(
                "Gaseosa personal",
                Monto.de("5.00"),
                CategoriaProducto.BARRA
            );

            gestionProductos.agregar(
                "Limonada",
                Monto.de("8.00"),
                CategoriaProducto.BARRA
            );

            gestionProductos.agregar(
                "Chicha morada",
                Monto.de("8.00"),
                CategoriaProducto.BARRA
            );

            gestionProductos.agregar(
                "Maracumango",
                Monto.de("12.00"),
                CategoriaProducto.BARRA
            );

            gestionProductos.agregar(
                "Maracuya",
                Monto.de("8.00"),
                CategoriaProducto.BARRA
            );

            gestionProductos.agregar(
                "Cerveza personal",
                Monto.de("10.00"),
                CategoriaProducto.BARRA
            );

            System.out.println(
                "Catalogo inicial de 11 productos registrado."
            );
        };
    }
}
