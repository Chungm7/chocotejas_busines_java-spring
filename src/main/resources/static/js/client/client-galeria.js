// client-galeria.js
document.addEventListener('DOMContentLoaded', function() {
    // Elementos del DOM
    const searchInput = document.getElementById('search-input');
    const categoryFilters = document.getElementById('category-filters');
    const priceRange = document.getElementById('price-range');
    const priceValue = document.getElementById('price-value');
    const productGalleryContainer = document.getElementById('product-gallery-container');
    const cartItemsContainer = document.getElementById('cart-items-container');
    const cartTotal = document.getElementById('cart-total');
    const orderForm = document.getElementById('order-form');

    // Variables globales
    let allProducts = [];
    let cart = [];
    let selectedCategories = new Set();
    let maxPrice = 10;

    // Inicializar la galería
    initializeGallery();

    // Event Listeners
    searchInput.addEventListener('input', filterProducts);
    priceRange.addEventListener('input', updatePriceFilter);
    orderForm.addEventListener('submit', handleOrderSubmit);

    // Inicializar la galería
    function initializeGallery() {
        // Los productos ya están en el HTML mediante Thymeleaf
        // Convertimos los productos estáticos en datos dinámicos
        extractProductsFromHTML();
        initializeCategoryFilters();
        updatePriceFilter();
    }

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
    }

    // Inicializar filtros de categoría
    function initializeCategoryFilters() {
        // Reemplazar los checkboxes estáticos por dinámicos
        categoryFilters.innerHTML = '';

        // Obtener categorías únicas de los productos
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

        // Agregar event listeners a los checkboxes de categoría
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

    // Manejar envío del pedido
    function handleOrderSubmit(e) {
        e.preventDefault();

        if (cart.length === 0) {
            alert('Por favor agrega productos al carrito antes de enviar el pedido.');
            return;
        }

        const nombre = document.getElementById('nombre').value;
        const direccion = document.getElementById('direccion').value;
        const referencia = document.getElementById('referencia').value;
        const telefono = document.getElementById('telefono').value;

        if (!nombre || !direccion || !telefono) {
            alert('Por favor completa todos los campos obligatorios.');
            return;
        }

        // Construir mensaje de WhatsApp
        const mensaje = construirMensajeWhatsApp(nombre, direccion, referencia, telefono);
        const urlWhatsApp = `https://wa.me/51${telefono}?text=${encodeURIComponent(mensaje)}`;

        window.open(urlWhatsApp, '_blank');
    }

    // Construir mensaje para WhatsApp
    function construirMensajeWhatsApp(nombre, direccion, referencia, telefono) {
        let mensaje = `*PEDIDO - CHOCOTEJAS "EL SABOR DE CASA"*\n\n`;
        mensaje += `*Cliente:* ${nombre}\n`;
        mensaje += `*Dirección:* ${direccion}\n`;
        mensaje += `*Referencia:* ${referencia || 'No especificada'}\n`;
        mensaje += `*Teléfono:* ${telefono}\n\n`;
        mensaje += `*DETALLE DEL PEDIDO:*\n`;

        cart.forEach(item => {
            mensaje += `- ${item.name} x${item.quantity}: S/ ${(item.price * item.quantity).toFixed(2)}\n`;
        });

        mensaje += `\n*TOTAL: S/ ${calculateTotal().toFixed(2)}*`;
        mensaje += `\n\n¡Gracias por su pedido!`;

        return mensaje;
    }

    // Funciones del carrito (simplificadas para este ejemplo)
    function addToCart(productId, productName, productPrice) {
        const existingItem = cart.find(item => item.id === productId);

        if (existingItem) {
            existingItem.quantity += 1;
        } else {
            cart.push({
                id: productId,
                name: productName,
                price: productPrice,
                quantity: 1
            });
        }

        updateCartDisplay();
    }

    function removeFromCart(productId) {
        cart = cart.filter(item => item.id !== productId);
        updateCartDisplay();
    }

    function updateCartDisplay() {
        cartItemsContainer.innerHTML = '';

        if (cart.length === 0) {
            cartItemsContainer.innerHTML = '<p id="cart-empty-msg">Tu carrito está vacío.</p>';
            cartTotal.textContent = 'S/ 0.00';
            return;
        }

        cart.forEach(item => {
            const cartItem = document.createElement('div');
            cartItem.className = 'cart-item mb-2 p-2 border rounded';
            cartItem.innerHTML = `
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong>${item.name}</strong>
                        <br>
                        <small>S/ ${item.price.toFixed(2)} x ${item.quantity}</small>
                    </div>
                    <div>
                        <span class="me-2">S/ ${(item.price * item.quantity).toFixed(2)}</span>
                        <button class="btn btn-sm btn-danger remove-from-cart" data-product-id="${item.id}">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
            `;
            cartItemsContainer.appendChild(cartItem);
        });

        // Agregar event listeners a los botones de eliminar
        document.querySelectorAll('.remove-from-cart').forEach(button => {
            button.addEventListener('click', function() {
                const productId = this.dataset.productId;
                removeFromCart(productId);
            });
        });

        cartTotal.textContent = `S/ ${calculateTotal().toFixed(2)}`;
    }

    function calculateTotal() {
        return cart.reduce((total, item) => total + (item.price * item.quantity), 0);
    }

    // Hacer funciones globales para que los modales puedan acceder a ellas
    window.addToCart = addToCart;
});