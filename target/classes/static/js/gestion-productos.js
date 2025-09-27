$(document).ready(function () {
    const galeriaProductos = $('#galeriaProductos');
    const productoModal = new bootstrap.Modal(document.getElementById('productoModal'));
    const formProducto = $('#formProducto');
    const modalLabel = $('#productoModalLabel');
    const imagenPreview = $('#imagenPreview');
    let editMode = false;
    let editId = null;

    // --- CARGA INICIAL ---
    cargarCategorias();
    cargarProductos();

    // --- FUNCIONES PRINCIPALES ---

    /**
     * Carga las categorías desde la API y las popula en el select del modal.
     */
    function cargarCategorias() {
        $.get('/categorias/api/listar', function (response) {
            if (response.success) {
                const selectCategoria = $('#categoria');
                selectCategoria.empty();
                selectCategoria.append('<option value="" selected disabled>Seleccione una categoría</option>');
                response.data.forEach(cat => {
                    selectCategoria.append(`<option value="${cat.id}">${cat.nombre}</option>`);
                });
            } else {
                mostrarNotificacion('Error al cargar categorías', 'error');
            }
        });
    }

    /**
     * Carga los productos desde la API y los renderiza en la galería.
     */
    function cargarProductos() {
        $.get('/productos/api/listar', function (response) {
            if (response.success) {
                renderizarGaleria(response.data);
            } else {
                mostrarNotificacion('Error al cargar productos', 'error');
            }
        }).fail(function() {
            mostrarNotificacion('Error de conexión al cargar productos', 'error');
        });
    }

    /**
     * Renderiza las tarjetas de producto en el contenedor de la galería.
     * @param {Array} productos - La lista de productos a mostrar.
     */
    function renderizarGaleria(productos) {
        galeriaProductos.empty();
        if (productos.length === 0) {
            galeriaProductos.html('<p class="col-12 text-center">No se encontraron productos.</p>');
            return;
        }
        productos.forEach(producto => {
            const cardHtml = `
                <div class="col-xl-3 col-lg-4 col-md-6 product-container" data-nombre="${producto.nombre.toLowerCase()}">
                    <div class="card product-card h-100 ${producto.estado === 0 ? 'disabled' : ''}">
                        <div class="card-img-container">
                            <img src="/imagenes/${producto.rutaImagen || 'default.png'}" class="card-img-top" alt="${producto.nombre}">
                        </div>
                        <div class="card-body">
                            <h5 class="card-title">${producto.nombre}</h5>
                            <p class="card-text">${producto.descripcion}</p>
                            <p class="price">S/ ${producto.precio.toFixed(2)}</p>
                        </div>
                        <div class="card-footer d-flex justify-content-around align-items-center">
                             <button class="btn btn-sm btn-toggle-status ${producto.estado === 1 ? 'btn-outline-warning' : 'btn-outline-success'}" data-id="${producto.id}">
                                ${producto.estado === 1 ? 'Desactivar' : 'Activar'}
                            </button>
                            <button class="btn btn-icon btn-edit" data-id="${producto.id}"><i class="fas fa-edit"></i></button>
                            <button class="btn btn-icon btn-delete" data-id="${producto.id}"><i class="fas fa-trash"></i></button>
                        </div>
                         <span class="status-badge ${producto.estado === 1 ? 'bg-success' : 'bg-danger'}">
                            ${producto.estado === 1 ? 'Activo' : 'Inactivo'}
                        </span>
                    </div>
                </div>
            `;
            galeriaProductos.append(cardHtml);
        });
    }

    // --- MANEJO DEL MODAL Y FORMULARIO ---

    /**
     * Limpia el formulario del modal y resetea el modo de edición.
     */
    function resetForm() {
        formProducto[0].reset();
        modalLabel.text('Nuevo Producto');
        imagenPreview.hide();
        editMode = false;
        editId = null;
        // Limpiar validación de Bootstrap
        formProducto.removeClass('was-validated');
    }

    // Evento para abrir el modal en modo "Nuevo Producto"
    $('#btnNuevoProducto').on('click', function () {
        resetForm();
        productoModal.show();
    });

    // Evento para abrir el modal en modo "Editar Producto"
    galeriaProductos.on('click', '.btn-edit', function () {
        resetForm();
        editMode = true;
        editId = $(this).data('id');
        modalLabel.text('Editar Producto');

        $.get(`/productos/api/${editId}`, function (response) {
            if (response.success) {
                const producto = response.data;
                $('#idProducto').val(producto.id);
                $('#nombre').val(producto.nombre);
                $('#descripcion').val(producto.descripcion);
                $('#precio').val(producto.precio);
                $('#stock').val(producto.stock);
                // Seleccionar la categoría correcta
                $(`#categoria option:contains(${producto.categoria})`).prop('selected', true);

                if (producto.rutaImagen) {
                    imagenPreview.attr('src', `/imagenes/${producto.rutaImagen}`).show();
                }

                productoModal.show();
            } else {
                mostrarNotificacion('No se pudo cargar la información del producto', 'error');
            }
        });
    });

    // Evento de envío del formulario
    formProducto.on('submit', function (e) {
        e.preventDefault();

        // Validación de Bootstrap
        if (!this.checkValidity()) {
            e.stopPropagation();
            $(this).addClass('was-validated');
            return;
        }

        const formData = new FormData(this);
        const url = editMode ? `/productos/api/actualizar/${editId}` : '/productos/api/guardar';
        const method = editMode ? 'PUT' : 'POST';

        $.ajax({
            url: url,
            type: method,
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                productoModal.hide();
                cargarProductos();
                const message = editMode ? 'Producto actualizado con éxito' : 'Producto creado con éxito';
                mostrarNotificacion(message, 'success');
            },
            error: function (xhr) {
                const errorMsg = xhr.responseJSON?.message || 'Error al guardar el producto.';
                mostrarNotificacion(errorMsg, 'error');
            }
        });
    });

    // --- ACCIONES DE PRODUCTO ---

    // Cambiar estado (Activar/Desactivar)
    galeriaProductos.on('click', '.btn-toggle-status', function () {
        const id = $(this).data('id');

        Swal.fire({
            title: '¿Estás seguro?',
            text: "Se cambiará el estado del producto.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Sí, cambiar estado',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/productos/api/cambiar-estado/${id}`, function (response) {
                    if (response.success) {
                        mostrarNotificacion(response.message, 'success');
                        cargarProductos();
                    } else {
                        mostrarNotificacion(response.message, 'error');
                    }
                });
            }
        });
    });

    // Eliminar producto
    galeriaProductos.on('click', '.btn-delete', function () {
        const id = $(this).data('id');

        Swal.fire({
            title: '¿Estás seguro de eliminar?',
            text: "Esta acción no se puede revertir.",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#d33',
            cancelButtonColor: '#3085d6',
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/productos/api/eliminar/${id}`, function (response) {
                    if (response.success) {
                        mostrarNotificacion(response.message, 'success');
                        cargarProductos();
                    } else {
                        mostrarNotificacion(response.message, 'error');
                    }
                });
            }
        });
    });

    // --- UTILIDADES ---

    // Buscador de productos
    $('#buscadorProductos').on('keyup', function () {
        const searchTerm = $(this).val().toLowerCase();
        $('.product-container').each(function () {
            const nombreProducto = $(this).data('nombre');
            if (nombreProducto.includes(searchTerm)) {
                $(this).show();
            } else {
                $(this).hide();
            }
        });
    });

    // Vista previa de la imagen
    $('#imagenFile').on('change', function () {
        if (this.files && this.files[0]) {
            const reader = new FileReader();
            reader.onload = function (e) {
                imagenPreview.attr('src', e.target.result).show();
            };
            reader.readAsDataURL(this.files[0]);
        }
    });

    /**
     * Muestra una notificación tipo toast.
     * @param {string} message - El mensaje a mostrar.
     * @param {string} type - El tipo de notificación ('success', 'error', 'warning').
     */
    function mostrarNotificacion(message, type = 'info') {
        const Toast = Swal.mixin({
            toast: true,
            position: 'top-end',
            showConfirmButton: false,
            timer: 3000,
            timerProgressBar: true,
            didOpen: (toast) => {
                toast.addEventListener('mouseenter', Swal.stopTimer);
                toast.addEventListener('mouseleave', Swal.resumeTimer);
            }
        });

        Toast.fire({
            icon: type,
            title: message
        });
    }
});