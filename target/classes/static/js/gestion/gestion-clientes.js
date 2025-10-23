let tablaClientes;
let clienteModal;
let formCliente;

// Token para las APIs
const API_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjozNjAsImV4cCI6MTc2MDM4Mzk3OX0.s8pdevKFpdbjfpK8gz7bBmgh18GgEvIt8b_VUsjksKw";

$(document).ready(function () {
    clienteModal = new bootstrap.Modal(document.getElementById("clienteModal"));
    formCliente = $("#formCliente");

    // Inicializar DataTable
    tablaClientes = $("#tablaClientes").DataTable({
        responsive: true,
        ajax: {
            url: "/gestion/clientes/api/listar",
            dataSrc: "data"
        },
        columns: [
            { data: "id" },
            {
                data: "tipoDocumento",
                render: function (tipoDocumento) {
                    const badgeClass = tipoDocumento === "DNI" ? "clientes-dni-badge" : "clientes-ruc-badge";
                    return `<span class="clientes-documento-badge ${badgeClass}">${tipoDocumento}</span>`;
                }
            },
            { data: "numeroDocumento" },
            { data: "nombreCompleto" },
            {
                data: "direccion",
                render: function (direccion) {
                    return `<span class="clientes-direccion-text" title="${direccion}">${direccion}</span>`;
                }
            },
            {
                data: "estado",
                render: function (estado) {
                    if (estado === 1) {
                        return '<span class="badge bg-success">Activo</span>';
                    } else if (estado === 0) {
                        return '<span class="badge bg-secondary">Inactivo</span>';
                    } else {
                        return '<span class="badge bg-danger">Eliminado</span>';
                    }
                }
            },
            {
                data: null,
                render: function (data) {
                    // Solo mostrar acciones para clientes no eliminados
                    if (data.estado === 2) {
                        return '<span class="text-muted">Eliminado</span>';
                    }

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

    // Validar número de documento según tipo
    $("#tipoDocumento").change(function() {
        const tipo = $(this).val();
        const numeroInput = $("#numeroDocumento");
        const helpText = $("#documentoHelp");

        if (tipo === "DNI") {
            numeroInput.attr("maxlength", "8");
            numeroInput.attr("pattern", "\\d{8}");
            helpText.text("DNI: 8 dígitos numéricos");
        } else if (tipo === "RUC") {
            numeroInput.attr("maxlength", "11");
            numeroInput.attr("pattern", "\\d{11}");
            helpText.text("RUC: 11 dígitos numéricos");
        } else {
            numeroInput.removeAttr("maxlength");
            numeroInput.removeAttr("pattern");
            helpText.text("DNI: 8 dígitos, RUC: 11 dígitos");
        }

        // Limpiar información de la API
        $("#infoApi").hide();
        $("#infoNombre").text("");
        $("#infoEstado").text("");
        $("#nombreCompleto").val("");
    });


    // Botón nuevo cliente
    $("#btnNuevoCliente").click(function () {
        formCliente[0].reset();
        $("#idCliente").val("");
        $("#nombreCompleto").val(""); // Resetear el campo oculto
        $("#clienteModalLabel").text("Nuevo Cliente");
        $("#infoApi").hide();
        clienteModal.show();
    });

    // Guardar cliente
    // Guardar cliente
    formCliente.submit(function (e) {
        e.preventDefault();

        const tipoDocumento = $("#tipoDocumento").val();
        const numeroDocumento = $("#numeroDocumento").val().trim();
        const direccion = $("#direccion").val().trim();
        const nombreCompleto = $("#nombreCompleto").val() ? $("#nombreCompleto").val().trim() : "";
        const id = $("#idCliente").val();
        const isEdit = !!id;

        // Validaciones básicas
        if (!tipoDocumento) {
            mostrarNotificacion("Seleccione el tipo de documento", "danger");
            return;
        }

        if (!numeroDocumento) {
            mostrarNotificacion("Ingrese el número de documento", "danger");
            return;
        }

        if (!direccion) {
            mostrarNotificacion("Ingrese la dirección", "danger");
            return;
        }

        // Para nuevo cliente, validar que se haya consultado la API
        if (!isEdit && !nombreCompleto) {
            mostrarNotificacion("Debe consultar el documento primero para obtener los datos", "warning");
            return;
        }

        // Para edición, si no hay nombreCompleto, usar el existente
        if (isEdit && !nombreCompleto) {
            // En edición, el nombreCompleto ya está en la base de datos
            // No es necesario validarlo nuevamente
        }

        const url = isEdit ? `/gestion/clientes/api/actualizar/${id}` : "/gestion/clientes/api/guardar";
        const data = {
            tipoDocumento: tipoDocumento,
            numeroDocumento: numeroDocumento,
            nombreCompleto: nombreCompleto,
            direccion: direccion
        };

        $.ajax({
            url: url,
            type: "POST",
            contentType: "application/x-www-form-urlencoded",
            data: data,
            success: function (res) {
                if (res.success) {
                    mostrarNotificacion(res.message, "success");
                    clienteModal.hide();
                    tablaClientes.ajax.reload();
                } else {
                    mostrarNotificacion(res.message, "danger");
                }
            },
            error: function (xhr) {
                let errorMsg = "Error al guardar el cliente";
                try {
                    const response = JSON.parse(xhr.responseText);
                    errorMsg = response.message || errorMsg;
                } catch (e) {}
                mostrarNotificacion(errorMsg, "danger");
            }
        });
    });

    // Editar cliente
    $("#tablaClientes").on("click", ".btn-editar", function () {
        const id = $(this).data("id");
        $.get(`/gestion/clientes/api/${id}`, function (res) {
            if (res.success) {
                const cliente = res.data;

                // Llenar formulario
                $("#idCliente").val(cliente.id);
                $("#tipoDocumento").val(cliente.tipoDocumento);
                $("#numeroDocumento").val(cliente.numeroDocumento);
                $("#direccion").val(cliente.direccion);
                $("#nombreCompleto").val(cliente.nombreCompleto); // Asegurar que se establezca

                // Ocultar información de API en edición
                $("#infoApi").hide();

                $("#clienteModalLabel").text("Editar Cliente");
                clienteModal.show();
            } else {
                mostrarNotificacion("Cliente no encontrado", "danger");
            }
        }).fail(function() {
            mostrarNotificacion("Error al cargar el cliente", "danger");
        });
    });

    // Cambiar estado
    $("#tablaClientes").on("change", ".btn-estado", function () {
        const id = $(this).data("id");
        $.post(`/gestion/clientes/api/cambiar-estado/${id}`, function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "info");
                tablaClientes.ajax.reload();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        });
    });

    // Eliminar
    $("#tablaClientes").on("click", ".btn-eliminar", function () {
        const id = $(this).data("id");
        Swal.fire({
            title: "¿Eliminar cliente?",
            text: "Esta acción cambiará el estado a 'Eliminado'.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#d33"
        }).then((result) => {
            if (result.isConfirmed) {
                $.post(`/gestion/clientes/api/eliminar/${id}`, function (res) {
                    if (res.success) {
                        mostrarNotificacion(res.message, "success");
                        tablaClientes.ajax.reload();
                    } else {
                        mostrarNotificacion(res.message, "danger");
                    }
                });
            }
        });
    });

    // Verificar cliente
    $("#btnVerificarCliente").click(function () {
        verificarCliente();
    });
});

function verificarCliente() {
    const tipoDocumento = $("#tipoDocumento").val();
    const numeroDocumento = $("#numeroDocumento").val().trim();

    if (!numeroDocumento) {
        mostrarNotificacion("Ingrese un número de documento", "danger");
        return;
    }

    $.get(`/gestion/clientes/api/consultar-documento?tipoDocumento=${tipoDocumento}&numeroDocumento=${numeroDocumento}`, function (res) {
        if (res.success) {
            const cliente = res.data;
            $("#nombreCompleto").val(cliente.nombreCompleto);
            $("#direccion").val(cliente.direccion);
            $("#infoNombre").text(cliente.nombreCompleto);
            $("#infoApi").show();
            mostrarNotificacion("Cliente encontrado y datos rellenados", "success");
        } else {
            mostrarNotificacion(res.message, "danger");
        }
    }).fail(function () {
        mostrarNotificacion("Error al verificar el cliente", "danger");
    });
}

function verificarDocumentoExistente(numeroDocumento, idCliente = null) {
    return new Promise((resolve, reject) => {
        $.ajax({
            url: "/gestion/clientes/api/verificar-documento",
            type: "POST",
            data: {
                numeroDocumento: numeroDocumento,
                idCliente: idCliente
            },
            success: function(res) {
                resolve(res.existe);
            },
            error: function() {
                reject(false);
            }
        });
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