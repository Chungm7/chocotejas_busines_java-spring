// Main JavaScript for ChocoTejas

// Global variables
let currentProducts = [];
let currentCategories = [];
let currentUsers = [];

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    initializeApp();
});

// Initialize application
function initializeApp() {
    // Set active navigation
    setActiveNavigation();
    
    // Initialize tooltips
    initializeTooltips();
    
    // Load sample data
    loadSampleData();
    
    // Initialize event listeners
    initializeEventListeners();
}

// Set active navigation based on current page
function setActiveNavigation() {
    const currentPage = window.location.pathname.split('/').pop();
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href === currentPage || (currentPage === '' && href === 'index.html')) {
            link.classList.add('active');
        }
    });
}

// Initialize Bootstrap tooltips
function initializeTooltips() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
}

// Load sample data for demonstration
function loadSampleData() {
    // Sample products data
    currentProducts = [
        {
            id: 1,
            name: "ChocoTeja Clásica",
            description: "Delicioso chocolate relleno con manjar blanco tradicional, cubierto con chocolate de leche premium.",
            price: 15.00,
            category: "Clásicas",
            image: "https://via.placeholder.com/300x250/8B4513/FFFFFF?text=ChocoTeja+Clasica",
            status: "active"
        },
        {
            id: 2,
            name: "ChocoTeja de Lúcuma",
            description: "Exquisito chocolate relleno con crema de lúcuma, fruta peruana de sabor único y delicioso.",
            price: 18.00,
            category: "Especiales",
            image: "https://via.placeholder.com/300x250/D2691E/FFFFFF?text=ChocoTeja+Lucuma",
            status: "active"
        },
        {
            id: 3,
            name: "ChocoTeja de Maracuyá",
            description: "Refrescante chocolate relleno con crema de maracuyá, perfecto balance entre dulce y ácido.",
            price: 18.00,
            category: "Especiales",
            image: "https://via.placeholder.com/300x250/FFD700/000000?text=ChocoTeja+Maracuya",
            status: "active"
        },
        {
            id: 4,
            name: "ChocoTeja de Café",
            description: "Intenso chocolate relleno con crema de café peruano, ideal para los amantes del café.",
            price: 20.00,
            category: "Premium",
            image: "https://via.placeholder.com/300x250/2C1810/FFFFFF?text=ChocoTeja+Cafe",
            status: "active"
        },
        {
            id: 5,
            name: "ChocoTeja de Coco",
            description: "Tropical chocolate relleno con crema de coco, cubierto con coco rallado natural.",
            price: 17.00,
            category: "Especiales",
            image: "https://via.placeholder.com/300x250/F5F5DC/000000?text=ChocoTeja+Coco",
            status: "active"
        },
        {
            id: 6,
            name: "ChocoTeja Dark",
            description: "Chocolate amargo 70% cacao relleno con ganache de chocolate negro, para paladares exigentes.",
            price: 22.00,
            category: "Premium",
            image: "https://via.placeholder.com/300x250/1C1C1C/FFFFFF?text=ChocoTeja+Dark",
            status: "active"
        }
    ];

    // Sample categories
    currentCategories = [
        { id: 1, name: "Clásicas", description: "Sabores tradicionales", status: "active" },
        { id: 2, name: "Especiales", description: "Sabores únicos peruanos", status: "active" },
        { id: 3, name: "Premium", description: "Línea premium gourmet", status: "active" }
    ];

    // Sample users
    currentUsers = [
        { id: 1, name: "María García", email: "maria@chocotejas.com", role: "Administrador", status: "active" },
        { id: 2, name: "Carlos López", email: "carlos@chocotejas.com", role: "Trabajador", status: "active" },
        { id: 3, name: "Ana Rodríguez", email: "ana@chocotejas.com", role: "Trabajador", status: "inactive" }
    ];
}

// Initialize event listeners
function initializeEventListeners() {
    // WhatsApp buttons
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('btn-whatsapp') || e.target.closest('.btn-whatsapp')) {
            handleWhatsAppOrder(e);
        }
    });

    // Form submissions
    document.addEventListener('submit', function(e) {
        if (e.target.classList.contains('admin-form')) {
            handleFormSubmission(e);
        }
    });

    // Delete confirmations
    document.addEventListener('click', function(e) {
        if (e.target.classList.contains('btn-delete') || e.target.closest('.btn-delete')) {
            handleDeleteConfirmation(e);
        }
    });

    // Image upload handling
    const imageInputs = document.querySelectorAll('input[type="file"]');
    imageInputs.forEach(input => {
        input.addEventListener('change', handleImageUpload);
    });
}

// Handle WhatsApp order
function handleWhatsAppOrder(e) {
    e.preventDefault();
    
    const productName = e.target.getAttribute('data-product') || 
                       e.target.closest('.btn-whatsapp').getAttribute('data-product');
    
    const message = `¡Hola! Me interesa el producto: ${productName}. ¿Podrían darme más información sobre disponibilidad y precios?`;
    const whatsappNumber = "51999888777"; // Replace with actual number
    const whatsappUrl = `https://wa.me/${whatsappNumber}?text=${encodeURIComponent(message)}`;
    
    window.open(whatsappUrl, '_blank');
    
    showNotification('Redirigiendo a WhatsApp...', 'info');
}

// Show notification toast
function showNotification(message, type = 'success') {
    const toast = document.getElementById('notificationToast');
    const toastMessage = document.getElementById('toastMessage');
    const toastHeader = toast.querySelector('.toast-header');
    
    // Update message
    toastMessage.textContent = message;
    
    // Update icon and color based on type
    const icon = toastHeader.querySelector('i');
    icon.className = `bi me-2`;
    
    switch(type) {
        case 'success':
            icon.classList.add('bi-check-circle-fill', 'text-success');
            break;
        case 'error':
            icon.classList.add('bi-exclamation-triangle-fill', 'text-danger');
            break;
        case 'warning':
            icon.classList.add('bi-exclamation-triangle-fill', 'text-warning');
            break;
        case 'info':
            icon.classList.add('bi-info-circle-fill', 'text-info');
            break;
    }
    
    // Show toast
    const bsToast = new bootstrap.Toast(toast);
    bsToast.show();
}

