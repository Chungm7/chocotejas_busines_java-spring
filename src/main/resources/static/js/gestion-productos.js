let tablaProductos;
let productoModal;
let formProducto;
let productoActual = null;

$(document).ready(function () {
    productoModal = new bootstrap.Modal(document.getElementById("productoModal"));
    formProducto = $("#formProducto");

    // Inicializar DataTable
    tablaProductos = $("#tablaProductos").DataTable({
        responsive: true,
        ajax: {
            url: "/productos/api/listar",
            dataSrc: "data"
        },
        columns: [
            { data: "id" },
            {
                data: "imagen",
                render: function (imagen, type, row) {
                    if (!imagen) return '<span class="text-muted">Sin imagen</span>';
                    return `<img src="/imagenes/${imagen}" alt="${row.nombre}" style="width: 50px; height: 50px; object-fit: cover;" class="rounded">`;
                }
            },
            { data: "nombre" },
            {
                data: "descripcion",
                render: function (descripcion) {
                    return descripcion && descripcion.length > 50 ?
                        descripcion.substring(0, 50) + '...' :
                        (descripcion || 'Sin descripción');
                }
            },
            {
                data: "precio",
                render: function (precio) {
                    return `S/ ${parseFloat(precio || 0).toFixed(2)}`;
                }
            },
            {
                data: "stock",
                render: function (stock) {
                    return stock || 0;
                }
            },
            {
                data: "categoria",
                render: function (categoria) {
                    return categoria ? categoria.nombre : 'Sin categoría';
                }
            },
            {
                data: "estado",
                render: function (estado) {
                    return estado === 1
                        ? '<span class="badge bg-success">Activo</span>'
                        : '<span class="badge bg-secondary">Inactivo</span>';
                }
            },
            {
                data: null,
                render: function (data) {
                    if (data.estado === 2) return '<span class="text-muted">Eliminado</span>';

                    return `
                        <button class="btn btn-sm btn-warning btn-editar" data-id="${data.id}" title="Editar">
                            <i class="fas fa-edit"></i>
                        </button>
                        <button class="btn btn-sm btn-info btn-estado" data-id="${data.id}" title="${data.estado === 1 ? 'Desactivar' : 'Activar'}">
                            ${data.estado === 1 ? '<i class="fas fa-pause"></i>' : '<i class="fas fa-play"></i>'}
                        </button>
                        <button class="btn btn-sm btn-danger btn-eliminar" data-id="${data.id}" title="Eliminar">
                            <i class="fas fa-trash"></i>
                        </button>
                    `;
                }
            }
        ],
        language: { url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json" }
    });

    // Cargar categorías al iniciar
    cargarCategorias();

    // Botón nuevo producto
    $("#btnNuevoProducto").click(function () {
        resetForm();
        $("#productoModalLabel").text("Nuevo Producto");
        $("#imagenFile").prop("required", true);
        productoModal.show();
    });

    // Guardar/Actualizar producto - CORREGIDO
    formProducto.on("submit", function (e) {
        e.preventDefault();

        const idProducto = $("#idProducto").val();
        const isEdit = !!idProducto;

        // Validación diferente para nuevo vs editar
        if (!isEdit) {
            // Para NUEVO producto: validar campos obligatorios
            if (!formProducto[0].checkValidity()) {
                formProducto.addClass('was-validated');
                mostrarNotificacion("❌ Complete los campos requeridos", "warning");
                return;
            }
        }

        const formData = new FormData();
        const btnSubmit = $(this).find('button[type="submit"]');
        const originalText = btnSubmit.html();

        // Mostrar loading
        btnSubmit.prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span> Procesando...');

        // Para NUEVO producto: enviar todos los campos
        if (!isEdit) {
            formData.append("nombre", $("#nombre").val().trim());
            formData.append("descripcion", $("#descripcion").val().trim());
            formData.append("precio", $("#precio").val());
            formData.append("stock", $("#stock").val() || "0"); // Stock puede ser 0
            formData.append("categoria", $("#categoria").val());
            formData.append("imagenFile", $("#imagenFile")[0].files[0]);
        }
        // Para EDITAR producto: enviar solo campos modificados
        else {
            let hasChanges = false;

            // Comparar cada campo con los valores originales
            const nombre = $("#nombre").val().trim();
            if (nombre !== productoActual.nombre) {
                formData.append("nombre", nombre);
                hasChanges = true;
            }

            const descripcion = $("#descripcion").val().trim();
            if (descripcion !== (productoActual.descripcion || "")) {
                formData.append("descripcion", descripcion);
                hasChanges = true;
            }

            const precio = parseFloat($("#precio").val());
            if (precio !== parseFloat(productoActual.precio)) {
                formData.append("precio", precio.toString());
                hasChanges = true;
            }

            const stock = parseInt($("#stock").val()) || 0;
            if (stock !== parseInt(productoActual.stock || 0)) {
                formData.append("stock", stock.toString());
                hasChanges = true;
            }

            const categoria = $("#categoria").val();
            if (categoria && productoActual.categoria &&
                categoria !== productoActual.categoria.id.toString()) {
                formData.append("categoria", categoria);
                hasChanges = true;
            }

            const imagenFile = $("#imagenFile")[0].files[0];
            if (imagenFile) {
                formData.append("imagenFile", imagenFile);
                hasChanges = true;
            }

            // Si no hay cambios, no enviar
            if (!hasChanges) {
                mostrarNotificacion("ℹ️ No se realizaron cambios", "info");
                btnSubmit.prop('disabled', false).html(originalText);
                return;
            }
        }

        const url = isEdit ? `/productos/api/actualizar/${idProducto}` : "/productos/api/guardar";

        $.ajax({
            url: url,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (response) {
                if (response.success) {
                    mostrarNotificacion("✅ " + response.message, "success");
                    productoModal.hide();
                    tablaProductos.ajax.reload();
                    resetForm();
                } else {
                    mostrarNotificacion("❌ " + response.message, "danger");
                }
            },
            error: function (xhr) {
                let errorMsg = "Error al procesar la solicitud";
                try {
                    const response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {
                    errorMsg = xhr.statusText || errorMsg;
                }
                mostrarNotificacion("❌ " + errorMsg, "danger");
            },
            complete: function() {
                btnSubmit.prop('disabled', false).html(originalText);
            }
        });
    });

    // Editar producto - MEJORADO
    $(document).on("click", ".btn-editar", function () {
        const id = $(this).data("id");
        const btn = $(this);
        const originalHtml = btn.html();

        btn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span>');

        $.get(`/productos/api/${id}`)
            .done(function (response) {
                if (response.success && response.data) {
                    productoActual = response.data;
                    populateForm(productoActual);
                    $("#productoModalLabel").text("Editar Producto");
                    $("#imagenFile").prop("required", false); // Imagen opcional en edición
                    productoModal.show();
                } else {
                    mostrarNotificacion("❌ No se pudo cargar el producto", "danger");
                }
            })
            .fail(function(xhr) {
                mostrarNotificacion("❌ Error al cargar el producto", "danger");
            })
            .always(function() {
                btn.prop('disabled', false).html(originalHtml);
            });
    });

    // Cambiar estado
    $(document).on("click", ".btn-estado", function () {
        const id = $(this).data("id");

        Swal.fire({
            title: '¿Cambiar estado?',
            text: 'El producto cambiará entre activo e inactivo',
            icon: 'question',
            showCancelButton: true,
            confirmButtonText: 'Sí, cambiar',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/productos/api/cambiar-estado/${id}`)
                    .done(function (response) {
                        if (response.success) {
                            mostrarNotificacion("✅ " + response.message, "success");
                            tablaProductos.ajax.reload();
                        } else {
                            mostrarNotificacion("❌ " + response.message, "danger");
                        }
                    })
                    .fail(function() {
                        mostrarNotificacion("❌ Error al cambiar estado", "danger");
                    });
            }
        });
    });

    // Eliminar producto
    $(document).on("click", ".btn-eliminar", function () {
        const id = $(this).data("id");

        Swal.fire({
            title: "¿Eliminar producto?",
            text: "Esta acción no se puede deshacer",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/productos/api/eliminar/${id}`)
                    .done(function (response) {
                        if (response.success) {
                            mostrarNotificacion("✅ " + response.message, "success");
                            tablaProductos.ajax.reload();
                        } else {
                            mostrarNotificacion("❌ " + response.message, "danger");
                        }
                    })
                    .fail(function() {
                        mostrarNotificacion("❌ Error al eliminar", "danger");
                    });
            }
        });
    });

    // Vista previa de imagen
    $("#imagenFile").on("change", function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $("#imagenPreview").attr("src", e.target.result).show();
            };
            reader.readAsDataURL(file);
        }
    });

    // Al cerrar el modal, resetear
    $("#productoModal").on("hidden.bs.modal", function () {
        resetForm();
    });
});

