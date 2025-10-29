let tablaVentas;
let ventaModal;
let detallesVentaModal;
let clienteModal;
let formVenta;
let formCliente;
let productosDisponibles = [];
const API_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX2lkIjozNjAsImV4cCI6MTc2MDM4Mzk3OX0.s8pdevKFpdbjfpK8gz7bBmgh18GgEvIt8b_VUsjksKw";

$(document).ready(function () {
    ventaModal = new bootstrap.Modal(document.getElementById("ventaModal"));
    detallesVentaModal = new bootstrap.Modal(document.getElementById("detallesVentaModal"));
    clienteModal = new bootstrap.Modal(document.getElementById("clienteModal"));
    formVenta = $("#formVenta");
    formCliente = $("#formCliente");

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
                    if (cliente) {
                        return `${cliente.nombreCompleto} (${cliente.tipoDocumento}: ${cliente.numeroDocumento})`;
                    }
                    return '-';
                }
            },
            {
                data: "total",
                render: function (total) {
                    return `S/ ${total.toFixed(2)}`;
                }
            },
            {
                data: "detalleVentas",
                render: function (detalles) {
                    if (!detalles) return '-';
                    const productos = detalles.map(d =>
                        `${d.producto.nombre} (${d.cantidad} und)`
                    );
                    return productos.join(', ');
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

    // Buscar cliente
    $("#btnBuscarCliente").click(function () {
        buscarCliente();
    });

    // Nuevo cliente
    $("#btnNuevoCliente").click(function () {
        resetFormCliente();
        clienteModal.show();
    });

    // Consultar API al perder foco del número de documento del cliente
    $("#clienteNumeroDocumento").blur(function() {
        const tipo = $("#clienteTipoDocumento").val();
        const numero = $(this).val().trim();

        if (!tipo || !numero) return;

        const esDNIValido = tipo === "DNI" && numero.length === 8 && /^\d+$/.test(numero);
        const esRUCValido = tipo === "RUC" && numero.length === 11 && /^\d+$/.test(numero);

        if (!esDNIValido && !esRUCValido) {
            mostrarNotificacion("Número de documento inválido", "warning");
            return;
        }

        consultarApiCliente(tipo, numero);
    });

    // Guardar cliente
    formCliente.submit(async function (e) {
        e.preventDefault();
        await guardarCliente();
    });

    // Resto de eventos existentes...
    $("#btnAgregarProducto").click(function () {
        agregarFilaProducto();
    });

    $("#productosContainer").on("click", ".btn-eliminar-producto", function () {
        $(this).closest(".producto-row").remove();
        calcularTotales();
    });

    $("#productosContainer").on("change", ".producto-select", function () {
        const productoId = $(this).val();
        const fila = $(this).closest(".producto-row");
        const precioInput = fila.find(".precio-input");
        const stockInfo = fila.find(".stock-info");
        const cantidadInput = fila.find(".cantidad-input");

        if (productoId) {
            const producto = productosDisponibles.find(p => p.id == productoId);
            if (producto) {
                if (!producto.precio || producto.precio <= 0) {
                    mostrarNotificacion(`El producto "${producto.nombre}" no tiene precio asignado`, "warning");
                    $(this).val("");
                    precioInput.val("");
                    stockInfo.html("");
                    cantidadInput.prop("disabled", true);
                    return;
                }

                precioInput.val(`S/ ${producto.precio.toFixed(2)}`);

                let stockClass = "stock-disponible";
                if (producto.stock === 0) {
                    stockClass = "stock-agotado";
                } else if (producto.stock <= 5) {
                    stockClass = "stock-bajo";
                }

                stockInfo.html(`<span class="${stockClass}">Stock: ${producto.stock} unidades</span>`);
                cantidadInput.attr("max", producto.stock);

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

    $("#productosContainer").on("input", ".cantidad-input", function () {
        const fila = $(this).closest(".producto-row");
        calcularSubtotalFila(fila);
        calcularTotales();
    });

    formVenta.submit(function (e) {
        e.preventDefault();
        registrarVenta();
    });

    $("#tablaVentas").on("click", ".btn-detalles", function () {
        const ventaId = $(this).data("id");
        cargarDetallesVenta(ventaId);
    });

    $("#tablaVentas").on("click", ".btn-eliminar", function () {
        const ventaId = $(this).data("id");
        eliminarVenta(ventaId);
    });
});

async function guardarCliente() {
    const tipoDocumento = $("#clienteTipoDocumento").val();
    const numeroDocumento = $("#clienteNumeroDocumento").val().trim();
    const direccion = $("#clienteDireccion").val().trim();
    const nombreCompleto = $("#clienteNombreCompleto").val();

    if (!tipoDocumento || !numeroDocumento || !direccion || !nombreCompleto) {
        mostrarNotificacion("Complete todos los campos obligatorios", "danger");
        return;
    }

    const data = {
        tipoDocumento: tipoDocumento,
        numeroDocumento: numeroDocumento,
        nombreCompleto: nombreCompleto,
        direccion: direccion
    };

    try {
        const response = await $.ajax({
            url: "/gestion/clientes/api/guardar",
            type: "POST",
            contentType: "application/x-www-form-urlencoded",
            data: data
        });

        if (response.success) {
            mostrarNotificacion("Cliente registrado con éxito", "success");
            clienteModal.hide();
            // Actualizar el formulario de venta con el nuevo cliente
            $("#idCliente").val(response.data.id);
            $("#numeroDocumentoCliente").val(numeroDocumento);
            mostrarInfoCliente(response.data);
        } else {
            mostrarNotificacion(response.message, "danger");
        }
    } catch (xhr) {
        let errorMsg = "Error al guardar el cliente";
        try {
            const response = JSON.parse(xhr.responseText);
            errorMsg = response.message || errorMsg;
        } catch (e) {}
        mostrarNotificacion(errorMsg, "danger");
    }
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
    $("#productosContainer").empty();
    $("#subtotalVenta").text("S/ 0.00");
    $("#igvVenta").text("S/ 0.00");
    $("#totalVenta").text("S/ 0.00");
    agregarFilaProducto(); // Agregar una fila por defecto
}

function resetFormCliente() {
    formCliente[0].reset();
    $("#clienteId").val("");
    $("#clienteNombreCompleto").val("");
    $("#clienteInfoApi").hide().empty();
}
function mostrarInfoCliente(cliente) {
    const infoHtml = `
        <div class="d-flex justify-content-between align-items-start">
            <div>
                <strong class="text-dark">Cliente encontrado:</strong><br>
                <strong>Nombre:</strong> ${cliente.nombreCompleto}<br>
                <strong>Documento:</strong> ${cliente.tipoDocumento} - ${cliente.numeroDocumento}<br>
                <strong>Dirección:</strong> ${cliente.direccion}
            </div>
            <button type="button" class="btn-close" onclick="$('#infoCliente').hide()"></button>
        </div>
    `;

    $("#infoCliente").html(infoHtml).show();
}
function buscarCliente() {
    const numeroDocumento = $("#numeroDocumentoCliente").val().trim();

    if (!numeroDocumento) {
        mostrarNotificacion("Ingrese el número de documento", "warning");
        return;
    }

    $("#infoCliente").html('<small><i class="spinner-border spinner-border-sm"></i> Buscando cliente...</small>').show();

    $.ajax({
        url: "/gestion/ventas/api/buscar-cliente",
        type: "POST",
        data: {
            numeroDocumento: numeroDocumento
        },
        success: function (res) {
            if (res.success) {
                mostrarInfoCliente(res.data);
                $("#idCliente").val(res.data.id);
            } else {
                $("#infoCliente").hide();
                mostrarNotificacion(res.message, "warning");
                // Si no existe, permitir registrar nuevo cliente
                $("#btnNuevoCliente").click();
            }
        },
        error: function () {
            $("#infoCliente").hide();
            mostrarNotificacion("Error al buscar cliente", "danger");
        }
    });
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
    const clienteId = $("#idCliente").val();

    if (!clienteId) {
        mostrarNotificacion("Debe seleccionar un cliente para la venta", "danger");
        return;
    }

    const detalles = [];
    let hayErrores = false;
    let productosSinStock = [];
    let productosSinPrecio = [];

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

        if (!producto) {
            hayErrores = true;
            mostrarNotificacion("Producto no encontrado", "danger");
            return false;
        }

        if (!producto.precio || producto.precio <= 0) {
            hayErrores = true;
            productosSinPrecio.push(producto.nombre);
            return false;
        }

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
        clienteId: clienteId,
        detalles: detalles
    };

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
                cargarProductosDisponibles();
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

function consultarApiCliente(tipo, numero) {
    $("#clienteInfoApi").html('<small><i class="spinner-border spinner-border-sm"></i> Consultando API...</small>').show();

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
                let infoHtml = "";

                if (tipo === "DNI") {
                    const datos = res.datos;
                    nombreCompleto = `${datos.nombres} ${datos.ape_paterno} ${datos.ape_materno}`.trim();
                    infoHtml = `
                        <strong>Información obtenida del DNI:</strong><br>
                        <strong>Nombres:</strong> ${datos.nombres}<br>
                        <strong>Apellido Paterno:</strong> ${datos.ape_paterno}<br>
                        <strong>Apellido Materno:</strong> ${datos.ape_materno}<br>
                        <strong>Nombre Completo:</strong> <span class="fw-bold text-success">${nombreCompleto}</span>
                    `;
                } else {
                    const datos = res.datos;
                    nombreCompleto = datos.razon_social;
                    infoHtml = `
                        <strong>Información obtenida del RUC:</strong><br>
                        <strong>Razón Social:</strong> <span class="fw-bold text-success">${datos.razon_social}</span><br>
                        <strong>Estado:</strong> ${datos.estado}
                    `;
                }

                $("#clienteNombreCompleto").val(nombreCompleto);
                $("#clienteInfoApi").html(`<small>${infoHtml}</small>`).show();

                if (res.datos.domiciliado && res.datos.domiciliado.direccion) {
                    const direccionApi = res.datos.domiciliado.direccion;
                    $("#clienteDireccion").val(direccionApi);
                }
            } else {
                $("#clienteInfoApi").hide();
                mostrarNotificacion("Documento no encontrado en la API", "warning");
            }
        },
        error: function(xhr) {
            $("#clienteInfoApi").hide();
            let errorMsg = "Error al consultar la API";
            if (xhr.status === 404) {
                errorMsg = "Documento no encontrado en la API";
            }
            mostrarNotificacion(errorMsg, "danger");
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
                            <strong>Cliente:</strong> ${venta.cliente.nombreCompleto}<br>
                            <strong>Documento:</strong> ${venta.cliente.tipoDocumento} - ${venta.cliente.numeroDocumento}<br>
                            <strong>Dirección:</strong> ${venta.cliente.direccion}
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