// Handle form submissions
function handleFormSubmission(e) {
    e.preventDefault();
    
    const form = e.target;
    const formData = new FormData(form);
    const formType = form.getAttribute('data-form-type');
    
    // Show loading state
    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="loading-spinner me-2"></span>Guardando...';
    submitBtn.disabled = true;
    
    // Simulate API call
    setTimeout(() => {
        // Reset button
        submitBtn.innerHTML = originalText;
        submitBtn.disabled = false;
        
        // Close modal if exists
        const modal = form.closest('.modal');
        if (modal) {
            const bsModal = bootstrap.Modal.getInstance(modal);
            if (bsModal) {
                bsModal.hide();
            }
        }
        
        // Show success notification
        showNotification(`${formType} guardado exitosamente`);
        
        // Refresh data if needed
        if (formType === 'Producto') {
            refreshProductsTable();
        } else if (formType === 'Usuario') {
            refreshUsersTable();
        } else if (formType === 'Categoría') {
            refreshCategoriesTable();
        }
        
    }, 1500);
}

// Handle delete confirmation
function handleDeleteConfirmation(e) {
    e.preventDefault();
    
    const itemType = e.target.getAttribute('data-item-type') || 
                    e.target.closest('.btn-delete').getAttribute('data-item-type');
    const itemId = e.target.getAttribute('data-item-id') || 
                  e.target.closest('.btn-delete').getAttribute('data-item-id');
    
    // Show confirmation modal
    showDeleteConfirmation(itemType, itemId);
}

// Show delete confirmation modal
function showDeleteConfirmation(itemType, itemId) {
    const modalHtml = `
        <div class="modal fade" id="deleteConfirmModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title">
                            <i class="bi bi-exclamation-triangle me-2"></i>
                            Confirmar Eliminación
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p>¿Está seguro que desea eliminar este ${itemType.toLowerCase()}?</p>
                        <p class="text-muted">Esta acción no se puede deshacer.</p>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-danger" onclick="confirmDelete('${itemType}', '${itemId}')">
                            <i class="bi bi-trash me-2"></i>Eliminar
                        </button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Remove existing modal if any
    const existingModal = document.getElementById('deleteConfirmModal');
    if (existingModal) {
        existingModal.remove();
    }
    
    // Add modal to body
    document.body.insertAdjacentHTML('beforeend', modalHtml);
    
    // Show modal
    const modal = new bootstrap.Modal(document.getElementById('deleteConfirmModal'));
    modal.show();
}

// Confirm delete action
function confirmDelete(itemType, itemId) {
    // Close modal
    const modal = bootstrap.Modal.getInstance(document.getElementById('deleteConfirmModal'));
    modal.hide();
    
    // Simulate delete API call
    setTimeout(() => {
        showNotification(`${itemType} eliminado exitosamente`);
        
        // Refresh appropriate table
        if (itemType === 'Producto') {
            refreshProductsTable();
        } else if (itemType === 'Usuario') {
            refreshUsersTable();
        } else if (itemType === 'Categoría') {
            refreshCategoriesTable();
        }
    }, 500);
}

// Handle image upload
function handleImageUpload(e) {
    const file = e.target.files[0];
    if (!file) return;
    
    // Validate file type
    if (!file.type.startsWith('image/')) {
        showNotification('Por favor seleccione un archivo de imagen válido', 'error');
        return;
    }
    
    // Validate file size (max 5MB)
    if (file.size > 5 * 1024 * 1024) {
        showNotification('El archivo es demasiado grande. Máximo 5MB', 'error');
        return;
    }
    
    // Show preview
    const reader = new FileReader();
    reader.onload = function(e) {
        const preview = document.getElementById('imagePreview');
        if (preview) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        }
    };
    reader.readAsDataURL(file);
    
    // Simulate upload to cloud
    simulateImageUpload(file);
}

// Simulate image upload to cloud
function simulateImageUpload(file) {
    const uploadProgress = document.getElementById('uploadProgress');
    if (uploadProgress) {
        uploadProgress.style.display = 'block';
        
        let progress = 0;
        const interval = setInterval(() => {
            progress += Math.random() * 30;
            if (progress >= 100) {
                progress = 100;
                clearInterval(interval);
                
                setTimeout(() => {
                    uploadProgress.style.display = 'none';
                    showNotification('Imagen subida exitosamente');
                }, 500);
            }
            
            const progressBar = uploadProgress.querySelector('.progress-bar');
            if (progressBar) {
                progressBar.style.width = progress + '%';
                progressBar.textContent = Math.round(progress) + '%';
            }
        }, 200);
    }
}

// Refresh tables (these would typically make API calls)
function refreshProductsTable() {
    // This would typically reload data from the server
    console.log('Refreshing products table...');
}

function refreshUsersTable() {
    // This would typically reload data from the server
    console.log('Refreshing users table...');
}

function refreshCategoriesTable() {
    // This would typically reload data from the server
    console.log('Refreshing categories table...');
}

// Utility functions
function formatPrice(price) {
    return `S/ ${price.toFixed(2)}`;
}

function formatDate(date) {
    return new Date(date).toLocaleDateString('es-PE');
}

// Export functions for global access
window.showNotification = showNotification;
window.confirmDelete = confirmDelete;
window.handleWhatsAppOrder = handleWhatsAppOrder;