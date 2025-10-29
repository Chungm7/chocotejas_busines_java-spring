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

        // LIMPIAR COMPLETAMENTE LA INFORMACIÓN DE LA API
        $("#infoApi").hide().empty().removeClass("alert-warning alert-info");
        $("#nombreCompleto").val("");
        $("#formCliente button[type='submit']").prop("disabled", true);
    });
    // Consultar API al perder foco del número de documento
    $("#numeroDocumento").blur(function() {
        const tipo = $("#tipoDocumento").val();
        const numero = $(this).val().trim();
        const idCliente = $("#idCliente").val();

        // Validar formato básico
        if (!tipo || !numero) return;

        const esDNIValido = tipo === "DNI" && numero.length === 8 && /^\d+$/.test(numero);
        const esRUCValido = tipo === "RUC" && numero.length === 11 && /^\d+$/.test(numero);

        if (!esDNIValido && !esRUCValido) {
            mostrarNotificacion("Número de documento inválido", "warning");
            return;
        }

        // SIEMPRE consultar la API, sin importar si es nuevo cliente o edición
        consultarApiDocumento(tipo, numero);
    });

// Botón nuevo cliente
    $("#btnNuevoCliente").click(function () {
        formCliente[0].reset();
        $("#idCliente").val("");
        $("#nombreCompleto").val("");
        $("#clienteModalLabel").text("Nuevo Cliente");

        // RESET COMPLETO de la información de la API
        $("#infoApi").hide().empty().removeClass("alert-warning alert-danger alert-info");
        $("#formCliente button[type='submit']").prop("disabled", true);

        clienteModal.show();
    });

    // Guardar cliente
// Guardar cliente
    formCliente.submit(async function (e) {  // Agregar async aquí
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

        // Validar formato del documento
        if (tipoDocumento === "DNI" && (!/^\d{8}$/.test(numeroDocumento))) {
            mostrarNotificacion("El DNI debe tener 8 dígitos numéricos", "danger");
            return;
        }

        if (tipoDocumento === "RUC" && (!/^\d{11}$/.test(numeroDocumento))) {
            mostrarNotificacion("El RUC debe tener 11 dígitos numéricos", "danger");
            return;
        }

        if (!direccion) {
            mostrarNotificacion("Ingrese la dirección de entrega", "danger");
            return;
        }

        // Para nuevo cliente, validar que se haya consultado la API y tenga nombre completo
        if (!isEdit) {
            if (!nombreCompleto) {
                mostrarNotificacion("Debe consultar el documento primero para obtener los datos de la API", "warning");
                return;
            }

            // Verificar una última vez que el documento no exista (por si acaso)
            try {
                const existe = await verificarDocumentoExistente(numeroDocumento, null);
                if (existe) {
                    mostrarNotificacion("Ya existe un cliente con este número de documento", "warning");
                    return;
                }
            } catch (error) {
                console.error("Error verificando documento:", error);
                // Continuar aunque falle la verificación
            }
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
                $("#nombreCompleto").val(cliente.nombreCompleto);

                // FORZAR la consulta a la API para mostrar datos actualizados
                setTimeout(() => {
                    consultarApiDocumento(cliente.tipoDocumento, cliente.numeroDocumento);
                }, 100);

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
});

function consultarApiDocumento(tipo, numero) {
    // Mostrar loading
    $("#infoApi").html('<small><i class="spinner-border spinner-border-sm"></i> Consultando API...</small>')
        .show()
        .removeClass("alert-warning alert-danger")
        .addClass("alert-info");

    let url;
    if (tipo === "DNI") {
        url = `https://miapi.cloud/v1/dni/${numero}`;
    } else {
        url = `https://miapi.cloud/v1/ruc/${numero}`;
    }

    $.ajax({
        url: url,
        type: "GET",
        headers: {
            "Authorization": `Bearer ${API_TOKEN}`,
            "Accept": "application/json"
        },
        success: function (res) {
            if (res.success && res.datos) {
                let nombreCompleto = "";
                let estadoApi = "";
                let infoHtml = "";

                if (tipo === "DNI") {
                    const datos = res.datos;
                    nombreCompleto = `${datos.nombres} ${datos.ape_paterno} ${datos.ape_materno}`.trim();
                    estadoApi = "ACTIVO";

                    infoHtml = `
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <strong class="text-dark">Información del DNI:</strong><br>
                                <strong>Nombres:</strong> ${datos.nombres}<br>
                                <strong>Apellido Paterno:</strong> ${datos.ape_paterno}<br>
                                <strong>Apellido Materno:</strong> ${datos.ape_materno}<br>
                                <strong>Nombre Completo:</strong> <span class="fw-bold text-success">${nombreCompleto}</span>
                            </div>
                            <button type="button" class="btn-close" onclick="$('#infoApi').hide()"></button>
                        </div>
                    `;
                } else {
                    const datos = res.datos;
                    nombreCompleto = datos.razon_social;
                    estadoApi = datos.estado;

                    infoHtml = `
                        <div class="d-flex justify-content-between align-items-start">
                            <div>
                                <strong class="text-dark">Información del RUC:</strong><br>
                                <strong>Razón Social:</strong> <span class="fw-bold text-success">${datos.razon_social}</span><br>
                                <strong>Estado:</strong> ${datos.estado}<br>
                                <strong>Condición:</strong> ${datos.condicion}
                            </div>
                            <button type="button" class="btn-close" onclick="$('#infoApi').hide()"></button>
                        </div>
                    `;
                }

                // Actualizar el campo oculto con el nombre completo
                $("#nombreCompleto").val(nombreCompleto);

                // Mostrar información PERMANENTE
                $("#infoApi").html(`<small>${infoHtml}</small>`)
                    .removeClass("alert-warning alert-danger")
                    .addClass("alert-info")
                    .show();

                if (tipo === "RUC") {
                    if (estadoApi !== "ACTIVO") {
                        $("#infoApi").removeClass("alert-info").addClass("alert-warning");
                        mostrarNotificacion("El RUC no está activo. No se puede registrar.", "warning");
                        $("#formCliente button[type='submit']").prop("disabled", true);
                    } else {
                        $("#formCliente button[type='submit']").prop("disabled", false);
                        mostrarNotificacion("RUC validado correctamente", "success");
                    }
                } else {
                    $("#formCliente button[type='submit']").prop("disabled", false);
                    mostrarNotificacion("DNI validado correctamente", "success");
                }

            } else {
                $("#infoApi").hide();
                mostrarNotificacion("Documento no encontrado en la API", "warning");
                $("#formCliente button[type='submit']").prop("disabled", true);
            }
        },
        error: function(xhr, status, error) {
            $("#infoApi").hide();
            console.error("Error en la consulta API:", error);
            let errorMsg = "Error al consultar la API";

            if (xhr.status === 404) {
                errorMsg = "Documento no encontrado en la API";
            } else if (xhr.status === 401) {
                errorMsg = "Error de autenticación con la API";
            } else if (xhr.responseJSON && xhr.responseJSON.message) {
                errorMsg = xhr.responseJSON.message;
            }

            mostrarNotificacion(errorMsg, "danger");
            $("#formCliente button[type='submit']").prop("disabled", true);
        }
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
function mostrarNotificacion(mensaje, tipo) {
    // Crear un ID único para esta notificación
    const notificationId = 'notification-' + Date.now();

    const alert = `
        <div id="${notificationId}" class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;

    $("#notification-container").append(alert);

    // Auto-cerrar después de 4 segundos SOLO para esta notificación específica
    setTimeout(() => {
        $(`#${notificationId}`).alert('close');
    }, 4000);
}