let tablaCategorias;
let categoriaModal;
let formCategoria;

$(document).ready(function () {
    categoriaModal = new bootstrap.Modal(document.getElementById("categoriaModal"));
    formCategoria = $("#formCategoria");

    // Inicializar DataTable
    tablaCategorias = $("#tablaCategorias").DataTable({
        responsive: true,
        ajax: {
            url: "/categorias/api/listar",
            dataSrc: "data"
        },
        columns: [
            { data: "id" },
            { data: "nombre" },
            { data: "descripcion" },
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
                        <button class="btn btn-sm btn-danger btn-eliminar" data-id="${data.id}">Eliminar</button>
                    `;
                }
            }
        ],
        language: {
            url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
        }
    });

    // Botón nueva categoría
    $("#btnNuevaCategoria").click(function () {
        $("#formCategoria")[0].reset();
        $("#idCategoria").val("");
        $("#categoriaModalLabel").text("Nueva Categoría");
        categoriaModal.show();
    });

    // Guardar categoría
    formCategoria.submit(function (e) {
        e.preventDefault();
        const categoria = {
            id: $("#idCategoria").val() || null,
            nombre: $("#nombre").val(),
            descripcion: $("#descripcion").val(),
            estado: 1
        };

        $.ajax({
            url: "/categorias/api/guardar",
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(categoria),
            success: function (res) {
                if (res.success) {
                    mostrarNotificacion(res.message, "success");
                    categoriaModal.hide();
                    tablaCategorias.ajax.reload();
                } else {
                    mostrarNotificacion(res.message, "danger");
                }
            },
            error: function (xhr) {
                mostrarNotificacion("Error al guardar: " + xhr.responseText, "danger");
            }
        });
    });

    // Editar
    $("#tablaCategorias").on("click", ".btn-editar", function () {
        const id = $(this).data("id");
        $.get(`/categorias/api/${id}`, function (res) {
            if (res.success) {
                $("#idCategoria").val(res.data.id);
                $("#nombre").val(res.data.nombre);
                $("#descripcion").val(res.data.descripcion);
                $("#categoriaModalLabel").text("Editar Categoría");
                categoriaModal.show();
            } else {
                mostrarNotificacion("Categoría no encontrada", "danger");
            }
        });
    });

    // Cambiar estado
    $("#tablaCategorias").on("click", ".btn-estado", function () {
        const id = $(this).data("id");
        $.post(`/categorias/api/cambiar-estado/${id}`, function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "info");
                tablaCategorias.ajax.reload();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        });
    });

    // Eliminar
    $("#tablaCategorias").on("click", ".btn-eliminar", function () {
        const id = $(this).data("id");
        Swal.fire({
            title: "¿Eliminar categoría?",
            text: "Esta acción no se puede deshacer.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/categorias/api/eliminar/${id}`, function (res) {
                    if (res.success) {
                        mostrarNotificacion(res.message, "success");
                        tablaCategorias.ajax.reload();
                    } else {
                        mostrarNotificacion(res.message, "danger");
                    }
                });
            }
        });
    });
});

// Función de notificación (usa gestion-main.js)
function mostrarNotificacion(mensaje, tipo) {
    const alert = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
    $("#notification-container").append(alert);
    setTimeout(() => $(".alert").alert("close"), 4000);
}
