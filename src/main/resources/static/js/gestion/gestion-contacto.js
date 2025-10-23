let formContacto;

$(document).ready(function () {
    formContacto = $("#formContacto");

    // Cargar información de contacto al iniciar
    cargarContacto();

    // Botón cancelar
    $("#btnCancelar").click(function () {
        cargarContacto(); // Recargar los datos originales
        mostrarNotificacion("Cambios cancelados", "info");
    });

    // Guardar contacto
    formContacto.submit(function (e) {
        e.preventDefault();
        guardarContacto();
    });

    // Validación en tiempo real
    $("#telefono, #email, #direccion").on("input", function () {
        validarCampo($(this));
    });
});

function cargarContacto() {
    // Mostrar estado de carga
    $(".card").addClass("loading-contacto");

    $.get("/gestion/contacto/api/obtener", function (res) {
        if (res.success) {
            const contacto = res.data;

            // Llenar formulario
            $("#idContacto").val(contacto.id || "");
            $("#telefono").val(contacto.telefono || "");
            $("#email").val(contacto.email || "");
            $("#direccion").val(contacto.direccion || "");
            $("#descripcion").val(contacto.descripcion || "");

            mostrarNotificacion("Información de contacto cargada correctamente", "success");
        } else {
            if (res.message === "No se encontró información de contacto") {
                // Es normal si es la primera vez, mostrar mensaje informativo
                mostrarNotificacion("No hay información de contacto guardada. Complete el formulario y guarde.", "info");
            } else {
                mostrarNotificacion("Error al cargar la información: " + res.message, "danger");
            }
        }
    }).fail(function(xhr) {
        let errorMsg = "Error al cargar la información de contacto";
        try {
            const response = JSON.parse(xhr.responseText);
            errorMsg = response.message || errorMsg;
        } catch (e) {}
        mostrarNotificacion(errorMsg, "danger");
    }).always(function() {
        $(".card").removeClass("loading-contacto");
    });
}

function guardarContacto() {
    const contactoData = {
        telefono: $("#telefono").val().trim(),
        email: $("#email").val().trim(),
        direccion: $("#direccion").val().trim(),
        descripcion: $("#descripcion").val().trim()
    };

    // Validación básica del frontend
    if (!contactoData.telefono) {
        mostrarNotificacion("El teléfono es obligatorio", "danger");
        $("#telefono").focus();
        return;
    }

    if (!contactoData.email) {
        mostrarNotificacion("El email es obligatorio", "danger");
        $("#email").focus();
        return;
    }

    if (!contactoData.direccion) {
        mostrarNotificacion("La dirección es obligatoria", "danger");
        $("#direccion").focus();
        return;
    }

    // Validación de formato de email
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(contactoData.email)) {
        mostrarNotificacion("Por favor ingrese un email válido", "danger");
        $("#email").focus();
        return;
    }

    // Mostrar estado de carga en el botón
    const submitBtn = formContacto.find("button[type='submit']");
    const originalText = submitBtn.html();
    submitBtn.prop("disabled", true).html('<span class="spinner-border spinner-border-sm me-2"></span>Guardando...');

    $.ajax({
        url: "/gestion/contacto/api/actualizar",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(contactoData),
        success: function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "success");
                // Recargar los datos para asegurar consistencia
                cargarContacto();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        },
        error: function (xhr) {
            let errorMsg = "Error al guardar la información de contacto";
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

function validarCampo(campo) {
    const valor = campo.val().trim();

    if (campo.attr("name") === "email" && valor) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailRegex.test(valor)) {
            campo.addClass("is-invalid").removeClass("is-valid");
        } else {
            campo.addClass("is-valid").removeClass("is-invalid");
        }
    } else if (valor) {
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
$("#telefono, #email, #direccion").on("blur", function() {
    validarCampo($(this));
});