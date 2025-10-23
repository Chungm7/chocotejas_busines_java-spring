let tablaDeudas;
let pagoModal;
let formPago;

$(document).ready(function () {
    pagoModal = new bootstrap.Modal(document.getElementById("pagoModal"));
    formPago = $("#formPago");

    // Inicializar DataTable
    tablaDeudas = $("#tablaDeudas").DataTable({
        responsive: true,
        ajax: {
            url: "/gestion/deudas/api/listar",
            dataSrc: "data"
        },
        columns: [
            { data: "id" },
            { data: "cliente.nombreCompleto" },
            {
                data: "fecha",
                render: function (fecha) {
                    return new Date(fecha).toLocaleString('es-ES', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit'
                    });
                }
            },
            {
                data: "total",
                render: function (total) {
                    return `S/ ${total.toFixed(2)}`;
                }
            },
            {
                data: "montoPagado",
                render: function (montoPagado) {
                    return `S/ ${montoPagado.toFixed(2)}`;
                }
            },
            {
                data: null,
                render: function (data) {
                    const saldo = data.total - data.montoPagado;
                    return `S/ ${saldo.toFixed(2)}`;
                }
            },
            {
                data: null,
                render: function (data) {
                    return `
                        <button data-id="${data.id}" class="btn btn-sm btn-success btn-pagar">
                            <i class="bi bi-cash-coin me-1"></i>
                            Registrar Pago
                        </button>
                    `;
                }
            }
        ],
        language: {
            url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
        }
    });

    // Abrir modal de pago
    $("#tablaDeudas").on("click", ".btn-pagar", function () {
        const ventaId = $(this).data("id");
        $("#ventaIdPago").val(ventaId);
        pagoModal.show();
    });

    // Registrar pago
    formPago.submit(function (e) {
        e.preventDefault();
        registrarPago();
    });
});

function registrarPago() {
    const ventaId = $("#ventaIdPago").val();
    const monto = $("#montoPago").val();

    if (!monto || monto <= 0) {
        mostrarNotificacion("Ingrese un monto válido", "danger");
        return;
    }

    const pagoData = {
        ventaId: ventaId,
        monto: monto
    };

    $.ajax({
        url: "/gestion/pagos/api/registrar",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(pagoData),
        success: function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "success");
                pagoModal.hide();
                tablaDeudas.ajax.reload();
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        },
        error: function () {
            mostrarNotificacion("Error al registrar el pago", "danger");
        }
    });
}

function mostrarNotificacion(mensaje, tipo) {
    const alert = `
        <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>`;
    $("#notification-container").append(alert);

    setTimeout(() => {
        $(".alert").alert("close");
    }, 4000);
}
