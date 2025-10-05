// client-destacados.js - Funcionalidad para la rueda de productos destacados
document.addEventListener('DOMContentLoaded', function() {
    inicializarRuedaDestacados();
});

function inicializarRuedaDestacados() {
    const track = document.getElementById('destacadosTrack');
    const items = document.querySelectorAll('.producto-destacado-item');
    const container = document.getElementById('destacadosContainer');
    const indicatorsContainer = document.getElementById('destacadosIndicators');

    if (!track || items.length === 0) return;

    const itemsPerView = 4;
    const totalItems = items.length;
    const totalSlides = Math.ceil(totalItems / itemsPerView);
    let currentSlide = 0;

    // Crear indicadores si hay más de 4 productos
    if (totalItems > itemsPerView && indicatorsContainer) {
        crearIndicadores(indicatorsContainer, totalSlides);
    }

    // Actualizar la vista
    function actualizarVista() {
        const containerWidth = container.offsetWidth;
        const itemWidth = containerWidth / itemsPerView;
        const translateX = -currentSlide * (itemWidth * itemsPerView + 20 * itemsPerView); // 20px de gap

        track.style.transform = `translateX(${translateX}px)`;

        // Actualizar indicadores
        document.querySelectorAll('.carousel-indicator').forEach((indicator, index) => {
            indicator.classList.toggle('active', index === currentSlide);
        });

        // Actualizar estado de botones
        actualizarEstadoBotones();
    }

    function crearIndicadores(container, total) {
        container.innerHTML = '';
        for (let i = 0; i < total; i++) {
            const indicator = document.createElement('button');
            indicator.className = `carousel-indicator ${i === 0 ? 'active' : ''}`;
            indicator.setAttribute('onclick', `irASlide(${i})`);
            indicator.setAttribute('aria-label', `Ir a slide ${i + 1}`);
            container.appendChild(indicator);
        }
    }

    function actualizarEstadoBotones() {
        // Los botones se manejan con la función moverDestacados
    }

    // Inicializar
    actualizarVista();

    // Hacer accesible desde el global scope para los botones HTML
    window.moverDestacados = function(direction) {
        const newSlide = currentSlide + direction;

        if (newSlide >= 0 && newSlide < totalSlides) {
            currentSlide = newSlide;
            actualizarVista();
        }
    };

    window.irASlide = function(slideIndex) {
        if (slideIndex >= 0 && slideIndex < totalSlides) {
            currentSlide = slideIndex;
            actualizarVista();
        }
    };

    // Manejar redimensionamiento de ventana
    window.addEventListener('resize', actualizarVista);
}