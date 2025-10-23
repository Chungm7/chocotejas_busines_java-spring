let tablaVentas;
let ventaModal;
let detallesVentaModal;
let formVenta;
let productosDisponibles = [];
let clienteSeleccionado = null;

$(document).ready(function () {
    ventaModal = new bootstrap.Modal(document.getElementById("ventaModal"));
    detallesVentaModal = new bootstrap.Modal(document.getElementById("detallesVentaModal"));
    formVenta = $("#formVenta");

    // Cargar productos disponibles al iniciar
    cargarProductosDisponibles();

    // Inicializar DataTable
    tablaVentas = $("#tablaVentas").DataTable({
        responsive: true,
        ajax: {
            url: "/gestion/ventas/api/listar",
            dataSrc: "data"
        },
        columns: [
            { data: "id" },
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
                data: "cliente",
                render: function (cliente) {
                    return cliente ? cliente.nombreCompleto : 'N/A';
                }
            },
            {
                data: "total",
                render: function (total) {
                    return `S/ ${total.toFixed(2)}`;
                }
            },
            {
                data: null,
                render: function (data) {
                    return `
                        <div class="d-flex gap-1 justify-content-center">
                            <button data-id="${data.id}" class="action-btn action-btn-info btn-detalles" title="Ver detalles">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-eye-fill" viewBox="0 0 16 16">
                                    <path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0"/>
                                    <path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8m8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7"/>
                                </svg>
                            </button>
                            <button data-id="${data.id}" class="action-btn action-btn-delete btn-eliminar" title="Eliminar venta">
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
        },
        order: [[1, 'desc']] // Ordenar por fecha descendente
    });

    // Botón nueva venta
    $("#btnNuevaVenta").click(function () {
        resetFormVenta();
        $("#ventaModalLabel").text("Registrar Nueva Venta");
        ventaModal.show();
    });

    // Agregar producto al formulario
    $("#btnAgregarProducto").click(function () {
        agregarFilaProducto();
    });

    // Eliminar producto del formulario
    $("#productosContainer").on("click", ".btn-eliminar-producto", function () {
        $(this).closest(".producto-row").remove();
        calcularTotales();
    });

    // Cambio de producto seleccionado
    $("#productosContainer").on("change", ".producto-select", function () {
        const productoId = $(this).val();
        const fila = $(this).closest(".producto-row");
        const precioInput = fila.find(".precio-input");
        const stockInfo = fila.find(".stock-info");
        const cantidadInput = fila.find(".cantidad-input");

        if (productoId) {
            const producto = productosDisponibles.find(p => p.id == productoId);
            if (producto) {
                // Validar que el producto tenga precio
                if (!producto.precio || producto.precio <= 0) {
                    mostrarNotificacion(`El producto "${producto.nombre}" no tiene precio asignado`, "warning");
                    $(this).val(""); // Limpiar selección
                    precioInput.val("");
                    stockInfo.html("");
                    cantidadInput.prop("disabled", true);
                    return;
                }

                precioInput.val(`S/ ${producto.precio.toFixed(2)}`);

                // Actualizar información de stock
                let stockClass = "stock-disponible";
                if (producto.stock === 0) {
                    stockClass = "stock-agotado";
                } else if (producto.stock <= 5) {
                    stockClass = "stock-bajo";
                }

                stockInfo.html(`<span class="${stockClass}">Stock: ${producto.stock} unidades</span>`);

                // Establecer máximo en cantidad
                cantidadInput.attr("max", producto.stock);

                // Si el stock es 0, deshabilitar cantidad
                if (producto.stock === 0) {
                    cantidadInput.prop("disabled", true).val(0);
                    mostrarNotificacion(`El producto "${producto.nombre}" está agotado`, "warning");
                } else {
                    cantidadInput.prop("disabled", false).val(1);
                }
            }
        } else {
            precioInput.val("");
            stockInfo.html("");
            cantidadInput.prop("disabled", false);
        }

        calcularSubtotalFila(fila);
        calcularTotales();
    });

    // Cambio de cantidad
    $("#productosContainer").on("input", ".cantidad-input", function () {
        const fila = $(this).closest(".producto-row");
        calcularSubtotalFila(fila);
        calcularTotales();
    });

    // Guardar venta
    formVenta.submit(function (e) {
        e.preventDefault();
        registrarVenta();
    });

    // Ver detalles de venta
    $("#tablaVentas").on("click", ".btn-detalles", function () {
        const ventaId = $(this).data("id");
        cargarDetallesVenta(ventaId);
    });

    // Eliminar venta
    $("#tablaVentas").on("click", ".btn-eliminar", function () {
        const ventaId = $(this).data("id");
        eliminarVenta(ventaId);
    });

    // Buscar cliente
    $("#btnBuscarCliente").click(function () {
        buscarCliente();
    });
});

function buscarCliente() {
    const tipoDocumento = $("#tipoDocumento").val();
    const numeroDocumento = $("#numeroDocumento").val().trim();

    if (!numeroDocumento) {
        mostrarNotificacion("Ingrese un número de documento", "danger");
        return;
    }

    $.get(`/gestion/clientes/api/consultar-documento?tipoDocumento=${tipoDocumento}&numeroDocumento=${numeroDocumento}`, function (res) {
        if (res.success) {
            clienteSeleccionado = res.data;
            $("#nombreCliente").text(clienteSeleccionado.nombreCompleto);
            mostrarNotificacion("Cliente encontrado", "success");
        } else {
            mostrarNotificacion(res.message, "danger");
        }
    }).fail(function () {
        mostrarNotificacion("Error al buscar el cliente", "danger");
    });
}

function cargarProductosDisponibles() {
    $.get("/gestion/productos/api/listar", function (res) {
        if (res.success) {
            // Filtrar solo productos activos, con stock y con precio
            productosDisponibles = res.data.filter(p =>
                p.estado === 1 && p.precio !== null && p.precio !== undefined && p.precio > 0
            );
        }
    }).fail(function() {
        mostrarNotificacion("Error al cargar los productos disponibles", "danger");
    });
}

function agregarFilaProducto() {
    const template = document.getElementById("productoRowTemplate");
    const clone = template.content.cloneNode(true);

    const productoSelect = clone.querySelector(".producto-select");

    // Llenar select con productos disponibles
    productosDisponibles.forEach(producto => {
        const option = document.createElement("option");
        option.value = producto.id;
        option.textContent = `${producto.nombre} - S/ ${producto.precio.toFixed(2)}`;
        option.dataset.stock = producto.stock;
        productoSelect.appendChild(option);
    });

    $("#productosContainer").append(clone);
}

function resetFormVenta() {
    clienteSeleccionado = null;
    $("#tipoDocumento").val("DNI");
    $("#numeroDocumento").val("");
    $("#nombreCliente").text("-");
    $("#tipoPago").val("Contado");
    $("#productosContainer").empty();
    $("#subtotalVenta").text("S/ 0.00");
    $("#igvVenta").text("S/ 0.00");
    $("#totalVenta").text("S/ 0.00");
    agregarFilaProducto(); // Agregar una fila por defecto
}

function calcularSubtotalFila(fila) {
    const productoSelect = fila.find(".producto-select");
    const cantidadInput = fila.find(".cantidad-input");
    const precioInput = fila.find(".precio-input");
    const subtotalInput = fila.find(".subtotal-input");

    if (productoSelect.val() && cantidadInput.val()) {
        const producto = productosDisponibles.find(p => p.id == productoSelect.val());
        const cantidad = parseInt(cantidadInput.val()) || 0;
        const precio = producto.precio;
        const subtotal = cantidad * precio;

        subtotalInput.val(`S/ ${subtotal.toFixed(2)}`);
    } else {
        subtotalInput.val("S/ 0.00");
    }
}

function calcularTotales() {
    let subtotal = 0;

    $(".producto-row").each(function() {
        const productoSelect = $(this).find(".producto-select");
        const cantidadInput = $(this).find(".cantidad-input");

        if (productoSelect.val() && cantidadInput.val()) {
            const producto = productosDisponibles.find(p => p.id == productoSelect.val());
            const cantidad = parseInt(cantidadInput.val()) || 0;
            subtotal += cantidad * producto.precio;
        }
    });

    const igv = subtotal * 0.18; // 18% IGV
    const total = subtotal + igv;

    $("#subtotalVenta").text(`S/ ${subtotal.toFixed(2)}`);
    $("#igvVenta").text(`S/ ${igv.toFixed(2)}`);
    $("#totalVenta").text(`S/ ${total.toFixed(2)}`);
}

function registrarVenta() {
    if (!clienteSeleccionado) {
        mostrarNotificacion("Debe seleccionar un cliente", "danger");
        return;
    }

    const detalles = [];
    let hayErrores = false;
    let productosSinStock = [];
    let productosSinPrecio = [];

    // Validar cada fila de producto
    $(".producto-row").each(function() {
        const productoSelect = $(this).find(".producto-select");
        const cantidadInput = $(this).find(".cantidad-input");

        if (!productoSelect.val()) {
            hayErrores = true;
            productoSelect.focus();
            mostrarNotificacion("Seleccione un producto para todas las filas", "danger");
            return false;
        }

        if (!cantidadInput.val() || parseInt(cantidadInput.val()) < 1) {
            hayErrores = true;
            cantidadInput.focus();
            mostrarNotificacion("Ingrese una cantidad válida para todos los productos", "danger");
            return false;
        }

        const producto = productosDisponibles.find(p => p.id == productoSelect.val());
        const cantidad = parseInt(cantidadInput.val());

        // Validar que el producto exista
        if (!producto) {
            hayErrores = true;
            mostrarNotificacion("Producto no encontrado", "danger");
            return false;
        }

        // Validar que el producto tenga precio
        if (!producto.precio || producto.precio <= 0) {
            hayErrores = true;
            productosSinPrecio.push(producto.nombre);
            return false;
        }

        // Validar stock
        if (cantidad > producto.stock) {
            hayErrores = true;
            productosSinStock.push({
                producto: producto.nombre,
                stock: producto.stock,
                solicitado: cantidad
            });
            return false;
        }

        detalles.push({
            productoId: producto.id,
            cantidad: cantidad
        });
    });

    if (hayErrores) {
        if (productosSinPrecio.length > 0) {
            const mensaje = productosSinPrecio.map(p =>
                `${p} no tiene precio asignado`
            ).join('\n');
            mostrarNotificacion(`Productos sin precio:\n${mensaje}`, "danger");
        }
        if (productosSinStock.length > 0) {
            const mensaje = productosSinStock.map(p =>
                `${p.producto}: Stock ${p.stock}, Solicitado ${p.solicitado}`
            ).join('\n');
            mostrarNotificacion(`Stock insuficiente:\n${mensaje}`, "danger");
        }
        return;
    }

    if (detalles.length === 0) {
        mostrarNotificacion("Agregue al menos un producto a la venta", "danger");
        return;
    }

    const ventaData = {
        clienteId: clienteSeleccionado.id,
        tipoPago: $("#tipoPago").val(),
        detalles: detalles
    };

    // Mostrar estado de carga
    const submitBtn = formVenta.find("button[type='submit']");
    const originalText = submitBtn.html();
    submitBtn.prop("disabled", true).html('<span class="spinner-border spinner-border-sm me-2"></span>Registrando...');

    $.ajax({
        url: "/gestion/ventas/api/registrar",
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify(ventaData),
        success: function (res) {
            if (res.success) {
                mostrarNotificacion(res.message, "success");
                ventaModal.hide();
                tablaVentas.ajax.reload();
                cargarProductosDisponibles(); // Actualizar stock disponible
            } else {
                mostrarNotificacion(res.message, "danger");
            }
        },
        error: function (xhr) {
            let errorMsg = "Error al registrar la venta";
            try {
                const response = JSON.parse(xhr.responseText);
                errorMsg = response.message || errorMsg;
            } catch (e) {
                errorMsg = "Error de conexión al registrar la venta";
            }
            mostrarNotificacion(errorMsg, "danger");
        },
        complete: function() {
            submitBtn.prop("disabled", false).html(originalText);
        }
    });
}

function cargarDetallesVenta(ventaId) {
    $.get(`/gestion/ventas/api/${ventaId}`, function (res) {
        if (res.success) {
            const venta = res.data;
            let html = `
                <div class="detalles-venta">
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <strong>ID Venta:</strong> ${venta.id}
                        </div>
                        <div class="col-md-6">
                            <strong>Fecha y Hora:</strong> ${new Date(venta.fecha).toLocaleString()}
                        </div>
                    </div>

                    <div class="row mb-4">
                        <div class="col-md-12">
                            <strong>Cliente:</strong> ${venta.cliente.nombreCompleto}
                        </div>
                    </div>

                    <h6 class="fw-bold mb-3">Productos Vendidos:</h6>
                    <div class="list-group mb-4">
            `;

            venta.detalleVentas.forEach(detalle => {
                html += `
                    <div class="list-group-item">
                        <div class="row align-items-center">
                            <div class="col-md-1">
                                <img src="../../../../../productos/${detalle.producto.imagen}"
                                     alt="${detalle.producto.nombre}"
                                     class="producto-img-thumbnail"
                                     onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNTAiIGhlaWdodD0iNTAiIHZpZXdCb3g9IjAgMCA1MCA1MCIgZmlsbD0ibm9uZSIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj4KPHJlY3Qgd2lkdGg9IjUwIiBoZWlnaHQ9IjUwIiBmaWxsPSIjRjNGNEY2Ii8+CjxwYXRoIGQ9Ik0yNSAzMEMyOC44NjYgMzAgMzIgMjYuODY2IDMyIDIzQzMyIDE5LjEzNCAyOC44NjYgMTYgMjUgMTZDMjEuMTM0IDE2IDE4IDE5LjEzNCAxOCAyM0MxOCAyNi44NjYgMjEuMTM0IDMwIDI1IDMwWiIgZmlsbD0iIzlDOEU5RiIvPgo8cGF0aCBkPSJNMTguNzUgMzRDMTcuNTAzIDM0IDE2LjM3NSAzNC42MDMgMTUuNjU2IDM1LjU5M0MxNi4zNzUgMzYuNTgzIDE3LjUwMyAzNy4xODggMTguNzUgMzcuMTg4SDMxLjI1QzMyLjQ5NyAzNy4xODggMzMuNjI1IDM2LjU4MyAzNC4zNDQgMzUuNTkzQzMzLjYyNSAzNC42MDMgMzIuNDk3IDM0IDMxLjI1IDM0SDE4Ljc1WiIgZmlsbD0iIzlDOEU5RiIvPgo8L3N2Zz4K'">
                            </div>
                            <div class="col-md-5">
                                <strong>${detalle.producto.nombre}</strong>
                                <div class="small text-muted">${detalle.producto.descripcion}</div>
                            </div>
                            <div class="col-md-2 text-center">
                                <span class="fw-semibold">${detalle.cantidad} und</span>
                            </div>
                            <div class="col-md-2 text-end">
                                S/ ${detalle.precioUnitario.toFixed(2)}
                            </div>
                            <div class="col-md-2 text-end">
                                <strong>S/ ${detalle.subtotal.toFixed(2)}</strong>
                            </div>
                        </div>
                    </div>
                `;
            });

            const subtotal = venta.total / 1.18;
            const igv = venta.total - subtotal;

            html += `
                    </div>
                    <div class="row">
                        <div class="col-md-8"></div>
                        <div class="col-md-4">
                            <div class="venta-summary">
                                <div class="d-flex justify-content-between mb-2">
                                    <span class="fw-semibold">Subtotal:</span>
                                    <span>S/ ${subtotal.toFixed(2)}</span>
                                </div>
                                <div class="d-flex justify-content-between mb-2">
                                    <span class="fw-semibold">IGV (18%):</span>
                                    <span>S/ ${igv.toFixed(2)}</span>
                                </div>
                                <div class="d-flex justify-content-between total-venta">
                                    <strong>Total:</strong>
                                    <strong>S/ ${venta.total.toFixed(2)}</strong>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            `;

            $("#detallesVentaContent").html(html);
            detallesVentaModal.show();
        } else {
            mostrarNotificacion("Error al cargar los detalles de la venta", "danger");
        }
    }).fail(function() {
        mostrarNotificacion("Error al cargar los detalles de la venta", "danger");
    });
}

function eliminarVenta(ventaId) {
    Swal.fire({
        title: "¿Eliminar venta?",
        text: "Esta acción restaurará el stock de los productos y no se puede deshacer.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar",
        confirmButtonColor: "#d33"
    }).then((result) => {
        if (result.isConfirmed) {
            $.post(`/gestion/ventas/api/eliminar/${ventaId}`, function (res) {
                if (res.success) {
                    mostrarNotificacion(res.message, "success");
                    tablaVentas.ajax.reload();
                    cargarProductosDisponibles(); // Actualizar stock disponible
                } else {
                    mostrarNotificacion(res.message, "danger");
                }
            });
        }
    });
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