let tablaProductos;
let productoModal;
let stockModal;
let formProducto;
let formStock;
let categorias = [];

$(document).ready(function () {
    productoModal = new bootstrap.Modal(document.getElementById("productoModal"));
    stockModal = new bootstrap.Modal(document.getElementById("stockModal"));
    formProducto = $("#formProducto");
    formStock = $("#formStock");

    // Cargar categorías al iniciar
    cargarCategorias();

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
                render: function (imagen) {
                    return `<img src="../../../../../imagenes/${imagen}" alt="Imagen producto" class="img-thumbnail" style="max-height: 50px;">`;
                }
            },
            { data: "nombre" },
            { data: "descripcion" },
            {
                data: "precio",
                render: function (precio) {
                    return `S/ ${parseFloat(precio).toFixed(2)}`;
                }
            },
            { data: "stock" },
            { data: "categoria.nombre" },
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
                    return `
                        <button class="btn btn-sm btn-warning btn-editar" data-id="${data.id}">Editar</button>
                        <button class="btn btn-sm btn-info btn-estado" data-id="${data.id}">${data.estado === 1 ? "Desactivar" : "Activar"}</button>
                        <button class="btn btn-sm btn-success btn-stock" data-id="${data.id}" data-stock="${data.stock}">Stock</button>
                        <button class="btn btn-sm btn-danger btn-eliminar" data-id="${data.id}">Eliminar</button>
                    `;
                }
            }
        ],
        language: {
            url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
        }
    });

    // Vista previa de imagen
    $("#imagenFile").change(function() {
        const file = this.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                $("#imagenPreview").attr("src", e.target.result);
                $("#imagenPreviewContainer").show();
            }
            reader.readAsDataURL(file);
        }
    });

    // Botón nuevo producto
    $("#btnNuevoProducto").click(function () {
        formProducto[0].reset();
        $("#idProducto").val("");
        $("#imagenPreviewContainer").hide();
        $("#productoModalLabel").text("Nuevo Producto");
        $("#imagenFile").prop("required", true);
        productoModal.show();
    });

    // Guardar producto
    formProducto.submit(function (e) {
        e.preventDefault();

        const formData = new FormData();
        const id = $("#idProducto").val();
        const isEdit = !!id;

        // Solo agregar campos que han sido modificados o son requeridos
        if (isEdit) {
            formData.append("id", id);
        }

        // Campos obligatorios para nuevo producto
        if (!isEdit || $("#nombre").val().trim() !== ($("#nombre").data('original') || '')) {
            formData.append("nombre", $("#nombre").val().trim());
        }

        if (!isEdit || $("#precio").val() !== ($("#precio").data('original') || '0')) {
            formData.append("precio", $("#precio").val());
        }

        if (!isEdit || $("#categoria").val() !== ($("#categoria").data('original') || '')) {
            formData.append("categoria", $("#categoria").val());
        }

        // Campos opcionales para edición
        const descripcion = $("#descripcion").val().trim();
        if (!isEdit || descripcion !== ($("#descripcion").data('original') || '')) {
            formData.append("descripcion", descripcion || "Sin descripción");
        }

        const stock = $("#stock").val();
        if (!isEdit || stock !== ($("#stock").data('original') || '0')) {
            formData.append("stock", stock);
        }

        // Imagen - solo si se seleccionó una nueva
        const imagenFile = $("#imagenFile")[0].files[0];
        if (imagenFile) {
            formData.append("imagenFile", imagenFile);
        } else if (!isEdit) {
            // Para nuevo producto, la imagen es obligatoria
            mostrarNotificacion("La imagen es obligatoria para nuevos productos", "danger");
            return;
        }

        // Validar que al menos hay un cambio en edición
        if (isEdit && formData.entries().next().done) {
            mostrarNotificacion("No se realizaron cambios", "warning");
            return;
        }

        const url = isEdit ? `/productos/api/actualizar/${id}` : "/productos/api/guardar";
        const method = "POST";

        $.ajax({
            url: url,
            type: method,
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                if (res.success) {
                    mostrarNotificacion(res.message, "success");
                    productoModal.hide();
                    tablaProductos.ajax.reload();
                } else {
                    mostrarNotificacion(res.message, "danger");
                }
            },
            error: function (xhr) {
                let errorMsg = "Error al guardar el producto";
                try {
                    const response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {}
                mostrarNotificacion(errorMsg, "danger");
            }
        });
    });

    // Editar producto
    $("#tablaProductos").on("click", ".btn-editar", function () {
        const id = $(this).data("id");
        $.get(`/productos/api/${id}`, function (res) {
            if (res.success) {
                const producto = res.data;

                // Guardar valores originales para comparación
                $("#nombre").data('original', producto.nombre);
                $("#precio").data('original', producto.precio);
                $("#descripcion").data('original', producto.descripcion);
                $("#stock").data('original', producto.stock);
                $("#categoria").data('original', producto.categoria);

                // Llenar formulario
                $("#idProducto").val(producto.id);
                $("#nombre").val(producto.nombre);
                $("#precio").val(producto.precio);
                $("#descripcion").val(producto.descripcion);
                $("#stock").val(producto.stock);
                $("#categoria").val(producto.categoria);

                // Mostrar imagen actual
                if (producto.imagen) {
                    $("#imagenPreview").attr("src", `../../../../../imagenes/${producto.imagen}`);
                    $("#imagenPreviewContainer").show();
                }

                $("#productoModalLabel").text("Editar Producto");
                $("#imagenFile").prop("required", false);
                productoModal.show();
            } else {
                mostrarNotificacion("Producto no encontrado", "danger");
            }
        });
    });

    // Cambiar estado
    $("#tablaProductos").on("click", ".btn-estado", function () {
        const id = $(this).data("id");
        $.post(`/productos/api/cambiar-estado/${id}`, function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "info");
                tablaProductos.ajax.reload();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        });
    });

    // Actualizar stock
    $("#tablaProductos").on("click", ".btn-stock", function () {
        const id = $(this).data("id");
        const currentStock = $(this).data("stock");

        $("#idProductoStock").val(id);
        $("#cantidad").val(currentStock);
        stockModal.show();
    });

    formStock.submit(function (e) {
        e.preventDefault();
        const id = $("#idProductoStock").val();
        const cantidad = $("#cantidad").val();

        $.post(`/productos/api/actualizar-stock/${id}`, { cantidad: cantidad }, function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "success");
                stockModal.hide();
                tablaProductos.ajax.reload();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        });
    });

    // Eliminar
    $("#tablaProductos").on("click", ".btn-eliminar", function () {
        const id = $(this).data("id");
        Swal.fire({
            title: "¿Eliminar producto?",
            text: "Esta acción no se puede deshacer.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/productos/api/eliminar/${id}`, function (res) {
                    if (res.success) {
                        mostrarNotificacion(res.message, "success");
                        tablaProductos.ajax.reload();
                    } else {
                        mostrarNotificacion(res.message, "danger");
                    }
                });
            }
        });
    });
});

// Función para cargar categorías
function cargarCategorias() {
    $.get("/categorias/api/listar", function (res) {
        if (res.success) {
            categorias = res.data;
            const select = $("#categoria");
            select.empty();
            select.append('<option value="">Seleccione una categoría</option>');

            categorias.forEach(categoria => {
                if (categoria.estado === 1) {
                    select.append(`<option value="${categoria.id}">${categoria.nombre}</option>`);
                }
            });
        }
    });
}

// Función de notificación
function mostrarNotificacion(mensaje, tipo) {
    const alert = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
    $("#notification-container").append(alert);
    setTimeout(() => $(".alert").alert("close"), 4000);
}