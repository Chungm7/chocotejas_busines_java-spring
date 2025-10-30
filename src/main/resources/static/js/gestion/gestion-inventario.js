let tablaInventario;
let productosDisponibles = [];

$(document).ready(function () {
    cargarProductosParaFiltro();
    inicializarDataTable();
    configurarEventos();
});

function cargarProductosParaFiltro() {
    $.get("/gestion/productos/api/listar", function (res) {
        if (res.success) {
            productosDisponibles = res.data;
            const selectProducto = $("#filtroProducto");

            productosDisponibles.forEach(producto => {
                selectProducto.append(new Option(producto.nombre, producto.id));
            });
        }
    }).fail(function() {
        mostrarNotificacion("Error al cargar los productos", "danger");
    });
}

function inicializarDataTable() {
    tablaInventario = $("#tablaInventario").DataTable({
        responsive: true,
        ajax: {
            url: "/gestion/inventario/api/listar",
            dataSrc: "data"
        },
        columns: [
            {
                data: "tipoMovimiento",
                render: function (tipo) {
                    let badgeClass = tipo === 'VENTA' ? 'bg-primary' : 'bg-warning';
                    let badgeText = tipo === 'VENTA' ? 'VENTA' : 'ACT. STOCK';
                    return `<span class="badge ${badgeClass}">${badgeText}</span>`;
                }
            },
            {
                data: "producto",
                render: function (producto) {
                    if (producto) {
                        return `
                            <div class="d-flex align-items-center">
                                <img src="../../../../../imagenes/${producto.imagen}" 
                                     alt="${producto.nombre}" 
                                     class="producto-img-thumbnail me-2"
                                     onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNTAiIGhlaWdodD0iNTAiIHZpZXdCb3g9IjAgMCA1MCA1MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjUwIiBoZWlnaHQ9IjUwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0yNSAzMEMyOC44NjYgMzAgMzIgMjYuODY2IDMyIDIzQzMyIDE5LjEzNCAyOC44NjYgMTYgMjUgMTZDMjEuMTM0IDE2IDE4IDE5LjEzNCAxOCAyM0MxOCAyNi44NjYgMjEuMTM0IDMwIDI1IDMwWiIgZmlsbD0iIzlDOEU5RiIvPgo8cGF0aCBkPSJNMTguNzUgMzRDMTcuNTAzIDM0IDE2LjM3NSAzNC42MDMgMTUuNjU2IDM1LjU5M0MxNi4zNzUgMzYuNTgzIDE3LjUwMyAzNy4xODggMTguNzUgMzcuMTg4SDMxLjI1QzMyLjQ5NyAzNy4xODggMzMuNjI1IDM2LjU4MyAzNC4zNDQgMzUuNTkzQzMzLjYyNSAzNC42MDMgMzIuNDk3IDM0IDMxLjI1IDM0SDE4Ljc1WiIgZmlsbD0iIzlDOEU5RiIvPgo8L3N2Zz4K'">
                                <div>
                                    <strong>${producto.nombre}</strong>
                                    <div class="small text-muted">Stock actual: ${producto.stock}</div>
                                </div>
                            </div>
                        `;
                    }
                    return '-';
                }
            },
            {
                data: "stockAnterior",
                render: function (stockAnterior) {
                    return `<span class="fw-semibold">${stockAnterior}</span>`;
                }
            },
            {
                data: "nuevoStock",
                render: function (nuevoStock) {
                    return `<span class="fw-bold text-dark">${nuevoStock}</span>`;
                }
            },
            {
                data: "diferencia",
                render: function (diferencia) {
                    let icono = diferencia > 0 ? 'bi-arrow-up-circle-fill text-success' :
                        diferencia < 0 ? 'bi-arrow-down-circle-fill text-danger' :
                            'bi-dash-circle text-secondary';
                    let texto = diferencia > 0 ? `+${diferencia}` : diferencia;
                    let badgeClass = diferencia > 0 ? 'bg-success' :
                        diferencia < 0 ? 'bg-danger' : 'bg-secondary';

                    return `
                        <div class="d-flex align-items-center">
                            <i class="bi ${icono} me-2"></i>
                            <span class="badge ${badgeClass}">${texto}</span>
                        </div>
                    `;
                }
            },
            {
                data: "comprobante",
                render: function (comprobante) {
                    return `<code class="text-dark">${comprobante}</code>`;
                }
            },
            {
                data: "fecha",
                render: function (fecha) {
                    return new Date(fecha).toLocaleString('es-ES', {
                        year: 'numeric',
                        month: '2-digit',
                        day: '2-digit',
                        hour: '2-digit',
                        minute: '2-digit'
                    });
                }
            },
            {
                data: "observaciones",
                render: function (observaciones) {
                    return observaciones ? `<small class="text-muted">${observaciones}</small>` : '-';
                }
            }
        ],
        language: {
            url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json"
        },
        order: [[1, 'desc']], // Ordenar por fecha descendente
        dom: '<"row"<"col-md-6"l><"col-md-6"f>>rt<"row"<"col-md-6"i><"col-md-6"p>>',
        pageLength: 25
    });
}

function configurarEventos() {
    $("#btnAplicarFiltros").click(function () {
        aplicarFiltros();
    });

    $("#btnLimpiarFiltros").click(function () {
        $("#filtroTipo").val("");
        $("#filtroProducto").val("");
        tablaInventario.search('').columns().search('').draw();
    });

    // Búsqueda en tiempo real
    $("#filtroTipo, #filtroProducto").change(function () {
        aplicarFiltros();
    });
}

function aplicarFiltros() {
    const tipo = $("#filtroTipo").val();
    const productoId = $("#filtroProducto").val();

    // Limpiar búsquedas anteriores
    tablaInventario.columns().search('');

    // Aplicar filtro por tipo
    if (tipo) {
        tablaInventario.column(2).search(tipo, true, false);
    }

    // Aplicar filtro por producto
    if (productoId) {
        const producto = productosDisponibles.find(p => p.id == productoId);
        if (producto) {
            tablaInventario.column(3).search(producto.nombre, true, false);
        }
    }

    tablaInventario.draw();
}

// Función de notificación
function mostrarNotificacion(mensaje, tipo) {
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