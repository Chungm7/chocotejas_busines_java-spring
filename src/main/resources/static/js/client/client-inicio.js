// client-inicio.js - Script específico para la página de inicio
document.addEventListener('DOMContentLoaded', function() {

    // Agregar event listener a los botones de WhatsApp
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('whatsapp-order-btn') ||
            e.target.closest('.whatsapp-order-btn')) {
            const button = e.target.classList.contains('whatsapp-order-btn') ? e.target : e.target.closest('.whatsapp-order-btn');
            handleWhatsAppOrder(button);
        }
    });

    // Manejar pedido por WhatsApp (misma función que en galeria.js)
    function handleWhatsAppOrder(button) {
        const productId = button.dataset.productId;
        const productName = button.dataset.productName;
        const productPrice = button.dataset.productPrice;

        // Construir mensaje de WhatsApp
        const mensaje = construirMensajeWhatsApp(productName, productPrice);

        // Número de WhatsApp de la tienda (cambiar por el número real)
        const phoneNumber = '51987654321';
        const urlWhatsApp = `https://wa.me/${phoneNumber}?text=${encodeURIComponent(mensaje)}`;

        window.open(urlWhatsApp, '_blank');

        // Cerrar el modal después de enviar
        const modalElement = button.closest('.modal');
        const modal = bootstrap.Modal.getInstance(modalElement);
        if (modal) {
            modal.hide();
        }
    }

    // Construir mensaje para WhatsApp (misma función que en galeria.js)
    function construirMensajeWhatsApp(productName, productPrice) {
        let mensaje = `¡Hola! Estoy interesado en el siguiente producto:\n\n`;
        mensaje += `*${productName}*\n`;
        mensaje += `Precio: S/ ${parseFloat(productPrice).toFixed(2)}\n\n`;
        mensaje += `Por favor, necesito que me contacten para realizar mi pedido.`;
        mensaje += `\n\n(Mensaje generado desde la tienda online Chocotejas "El Sabor de Casa")`;

        return mensaje;
    }

    // Inicializar carrusel si existe
    const destacadosCarousel = document.getElementById('destacadosCarousel');
    if (destacadosCarousel) {
        // El carrusel ya se inicializa automáticamente con data-bs-ride="carousel"
        console.log('Carrusel de productos destacados inicializado');
    }
});