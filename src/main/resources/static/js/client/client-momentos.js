// client-momentos.js - Funcionalidad para la galería de momentos
document.addEventListener('DOMContentLoaded', function() {
    inicializarGaleriaMomentos();
});

function inicializarGaleriaMomentos() {
    const galeriaContainer = document.getElementById('galeriaMomentos');

    if (!galeriaContainer) return;

    // Opcional: Agregar funcionalidad de lightbox si se hace clic en una imagen
    const momentoItems = document.querySelectorAll('.momento-item');

    momentoItems.forEach(item => {
        item.addEventListener('click', function() {
            const imgSrc = this.querySelector('.momento-img').src;
            const imgAlt = this.querySelector('.momento-img').alt;

            // Crear lightbox simple
            mostrarLightbox(imgSrc, imgAlt);
        });
    });
}

function mostrarLightbox(src, alt) {
    // Remover lightbox existente si hay uno
    const lightboxExistente = document.getElementById('lightboxMomentos');
    if (lightboxExistente) {
        lightboxExistente.remove();
    }

    // Crear elemento lightbox
    const lightbox = document.createElement('div');
    lightbox.id = 'lightboxMomentos';
    lightbox.innerHTML = `
        <div class="lightbox-overlay">
            <div class="lightbox-content">
                <button class="lightbox-close">&times;</button>
                <img src="${src}" alt="${alt}">
                <div class="lightbox-caption">${alt}</div>
            </div>
        </div>
    `;

    // Agregar estilos inline para el lightbox
    lightbox.style.cssText = `
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background: rgba(0, 0, 0, 0.8);
        display: flex;
        align-items: center;
        justify-content: center;
        z-index: 9999;
        opacity: 0;
        transition: opacity 0.3s ease;
    `;

    const lightboxContent = lightbox.querySelector('.lightbox-content');
    lightboxContent.style.cssText = `
        position: relative;
        max-width: 90%;
        max-height: 90%;
        background: white;
        border-radius: 12px;
        overflow: hidden;
        transform: scale(0.8);
        transition: transform 0.3s ease;
    `;

    const lightboxImg = lightbox.querySelector('img');
    lightboxImg.style.cssText = `
        display: block;
        max-width: 100%;
        max-height: 80vh;
        object-fit: contain;
    `;

    const lightboxClose = lightbox.querySelector('.lightbox-close');
    lightboxClose.style.cssText = `
        position: absolute;
        top: 10px;
        right: 15px;
        background: none;
        border: none;
        font-size: 2rem;
        color: white;
        cursor: pointer;
        z-index: 10;
    `;

    const lightboxCaption = lightbox.querySelector('.lightbox-caption');
    lightboxCaption.style.cssText = `
        padding: 1rem;
        text-align: center;
        background: var(--color-dark-chocolate);
        color: var(--color-cream);
        font-family: var(--font-headings);
    `;

    // Agregar al DOM
    document.body.appendChild(lightbox);

    // Animar entrada
    setTimeout(() => {
        lightbox.style.opacity = '1';
        lightboxContent.style.transform = 'scale(1)';
    }, 10);

    // Funcionalidad de cierre
    lightboxClose.addEventListener('click', cerrarLightbox);
    lightbox.addEventListener('click', function(e) {
        if (e.target === lightbox) {
            cerrarLightbox();
        }
    });

    // Cerrar con ESC
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Escape') {
            cerrarLightbox();
        }
    });

    function cerrarLightbox() {
        lightbox.style.opacity = '0';
        lightboxContent.style.transform = 'scale(0.8)';
        setTimeout(() => {
            if (lightbox.parentNode) {
                lightbox.parentNode.removeChild(lightbox);
            }
        }, 300);
    }
}