/**
 * Script principal con lógica común para toda la aplicación.
 * Archivo: src/main/resources/static/js/gestion-main.js
 */

$(document).ready(function() {
    /**
     * Configura la interactividad del sidebar responsivo.
     */
    function setupSidebar() {
        const sidebar = $('#sidebar');
        const openSidebarBtn = $('#open-sidebar');
        const closeSidebarBtn = $('#close-sidebar');
        const sidebarOverlay = $('#sidebar-overlay');

        openSidebarBtn.on('click', function() {
            sidebar.addClass('active');
            sidebarOverlay.addClass('active');
        });

        function closeSidebar() {
            sidebar.removeClass('active');
            sidebarOverlay.removeClass('active');
        }

        closeSidebarBtn.on('click', closeSidebar);
        sidebarOverlay.on('click', closeSidebar);
    }

    // Inicializar la funcionalidad del sidebar en cada carga de página.
    setupSidebar();
});
// En gestion-main.js - agregar estas funciones

// Función para guardar la posición del scroll del sidebar
function guardarPosicionScrollSidebar() {
    const sidebarContainer = document.querySelector('.sidebar-menu-container');
    if (sidebarContainer) {
        const scrollPosition = sidebarContainer.scrollTop;
        sessionStorage.setItem('sidebarScrollPosition', scrollPosition);
    }
}

// Función para restaurar la posición del scroll del sidebar
function restaurarPosicionScrollSidebar() {
    const sidebarContainer = document.querySelector('.sidebar-menu-container');
    const savedPosition = sessionStorage.getItem('sidebarScrollPosition');

    if (sidebarContainer && savedPosition) {
        // Usamos setTimeout para asegurar que el DOM esté completamente renderizado
        setTimeout(() => {
            sidebarContainer.scrollTop = parseInt(savedPosition);
        }, 50);
    }
}

// Función para limpiar la posición guardada (opcional, para casos específicos)
function limpiarPosicionScrollSidebar() {
    sessionStorage.removeItem('sidebarScrollPosition');
}

// Event listeners para los enlaces del sidebar
document.addEventListener('DOMContentLoaded', function() {
    // Restaurar posición al cargar la página
    restaurarPosicionScrollSidebar();

    // Agregar event listeners a todos los enlaces del sidebar
    const sidebarLinks = document.querySelectorAll('.sidebar-menu-container .nav-link');
    sidebarLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // No aplicar a enlaces externos o con target _blank
            if (!this.target || this.target === '_self') {
                guardarPosicionScrollSidebar();
            }
        });
    });

    // También capturar el scroll manual para guardar la posición
    const sidebarContainer = document.querySelector('.sidebar-menu-container');
    if (sidebarContainer) {
        let scrollTimeout;
        sidebarContainer.addEventListener('scroll', function() {
            // Usamos debounce para no guardar en cada pixel de scroll
            clearTimeout(scrollTimeout);
            scrollTimeout = setTimeout(() => {
                guardarPosicionScrollSidebar();
            }, 150);
        });
    }
});