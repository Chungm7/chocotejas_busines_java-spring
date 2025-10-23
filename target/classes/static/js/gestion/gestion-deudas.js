let tablaDeudas;

$(document).ready(function () {
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
});
