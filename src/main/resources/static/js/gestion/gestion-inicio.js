let formInicio;

$(document).ready(function () {
    formInicio = $("#formInicio");

    // Cargar información de inicio al iniciar
    cargarInicio();

    // Botón cancelar
    $("#btnCancelar").click(function () {
        cargarInicio(); // Recargar los datos originales
        mostrarNotificacion("Cambios cancelados", "info");
    });

    // Botón vista previa
    $("#btnVistaPrevia").click(function () {
        mostrarVistaPrevia();
    });

    // Contador de caracteres para el contenido
    $("#contenido").on("input", function () {
        actualizarContadorCaracteres();
        validarCampo($(this));
    });

    // Validación en tiempo real para el título
    $("#titulo").on("input", function () {
        validarCampo($(this));
        actualizarVistaPreviaTitulo();
    });

    // Guardar inicio
    formInicio.submit(function (e) {
        e.preventDefault();
        guardarInicio();
    });
});

function cargarInicio() {
    // Mostrar estado de carga
    $(".card").addClass("loading-inicio");

    $.get("/gestion/inicio/api/obtener", function (res) {
        if (res.success) {
            const inicio = res.data;

            // Llenar formulario
            $("#idInicio").val(inicio.id || "");
            $("#titulo").val(inicio.titulo || "");
            $("#contenido").val(inicio.contenido || "");

            // Actualizar contador
            actualizarContadorCaracteres();

            mostrarNotificacion("Información de inicio cargada correctamente", "success");
        } else {
            if (res.message === "No se encontró información de inicio") {
                // Es normal si es la primera vez, mostrar mensaje informativo
                mostrarNotificacion("No hay información de inicio guardada. Complete el formulario y guarde.", "info");
            } else {
                mostrarNotificacion("Error al cargar la información: " + res.message, "danger");
            }
        }
    }).fail(function(xhr) {
        let errorMsg = "Error al cargar la información de inicio";
        try {
            const response = JSON.parse(xhr.responseText);
            errorMsg = response.message || errorMsg;
        } catch (e) {}
        mostrarNotificacion(errorMsg, "danger");
    }).always(function() {
        $(".card").removeClass("loading-inicio");
    });
}

function guardarInicio() {
    const inicioData = {
        titulo: $("#titulo").val().trim(),
        contenido: $("#contenido").val().trim()
    };

    // Validación básica del frontend
    if (!inicioData.titulo) {
        mostrarNotificacion("El título es obligatorio", "danger");
        $("#titulo").focus();
        return;
    }

    if (!inicioData.contenido) {
        mostrarNotificacion("El contenido es obligatorio", "danger");
        $("#contenido").focus();
        return;
    }

    // Mostrar estado de carga en el botón
    const submitBtn = formInicio.find("button[type='submit']");
    const originalText = submitBtn.html();
    submitBtn.prop("disabled", true).html('<span class="spinner-border spinner-border-sm me-2"></span>Guardando...');

    $.ajax({
        url: "/gestion/inicio/api/actualizar",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(inicioData),
        success: function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "success");
                // Recargar los datos para asegurar consistencia
                cargarInicio();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        },
        error: function (xhr) {
            let errorMsg = "Error al guardar la información de inicio";
            try {
                const response = JSON.parse(xhr.responseText);
                errorMsg = response.message || errorMsg;
            } catch (e) {}
            mostrarNotificacion(errorMsg, "danger");
        },
        complete: function() {
            submitBtn.prop("disabled", false).html(originalText);
        }
    });
}

function mostrarVistaPrevia() {
    const titulo = $("#titulo").val().trim();
    const contenido = $("#contenido").val().trim();

    if (!titulo && !contenido) {
        mostrarNotificacion("Ingrese título y contenido para ver la vista previa", "warning");
        return;
    }

    // Actualizar vista previa
    $("#vistaPreviaTitulo").text(titulo || "[Sin título]");

    // Para el contenido, reemplazar saltos de línea por párrafos
    let contenidoFormateado = contenido;
    if (contenido) {
        // Reemplazar saltos de línea por <br> para mantener el formato
        contenidoFormateado = contenido.replace(/\n/g, '<br>');
    } else {
        contenidoFormateado = "[Sin contenido]";
    }

    $("#vistaPreviaContenido").html(contenidoFormateado);
    $("#vistaPreviaContainer").show();

    // Desplazarse suavemente a la vista previa
    $('html, body').animate({
        scrollTop: $("#vistaPreviaContainer").offset().top - 100
    }, 500);
}

function actualizarContadorCaracteres() {
    const contenido = $("#contenido").val();
    const contador = $("#contadorCaracteres");

    contador.text(contenido.length + " caracteres");

    // Advertencia si el contenido es muy extenso (más de 2000 caracteres)
    if (contenido.length > 2000) {
        contador.addClass("contador-advertencia");
    } else {
        contador.removeClass("contador-advertencia");
    }
}

function actualizarVistaPreviaTitulo() {
    const titulo = $("#titulo").val().trim();
    if (titulo && $("#vistaPreviaContainer").is(":visible")) {
        $("#vistaPreviaTitulo").text(titulo);
    }
}

function validarCampo(campo) {
    const valor = campo.val().trim();

    if (valor) {
        campo.addClass("is-valid").removeClass("is-invalid");
    } else {
        campo.removeClass("is-valid is-invalid");
    }
}

// Función de notificación
function mostrarNotificacion(mensaje, tipo) {
    // Cerrar notificaciones existentes
    $(".alert").alert("close");

    const alert = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            <div class="d-flex align-items-center">
                <i class="bi ${tipo === 'success' ? 'bi-check-circle-fill' : tipo === 'danger' ? 'bi-exclamation-triangle-fill' : tipo === 'info' ? 'bi-info-circle-fill' : 'bi-bell-fill'} me-2"></i>
                <span>${mensaje}</span>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
    $("#notification-container").append(alert);

    // Auto-cerrar después de 5 segundos (excepto para errores)
    if (tipo !== "danger") {
        setTimeout(() => {
            $(".alert").alert("close");
        }, 5000);
    }
}

// Validar formulario al perder el foco
$("#titulo, #contenido").on("blur", function() {
    validarCampo($(this));
});