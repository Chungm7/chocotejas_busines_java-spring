// client-logo.js - Funcionalidad para el logo dinámico
document.addEventListener('DOMContentLoaded', function() {
    inicializarLogoDinamico();
});

function inicializarLogoDinamico() {
    const navbarBrand = document.querySelector('.navbar-brand');

    if (!navbarBrand) return;

    // Agregar efecto hover al logo
    navbarBrand.addEventListener('mouseenter', function() {
        this.style.transform = 'scale(1.05)';
    });

    navbarBrand.addEventListener('mouseleave', function() {
        this.style.transform = 'scale(1)';
    });

    // Ajustar el logo en scroll
    window.addEventListener('scroll', function() {
        const navbar = document.querySelector('.navbar');
        const logoImg = document.querySelector('.navbar-brand-logo img');
        const logoText = document.querySelector('.navbar-brand-text');

        if (window.scrollY > 50) {
            navbar.style.padding = '0.5rem 0';
            if (logoImg) {
                logoImg.style.maxHeight = '40px';
            }
            if (logoText) {
                logoText.style.fontSize = '1.5rem';
            }
        } else {
            navbar.style.padding = '1rem 0';
            if (logoImg) {
                logoImg.style.maxHeight = '50px';
            }
            if (logoText) {
                logoText.style.fontSize = '1.8rem';
            }
        }
    });
}

// Función para precargar imagen del logo (opcional)
function precargarLogo(url) {
    return new Promise((resolve, reject) => {
        const img = new Image();
        img.onload = () => resolve(img);
        img.onerror = reject;
        img.src = url;
    });
}