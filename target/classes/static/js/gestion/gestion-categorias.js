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
            url: "/gestion/categorias/api/listar",
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
                        <div class="d-flex gap-1 justify-content-center">
                            <button data-id="${data.id}" class="action-btn action-btn-edit btn-editar" title="Editar">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-pencil-square" viewBox="0 0 16 16">
                                    <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                                    <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
                                </svg>
                            </button>
                            <div class="form-check form-switch">
                                <input class="form-check-input btn-estado" type="checkbox" role="switch"
                                       data-id="${data.id}" ${data.estado === 1 ? 'checked' : ''}
                                       title="${data.estado === 1 ? 'Desactivar' : 'Activar'}">
                            </div>
                            <button data-id="${data.id}" class="action-btn action-btn-delete btn-eliminar" title="Eliminar">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash3-fill" viewBox="0 0 16 16">
                                    <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
                                </svg>
                            </button>
                        </div>
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
            url: "/gestion/categorias/api/guardar",
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
        $.get(`/gestion/categorias/api/${id}`, function (res) {
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
    $("#tablaCategorias").on("change", ".btn-estado", function () {
        const id = $(this).data("id");
        $.post(`/gestion/categorias/api/cambiar-estado/${id}`, function (res) {
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
                $.post(`/gestion/categorias/api/eliminar/${id}`, function (res) {
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