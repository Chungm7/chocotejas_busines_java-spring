// client-galeria.js - Script completo para galería y funcionalidad WhatsApp
document.addEventListener('DOMContentLoaded', function() {
    console.log('client-galeria.js cargado - Inicializando...');

    // Solo inicializar funcionalidades de galería si estamos en la página de galería
    const searchInput = document.getElementById('search-input');
    const categoryFilters = document.getElementById('category-filters');
    const priceRange = document.getElementById('price-range');
    const productGalleryContainer = document.getElementById('product-gallery-container');

    // Verificar si estamos en la página de galería
    const isGaleriaPage = searchInput && categoryFilters && priceRange && productGalleryContainer;

    if (isGaleriaPage) {
        console.log('Inicializando funcionalidades de galería...');
        initializeGallery();
    } else {
        console.log('No es página de galería, solo inicializando WhatsApp...');
    }

    // Inicializar funcionalidad de WhatsApp en TODAS las páginas
    initializeWhatsApp();
});

function initializeGallery() {
    // Elementos del DOM
    const searchInput = document.getElementById('search-input');
    const categoryFilters = document.getElementById('category-filters');
    const priceRange = document.getElementById('price-range');
    const priceValue = document.getElementById('price-value');
    const productGalleryContainer = document.getElementById('product-gallery-container');

    // Variables globales
    let allProducts = [];
    let selectedCategories = new Set();
    let maxPrice = 10;

    // Inicializar la galería
    extractProductsFromHTML();
    initializeCategoryFilters();
    updatePriceFilter();

    // Event Listeners específicos de galería
    searchInput.addEventListener('input', filterProducts);
    priceRange.addEventListener('input', updatePriceFilter);

    // Extraer productos del HTML generado por Thymeleaf
    function extractProductsFromHTML() {
        const productCards = document.querySelectorAll('.product-card-item');
        allProducts = Array.from(productCards).map(card => {
            return {
                id: card.dataset.id,
                name: card.querySelector('.card-title').textContent,
                description: card.querySelector('.card-text').textContent,
                price: parseFloat(card.querySelector('.price').textContent.replace('S/ ', '')),
                category: card.dataset.category,
                categoryId: card.dataset.categoryId,
                image: card.querySelector('img').src,
                stock: parseInt(card.dataset.stock)
            };
        });

        // Establecer el precio máximo para el rango
        if (allProducts.length > 0) {
            const maxProductPrice = Math.max(...allProducts.map(p => p.price));
            maxPrice = Math.ceil(maxProductPrice);
            priceRange.max = maxPrice;
            priceRange.value = maxPrice;
            priceValue.textContent = `S/ ${maxPrice.toFixed(2)}`;
        }
    }

    // Inicializar filtros de categoría
    function initializeCategoryFilters() {
        categoryFilters.innerHTML = '';

        const categories = [...new Set(allProducts.map(p => p.category))];

        categories.forEach(category => {
            const categoryId = allProducts.find(p => p.category === category).categoryId;
            const checkbox = document.createElement('div');
            checkbox.className = 'form-check';
            checkbox.innerHTML = `
                <input class="form-check-input category-checkbox" type="checkbox"
                       value="${category}" data-category-id="${categoryId}" id="category-${categoryId}">
                <label class="form-check-label" for="category-${categoryId}">
                    ${category}
                </label>
            `;
            categoryFilters.appendChild(checkbox);
        });

        document.querySelectorAll('.category-checkbox').forEach(checkbox => {
            checkbox.addEventListener('change', handleCategoryFilter);
        });
    }

    // Manejar filtro de categorías
    function handleCategoryFilter(e) {
        const category = e.target.value;
        const categoryId = e.target.dataset.categoryId;

        if (e.target.checked) {
            selectedCategories.add(categoryId);
        } else {
            selectedCategories.delete(categoryId);
        }

        filterProducts();
    }

    // Actualizar filtro de precio
    function updatePriceFilter() {
        maxPrice = parseFloat(priceRange.value);
        priceValue.textContent = `S/ ${maxPrice.toFixed(2)}`;
        filterProducts();
    }

    // Filtrar productos
    function filterProducts() {
        const searchTerm = searchInput.value.toLowerCase();

        document.querySelectorAll('.product-card-item').forEach(card => {
            const productName = card.querySelector('.card-title').textContent.toLowerCase();
            const productPrice = parseFloat(card.querySelector('.price').textContent.replace('S/ ', ''));
            const productCategoryId = card.dataset.categoryId;

            const matchesSearch = productName.includes(searchTerm);
            const matchesCategory = selectedCategories.size === 0 || selectedCategories.has(productCategoryId);
            const matchesPrice = productPrice <= maxPrice;

            if (matchesSearch && matchesCategory && matchesPrice) {
                card.style.display = 'block';
            } else {
                card.style.display = 'none';
            }
        });
    }
}

// Inicializar funcionalidad WhatsApp (funciona en todas las páginas)
function initializeWhatsApp() {
    console.log('Inicializando funcionalidad WhatsApp...');

    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('whatsapp-order-btn') ||
            e.target.closest('.whatsapp-order-btn')) {
            const button = e.target.classList.contains('whatsapp-order-btn') ?
                e.target : e.target.closest('.whatsapp-order-btn');
            handleWhatsAppOrder(button);
        }
    });
}

// Manejar pedido por WhatsApp
function handleWhatsAppOrder(button) {
    console.log('Manejando pedido por WhatsApp...');

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
    if (modalElement) {
        const modal = bootstrap.Modal.getInstance(modalElement);
        if (modal) {
            modal.hide();
        }
    }
}

// Construir mensaje para WhatsApp
function construirMensajeWhatsApp(productName, productPrice) {
    let mensaje = `¡Hola! Estoy interesado en el siguiente producto:\n\n`;
    mensaje += `*${productName}*\n`;
    mensaje += `Precio: S/ ${parseFloat(productPrice).toFixed(2)}\n\n`;
    mensaje += `Por favor, necesito que me contacten para realizar mi pedido.`;
    mensaje += `\n\n(Mensaje generado desde la tienda online Chocotejas "El Sabor de Casa")`;

    return mensaje;
}

// Funciones auxiliares para debugging (opcional)
function debugProducts() {
    console.log('Productos cargados:', allProducts);
}

function debugFilters() {
    console.log('Categorías seleccionadas:', Array.from(selectedCategories));
    console.log('Precio máximo:', maxPrice);
}