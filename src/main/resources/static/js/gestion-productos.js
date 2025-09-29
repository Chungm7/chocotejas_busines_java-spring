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
                render: function (imagen, type, row) {
                    // Usar ruta correcta para las imágenes
                    return `<img src="../../../../../imagenes/${imagen}" alt="Imagen producto" class="img-thumbnail" style="max-height: 50px; max-width: 50px;" onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNTAiIGhlaWdodD0iNTAiIHZpZXdCb3g9IjAgMCA1MCA1MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjUwIiBoZWlnaHQ9IjUwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0yNSAzMEMyOC44NjYgMzAgMzIgMjYuODY2IDMyIDIzQzMyIDE5LjEzNCAyOC44NjYgMTYgMjUgMTZDMjEuMTM0IDE2IDE4IDE5LjEzNCAxOCAyM0MxOCAyNi44NjYgMjEuMTM0IDMwIDI1IDMwWiIgZmlsbD0iIzlDOEU5RiIvPgo8cGF0aCBkPSJNMTguNzUgMzRDMTcuNTAzIDM0IDE2LjM3NSAzNC42MDMgMTUuNjU2IDM1LjU5M0MxNi4zNzUgMzYuNTgzIDE3LjUwMyAzNy4xODggMTguNzUgMzcuMTg4SDMxLjI1QzMyLjQ5NyAzNy4xODggMzMuNjI1IDM2LjU4MyAzNC4zNDQgMzUuNTkzQzMzLjYyNSAzNC42MDMgMzIuNDk3IDM0IDMxLjI1IDM0SDE4Ljc1WiIgZmlsbD0iIzlDOEU5RiIvPgo8L3N2Zz4K'">`;
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
                        <button class="btn btn-sm btn-warning btn-editar" data-id="${data.id}" title="Editar">
                            <i class="bi bi-pencil"></i> Editar
                        </button>
                        <button class="btn btn-sm btn-info btn-estado" data-id="${data.id}" title="${data.estado === 1 ? 'Desactivar' : 'Activar'}">
                            <i class="bi bi-power"></i> ${data.estado === 1 ? "Desactivar" : "Activar"}
                        </button>
                        <button class="btn btn-sm btn-success btn-stock" data-id="${data.id}" data-stock="${data.stock}" title="Actualizar Stock">
                            <i class="bi bi-box"></i> Stock
                        </button>
                        <button class="btn btn-sm btn-danger btn-eliminar" data-id="${data.id}" title="Eliminar">
                            <i class="bi bi-trash"></i> Eliminar
                        </button>
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
        $("#precio").val("0");
        $("#stock").val("0");
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

        // Para nuevo producto, todos los campos son requeridos
        if (!isEdit) {
            formData.append("nombre", $("#nombre").val().trim());
            formData.append("precio", $("#precio").val() || "0");
            formData.append("categoria", $("#categoria").val());
            formData.append("descripcion", $("#descripcion").val().trim() || "Sin descripción");
            formData.append("stock", $("#stock").val() || "0");

            const imagenFile = $("#imagenFile")[0].files[0];
            if (!imagenFile) {
                mostrarNotificacion("La imagen es obligatoria para nuevos productos", "danger");
                return;
            }
            formData.append("imagenFile", imagenFile);
        } else {
            // Para edición, solo campos modificados
            if ($("#nombre").val().trim() !== ($("#nombre").data('original') || '')) {
                formData.append("nombre", $("#nombre").val().trim());
            }
            if ($("#precio").val() !== ($("#precio").data('original') || '0')) {
                formData.append("precio", $("#precio").val());
            }
            if ($("#categoria").val() !== ($("#categoria").data('original') || '')) {
                formData.append("categoria", $("#categoria").val());
            }
            if ($("#descripcion").val().trim() !== ($("#descripcion").data('original') || '')) {
                formData.append("descripcion", $("#descripcion").val().trim() || "Sin descripción");
            }
            if ($("#stock").val() !== ($("#stock").data('original') || '0')) {
                formData.append("stock", $("#stock").val());
            }

            const imagenFile = $("#imagenFile")[0].files[0];
            if (imagenFile) {
                formData.append("imagenFile", imagenFile);
            }
        }

        const url = isEdit ? `/productos/api/actualizar/${id}` : "/productos/api/guardar";

        $.ajax({
            url: url,
            type: "POST",
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
                $("#precio").data('original', producto.precio.toString());
                $("#descripcion").data('original', producto.descripcion);
                $("#stock").data('original', producto.stock.toString());
                $("#categoria").data('original', producto.categoria.toString()); // Usar ID de categoría

                // Llenar formulario
                $("#idProducto").val(producto.id);
                $("#nombre").val(producto.nombre);
                $("#precio").val(producto.precio);
                $("#descripcion").val(producto.descripcion);
                $("#stock").val(producto.stock);
                $("#categoria").val(producto.categoria); // Usar ID para seleccionar

                // Mostrar imagen actual
                if (producto.imagen) {
                    $("#imagenPreview").attr("src", `../../../../../imagenes/${producto.imagen}`);
                    $("#imagenPreviewContainer").show();
                } else {
                    $("#imagenPreviewContainer").hide();
                }

                $("#productoModalLabel").text("Editar Producto");
                $("#imagenFile").prop("required", false);
                productoModal.show();
            } else {
                mostrarNotificacion("Producto no encontrado", "danger");
            }
        }).fail(function() {
            mostrarNotificacion("Error al cargar el producto", "danger");
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
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#d33"
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
    }).fail(function() {
        mostrarNotificacion("Error al cargar las categorías", "danger");
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

    // Auto-cerrar después de 4 segundos
    setTimeout(() => {
        $(".alert").alert("close");
    }, 4000);
}