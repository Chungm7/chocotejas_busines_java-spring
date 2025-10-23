let tablaRedesSociales;
let redSocialModal;
let formRedSocial;

$(document).ready(function () {
    redSocialModal = new bootstrap.Modal(document.getElementById("redSocialModal"));
    formRedSocial = $("#formRedSocial");

    // Inicializar DataTable
    tablaRedesSociales = $("#tablaRedesSociales").DataTable({
        responsive: true,
        ajax: {
            url: "/gestion/redes-sociales/api/listar",
            dataSrc: "data"
        },
        columns: [
            { data: "id" },
            {
                data: "icono",
                render: function (icono, type, row) {
                    let claseCompleta = "bi ";
                    switch (icono) {
                        case "FACEBOOK": claseCompleta += "bi-facebook text-primary"; break;
                        case "TWITTER": claseCompleta += "bi-twitter text-info"; break;
                        case "INSTAGRAM": claseCompleta += "bi-instagram text-danger"; break;
                        case "YOUTUBE": claseCompleta += "bi-youtube text-danger"; break;
                        case "WHATSAPP": claseCompleta += "bi-whatsapp text-success"; break;
                        case "TIKTOK": claseCompleta += "bi-tiktok text-dark"; break;
                        default: claseCompleta = "bi-question-circle text-secondary";
                    }
                    return `<i class="${claseCompleta} red-social-icon" title="${icono}"></i>`;
                }
            },
            { data: "nombre" },
            {
                data: "url",
                render: function (url) {
                    return `<a href="${url}" target="_blank" class="url-link text-truncate d-inline-block" style="max-width: 200px;" title="${url}">${url}</a>`;
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

    // Vista previa del icono
    $("#icono").change(function() {
        const iconoSeleccionado = $(this).val();
        if (iconoSeleccionado) {
            let claseCompleta = "bi ";
            let colorClass = "";

            switch (iconoSeleccionado) {
                case "FACEBOOK":
                    claseCompleta += "bi-facebook";
                    colorClass = "text-primary";
                    break;
                case "TWITTER":
                    claseCompleta += "bi-twitter";
                    colorClass = "text-info";
                    break;
                case "INSTAGRAM":
                    claseCompleta += "bi-instagram";
                    colorClass = "text-danger";
                    break;
                case "YOUTUBE":
                    claseCompleta += "bi-youtube";
                    colorClass = "text-danger";
                    break;
                case "WHATSAPP":
                    claseCompleta += "bi-whatsapp";
                    colorClass = "text-success";
                    break;
                case "TIKTOK":
                    claseCompleta += "bi-tiktok";
                    colorClass = "text-dark";
                    break;
                default:
                    claseCompleta = "bi-question-circle";
                    colorClass = "text-secondary";
            }
            $("#iconoPreview").attr("class", `${claseCompleta} ${colorClass}`);
            $("#iconoPreviewContainer").show();
        } else {
            $("#iconoPreviewContainer").hide();
        }
    });

    // Botón nueva red social
    $("#btnNuevaRedSocial").click(function () {
        formRedSocial[0].reset();
        $("#idRedSocial").val("");
        $("#iconoPreviewContainer").hide();
        $("#redSocialModalLabel").text("Nueva Red Social");
        redSocialModal.show();
    });

    // Guardar red social
    formRedSocial.submit(function (e) {
        e.preventDefault();

        const id = $("#idRedSocial").val();
        const isEdit = !!id;

        const redSocialData = {
            nombre: $("#nombre").val().trim(),
            url: $("#url").val().trim(),
            icono: $("#icono").val()
        };

        if (isEdit) {
            redSocialData.id = id;
        }

        const url = isEdit ? `/gestion/redes-sociales/api/actualizar/${id}` : "/gestion/redes-sociales/api/guardar";

        $.ajax({
            url: url,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(redSocialData),
            success: function (res) {
                if (res.success) {
                    mostrarNotificacion(res.message, "success");
                    redSocialModal.hide();
                    tablaRedesSociales.ajax.reload();
                } else {
                    mostrarNotificacion(res.message, "danger");
                }
            },
            error: function (xhr) {
                let errorMsg = "Error al guardar la red social";
                try {
                    const response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {
                    // Si no se puede parsear, usar el mensaje por defecto
                }
                mostrarNotificacion(errorMsg, "danger");
            }
        });
    });

    // Editar red social
    $("#tablaRedesSociales").on("click", ".btn-editar", function () {
        const id = $(this).data("id");
        $.get(`/gestion/redes-sociales/api/${id}`, function (res) {
            if (res.success) {
                const redSocial = res.data;

                // Llenar formulario
                $("#idRedSocial").val(redSocial.id);
                $("#nombre").val(redSocial.nombre);
                $("#url").val(redSocial.url);
                $("#icono").val(redSocial.icono);

                // Mostrar vista previa del icono
                let claseCompleta = "bi ";
                let colorClass = "";

                switch (redSocial.icono) {
                    case "FACEBOOK":
                        claseCompleta += "bi-facebook";
                        colorClass = "text-primary";
                        break;
                    case "TWITTER":
                        claseCompleta += "bi-twitter";
                        colorClass = "text-info";
                        break;
                    case "INSTAGRAM":
                        claseCompleta += "bi-instagram";
                        colorClass = "text-danger";
                        break;
                    case "YOUTUBE":
                        claseCompleta += "bi-youtube";
                        colorClass = "text-danger";
                        break;
                    case "WHATSAPP":
                        claseCompleta += "bi-whatsapp";
                        colorClass = "text-success";
                        break;
                    case "TIKTOK":
                        claseCompleta += "bi-tiktok";
                        colorClass = "text-dark";
                        break;
                    default:
                        claseCompleta = "bi-question-circle";
                        colorClass = "text-secondary";
                }
                $("#iconoPreview").attr("class", `${claseCompleta} ${colorClass}`);
                $("#iconoPreviewContainer").show();

                $("#redSocialModalLabel").text("Editar Red Social");
                redSocialModal.show();
            } else {
                mostrarNotificacion("Red social no encontrada", "danger");
            }
        }).fail(function() {
            mostrarNotificacion("Error al cargar la red social", "danger");
        });
    });

    // Cambiar estado
    $("#tablaRedesSociales").on("change", ".btn-estado", function () {
        const id = $(this).data("id");
        $.post(`/gestion/redes-sociales/api/cambiar-estado/${id}`, function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "info");
                tablaRedesSociales.ajax.reload();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        });
    });

    // Eliminar
    $("#tablaRedesSociales").on("click", ".btn-eliminar", function () {
        const id = $(this).data("id");
        Swal.fire({
            title: "¿Eliminar red social?",
            text: "Esta acción no se puede deshacer.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#d33"
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/gestion/redes-sociales/api/eliminar/${id}`, function (res) {
                    if (res.success) {
                        mostrarNotificacion(res.message, "success");
                        tablaRedesSociales.ajax.reload();
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

    // Auto-cerrar después de 4 segundos
    setTimeout(() => {
        $(".alert").alert("close");
    }, 4000);
}