// Define el paquete al que pertenece la clase.
package com.example.sistema_venta_chocotejas.controller;

// Importaciones de clases necesarias de otros paquetes.
import com.example.sistema_venta_chocotejas.service.CategoriaService;
import com.example.sistema_venta_chocotejas.service.Impl.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// @Controller: Marca esta clase como un controlador de Spring MVC, encargado de manejar peticiones web.
@Controller
public class DashboardController {

    // Declara una dependencia final al servicio de usuario. 'final' asegura que se
    // inicialice en el constructor.
    private final UsuarioService usuarioService;
    private  final CategoriaService categoriaService;

    // Constructor para la inyección de dependencias. Spring automáticamente
    // proporcionará una instancia de UsuarioService.
    public DashboardController(UsuarioService usuarioService, CategoriaService categoriaService) {
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
    }

    // @GetMapping("/"): Asocia este método a las peticiones HTTP GET para la URL
    // raíz ("/").
    // Es la página principal que se muestra después de iniciar sesión.
    @GetMapping("/")
    public String mostrarDashboard(Model model) {
        // 1. Llama al método contarUsuarios() del servicio para obtener el número total
        // de usuarios activos e inactivos (excluyendo los eliminados).
        long totalUsuarios = usuarioService.contarUsuarios();
        long totalUsuariosActivos = usuarioService.contarUsuariosActivos();
        long totalUsuariosInactivos = usuarioService.contarUsuariosInactivos();

        // total de categorias (excluyendo los eliminados)
        long totalCategorias = categoriaService.contarCategorias();
        long totalCategoriasActivas = categoriaService.contarCategoriasActivas();
        long totalCategoriasInactivas = categoriaService.contarCategoriasInactivas();


        // 2. 'model' es un objeto que permite pasar datos desde el controlador a la
        // vista (HTML).
        // Aquí, añadimos el conteo de usuarios al modelo con el nombre "totalUsuarios".
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("totalUsuariosActivos", totalUsuariosActivos);
        model.addAttribute("totalUsuariosInactivos", totalUsuariosInactivos);
        //categorias
        model.addAttribute("totalCategorias", totalCategorias);
        model.addAttribute("totalCategoriasActivas", totalCategoriasActivas);
        model.addAttribute("totalCategoriasInactivas", totalCategoriasInactivas);

        // 3. Devuelve el nombre de la vista (el archivo HTML) que se debe renderizar.
        // Spring Boot buscará un archivo llamado "indexclient.html" en la carpeta
        // 'src/main/resources/templates'.
        return "gestion-dashboard";
    }
}