function resetForm() {
    formProducto[0].reset();
    $("#idProducto").val("");
    $("#imagenPreview").hide();
    formProducto.removeClass('was-validated');
    productoActual = null;
    // Establecer stock por defecto a 0
    $("#stock").val(0);
}

function populateForm(producto) {
    resetForm();
    productoActual = producto;

    $("#idProducto").val(producto.id);
    $("#nombre").val(producto.nombre || "");
    $("#descripcion").val(producto.descripcion || "");
    $("#precio").val(producto.precio || "");
    $("#stock").val(producto.stock || 0); // Aceptar stock 0

    // Cargar categorías y seleccionar la del producto
    cargarCategoriasParaEdicion(producto.categoria);

    // Mostrar imagen actual
    if (producto.imagen) {
        $("#imagenPreview")
            .attr("src", `/imagenes/${producto.imagen}`)
            .show();
    }
}

function cargarCategorias() {
    $.get("/categorias/api/listar")
        .done(function (response) {
            if (response.success) {
                const select = $("#categoria");
                let options = '<option value="" disabled>Seleccione una categoría</option>';

                response.data.forEach(categoria => {
                    if (categoria.estado === 1) {
                        options += `<option value="${categoria.id}">${categoria.nombre}</option>`;
                    }
                });

                select.html(options);
            }
        })
        .fail(function() {
            console.error("Error cargando categorías");
        });
}

function cargarCategoriasParaEdicion(categoriaProducto) {
    $.get("/categorias/api/listar")
        .done(function (response) {
            if (response.success) {
                const select = $("#categoria");
                let options = '<option value="" disabled>Seleccione una categoría</option>';

                response.data.forEach(categoria => {
                    if (categoria.estado === 1) {
                        const selected = (categoriaProducto && categoria.id === categoriaProducto.id) ?
                            'selected' : '';
                        options += `<option value="${categoria.id}" ${selected}>${categoria.nombre}</option>`;
                    }
                });

                select.html(options);

                // Asegurar que la categoría esté seleccionada
                if (categoriaProducto) {
                    setTimeout(() => {
                        $("#categoria").val(categoriaProducto.id);
                    }, 100);
                }
            }
        })
        .fail(function() {
            console.error("Error cargando categorías");
        });
}

function mostrarNotificacion(mensaje, tipo) {
    const alert = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
    $("#notification-container").html(alert);
    setTimeout(() => $(".alert").alert("close"), 5000);
}