let tablaSliders;
let sliderModal;
let formSlider;

$(document).ready(function () {
    sliderModal = new bootstrap.Modal(document.getElementById("sliderModal"));
    formSlider = $("#formSlider");

    // Inicializar DataTable
    tablaSliders = $("#tablaSliders").DataTable({
        responsive: true,
        ajax: {
            url: "/gestion/sliders/api/listar",
            dataSrc: "data"
        },
        columns: [
            { data: "id" },
            {
                data: "ruta", // CAMBIADO: de "imagen" a "ruta"
                render: function (ruta, type, row) { // CAMBIADO: parámetro de imagen a ruta
                    return `<img src="../../../../../sliders/${ruta}" alt="Imagen slider" class="slider-img-thumbnail" style="max-height: 50px; max-width: 50px;" onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNTAiIGhlaWdodD0iNTAiIHZpZXdCb3g9IjAgMCA1MCA1MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjUwIiBoZWlnaHQ9IjUwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0yNSAzMEMyOC44NjYgMzAgMzIgMjYuODY2IDMyIDIzQzMyIDE5LjEzNCAyOC44NjYgMTYgMjUgMTZDMjEuMTM0IDE2IDE4IDE5LjEzNCAxOCAyM0MxOCAyNi44NjYgMjEuMTM0IDMwIDI1IDMwWiIgZmlsbD0iIzlDOEU5RiIvPgo8cGF0aCBkPSJNMTguNzUgMzRDMTcuNTAzIDM0IDE2LjM3NSAzNC42MDMgMTUuNjU2IDM1LjU5M0MxNi4zNzUgMzYuNTgzIDE3LjUwMyAzNy4xODggMTguNzUgMzcuMTg4SDMxLjI1QzMyLjQ5NyAzNy4xODggMzMuNjI1IDM2LjU4MyAzNC4zNDQgMzUuNTkzQzMzLjYyNSAzNC42MDMgMzIuNDk3IDM0IDMxLjI1IDM0SDE4Ljc1WiIgZmlsbD0iIzlDOEU5RiIvPgo8L3N2Zz4K'">`;
                }
            },
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
                data: "activo",
                render: function (activo, type, row) {
                    const colorClass = activo ? 'text-warning' : 'text-dark-chocolate';
                    const fill = activo ? 'currentColor' : 'none';
                    const title = activo ? 'Desactivar slider' : 'Activar slider';

                    return `
                <button class="btn btn-sm btn-destacado ${colorClass}" data-id="${row.id}" title="${title}">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" stroke="currentColor" stroke-width="1.5" fill="${fill}">
                        <path stroke-linecap="round" stroke-linejoin="round" d="M11.049 2.927c.3-.921 1.603-.921 1.902 0l1.519 4.674a1 1 0 00.95.69h4.915c.969 0 1.371 1.24.588 1.81l-3.976 2.888a1 1 0 00-.363 1.118l1.518 4.674c.3.922-.755 1.688-1.538 1.118l-3.976-2.888a1 1 0 00-1.176 0l-3.976 2.888c-.783.57-1.838-.197-1.538-1.118l1.518-4.674a1 1 0 00-.363-1.118l-3.976-2.888c-.784-.57-.38-1.81.588-1.81h4.914a1 1 0 00.951-.69l1.519-4.674z" />
                    </svg>
                </button>
            `;
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

    // Botón nuevo slider
    $("#btnNuevoSlider").click(function () {
        formSlider[0].reset();
        $("#idSlider").val("");
        $("#imagenPreviewContainer").hide();
        $("#sliderModalLabel").text("Nuevo Slider");
        $("#imagenFile").prop("required", true);
        sliderModal.show();
    });

    // Guardar slider
    formSlider.submit(function (e) {
        e.preventDefault();

        const formData = new FormData();
        const id = $("#idSlider").val();
        const isEdit = !!id;

        // Para nuevo slider, todos los campos son requeridos
        if (!isEdit) {
            formData.append("nombre", $("#nombre").val().trim());
            formData.append("descripcion", $("#descripcion").val().trim() || "Sin descripción");

            const imagenFile = $("#imagenFile")[0].files[0];
            if (!imagenFile) {
                mostrarNotificacion("La imagen es obligatoria para nuevos sliders", "danger");
                return;
            }
            formData.append("imagenFile", imagenFile);
        } else {
            // Para edición, solo campos modificados
            if ($("#nombre").val().trim() !== ($("#nombre").data('original') || '')) {
                formData.append("nombre", $("#nombre").val().trim());
            }
            if ($("#descripcion").val().trim() !== ($("#descripcion").data('original') || '')) {
                formData.append("descripcion", $("#descripcion").val().trim() || "Sin descripción");
            }

            const imagenFile = $("#imagenFile")[0].files[0];
            if (imagenFile) {
                formData.append("rutaFile", imagenFile);
            }
        }

        const url = isEdit ? `/gestion/sliders/api/actualizar/${id}` : "/gestion/sliders/api/guardar";

        $.ajax({
            url: url,
            type: "POST",
            data: formData,
            processData: false,
            contentType: false,
            success: function (res) {
                if (res.success) {
                    mostrarNotificacion(res.message, "success");
                    sliderModal.hide();
                    tablaSliders.ajax.reload();
                } else {
                    mostrarNotificacion(res.message, "danger");
                }
            },
            error: function (xhr) {
                let errorMsg = "Error al guardar el slider";
                try {
                    const response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {}
                mostrarNotificacion(errorMsg, "danger");
            }
        });
    });

    // Editar slider
    $("#tablaSliders").on("click", ".btn-editar", function () {
        const id = $(this).data("id");
        $.get(`/gestion/sliders/api/${id}`, function (res) {
            if (res.success) {
                const slider = res.data;

                // Guardar valores originales para comparación
                $("#nombre").data('original', slider.nombre);
                $("#descripcion").data('original', slider.descripcion);

                // Llenar formulario
                $("#idSlider").val(slider.id);
                $("#nombre").val(slider.nombre);
                $("#descripcion").val(slider.descripcion);

                // Mostrar imagen actual - CAMBIADO: usar 'imagen' en lugar de 'ruta'
                if (slider.imagen) {
                    $("#imagenPreview").attr("src", `../../../../../sliders/${slider.imagen}`);
                    $("#imagenPreviewContainer").show();
                } else {
                    $("#imagenPreviewContainer").hide();
                }

                $("#sliderModalLabel").text("Editar Slider");
                $("#imagenFile").prop("required", false);
                sliderModal.show();
            } else {
                mostrarNotificacion("Slider no encontrado", "danger");
            }
        }).fail(function() {
            mostrarNotificacion("Error al cargar el slider", "danger");
        });
    });

    // Cambiar estado
    $("#tablaSliders").on("change", ".btn-estado", function () {
        const id = $(this).data("id");
        $.post(`/gestion/sliders/api/cambiar-estado/${id}`, function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "info");
                tablaSliders.ajax.reload();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        });
    });

    // Eliminar
    $("#tablaSliders").on("click", ".btn-eliminar", function () {
        const id = $(this).data("id");
        Swal.fire({
            title: "¿Eliminar slider?",
            text: "Esta acción no se puede deshacer.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#d33"
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/gestion/sliders/api/eliminar/${id}`, function (res) {
                    if (res.success) {
                        mostrarNotificacion(res.message, "success");
                        tablaSliders.ajax.reload();
                    } else {
                        mostrarNotificacion(res.message, "danger");
                    }
                });
            }
        });
    });

    // Cambiar estado activo (estrella)
    $("#tablaSliders").on("click", ".btn-destacado", function () {
        const id = $(this).data("id");
        $.post(`/gestion/sliders/api/activar-logo/${id}`, function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "info");
                tablaSliders.ajax.reload();
            } else {
                mostrarNotificacion(res.message, "danger");
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

    // Auto-cerrar después de 4 segundos
    setTimeout(() => {
        $(".alert").alert("close");
    }, 4000);
}