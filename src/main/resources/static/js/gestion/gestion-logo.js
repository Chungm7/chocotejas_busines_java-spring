let tablaLogos;
let modalLogo;
let formLogo;

$(document).ready(function() {
    inicializarComponentes();
    cargarTablaLogos();
});

function inicializarComponentes() {
    modalLogo = new bootstrap.Modal(document.getElementById('logoModal'));
    formLogo = document.getElementById('formLogo');

    // Evento para nuevo logo
    $('#btnNuevoLogo').click(function() {
        abrirModalLogo();
    });

    // Evento para vista previa de imagen
    $('#rutaFile').change(function(e) {
        mostrarVistaPrevia(e.target);
    });

    // Evento submit del formulario
    $(formLogo).submit(function(e) {
        e.preventDefault();
        guardarLogo();
    });
}

function cargarTablaLogos() {
    tablaLogos = $('#tablaLogos').DataTable({
        responsive: true,
        ajax: {
            url: '/gestion/logos/api/listar',
            dataSrc: 'data'
        },
        columns: [
            { data: 'id' },
            {
                data: 'ruta',
                render: function(data) {
                    return data ?
                        `<img src="../../../../../logos/${data}" class="logo-img-thumbnail" alt="Logo">` :
                        '<span class="text-muted">Sin imagen</span>';
                }
            },
            { data: 'nombre' },
            {
                data: 'descripcion',
                render: function(data) {
                    return data || '<span class="text-muted">Sin descripción</span>';
                }
            },
            {
                data: 'estado',
                render: function(data) {
                    let badgeClass = data === 1 ? 'bg-success' : 'bg-secondary';
                    let badgeText = data === 1 ? 'Activo' : 'Inactivo';
                    return `<span class="badge ${badgeClass}">${badgeText}</span>`;
                }
            },
            {
                data: 'activo',
                render: function(data, type, row) {
                    const isActive = data === true || data === 1;
                    return `
                        <button class="btn btn-destacado ${isActive ? 'active' : ''}" 
                                onclick="cambiarLogoActivo(${row.id})"
                                title="${isActive ? 'Logo activo' : 'Activar logo'}">
                            <i class="bi ${isActive ? 'bi-check-circle-fill text-success' : 'bi-circle'}"></i>
                        </button>
                    `;
                }
            },
            {
                data: null,
                render: function(data) {
                    return `
                        <div class="d-flex justify-content-center">
                            <button class="action-btn action-btn-edit" onclick="editarLogo(${data.id})" title="Editar">
                                <i class="bi bi-pencil"></i>
                            </button>
                            <button class="action-btn action-btn-delete" onclick="eliminarLogo(${data.id})" title="Eliminar">
                                <i class="bi bi-trash"></i>
                            </button>
                            <button class="action-btn ${data.estado === 1 ? 'action-btn-permissions' : 'bg-warning'}" 
                                    onclick="cambiarEstadoLogo(${data.id})" 
                                    title="${data.estado === 1 ? 'Desactivar' : 'Activar'}">
                                <i class="bi ${data.estado === 1 ? 'bi-eye' : 'bi-eye-slash'}"></i>
                            </button>
                        </div>
                    `;
                }
            }
        ],
        language: {
            url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
        }
    });
}

function abrirModalLogo(logo = null) {
    $('#logoModalLabel').text(logo ? 'Editar Logo' : 'Nuevo Logo');
    $('#idLogo').val(logo ? logo.id : '');
    $('#nombre').val(logo ? logo.nombre : '');
    $('#descripcion').val(logo ? logo.descripcion : '');

    // Limpiar vista previa
    $('#imagenPreviewContainer').hide();
    $('#rutaFile').val('');

    // Cambiar requerimiento de imagen solo para nuevo
    $('#rutaFile').prop('required', !logo);

    modalLogo.show();
}

function mostrarVistaPrevia(input) {
    const previewContainer = $('#imagenPreviewContainer');
    const preview = $('#imagenPreview')[0];

    if (input.files && input.files[0]) {
        const reader = new FileReader();

        reader.onload = function(e) {
            preview.src = e.target.result;
            previewContainer.show();
        }

        reader.readAsDataURL(input.files[0]);
    } else {
        previewContainer.hide();
    }
}

function guardarLogo() {
    const formData = new FormData(formLogo);
    const idLogo = $('#idLogo').val();
    const url = idLogo ? `/gestion/logos/api/actualizar/${idLogo}` : '/gestion/logos/api/guardar';

    $.ajax({
        url: url,
        type: 'POST',
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if (response.success) {
                mostrarNotificacion('success', response.message);
                modalLogo.hide();
                tablaLogos.ajax.reload();
            } else {
                mostrarNotificacion('error', response.message);
            }
        },
        error: function(xhr) {
            const errorMsg = xhr.responseJSON?.message || 'Error al guardar el logo';
            mostrarNotificacion('error', errorMsg);
        }
    });
}

function editarLogo(id) {
    $.ajax({
        url: `/gestion/logos/api/${id}`,
        type: 'GET',
        success: function(response) {
            if (response.success) {
                abrirModalLogo(response.data);
            } else {
                mostrarNotificacion('error', 'Error al cargar el logo');
            }
        },
        error: function() {
            mostrarNotificacion('error', 'Error al cargar el logo');
        }
    });
}

function eliminarLogo(id) {
    Swal.fire({
        title: '¿Estás seguro?',
        text: "¡No podrás revertir esto!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#d33',
        cancelButtonColor: '#3085d6',
        confirmButtonText: 'Sí, eliminar!',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            $.ajax({
                url: `/gestion/logos/api/eliminar/${id}`,
                type: 'POST',
                success: function(response) {
                    if (response.success) {
                        mostrarNotificacion('success', response.message);
                        tablaLogos.ajax.reload();
                    } else {
                        mostrarNotificacion('error', response.message);
                    }
                },
                error: function(xhr) {
                    const errorMsg = xhr.responseJSON?.message || 'Error al eliminar el logo';
                    mostrarNotificacion('error', errorMsg);
                }
            });
        }
    });
}

function cambiarEstadoLogo(id) {
    $.ajax({
        url: `/gestion/logos/api/cambiar-estado/${id}`,
        type: 'POST',
        success: function(response) {
            if (response.success) {
                mostrarNotificacion('success', response.message);
                tablaLogos.ajax.reload();
            } else {
                mostrarNotificacion('error', response.message);
            }
        },
        error: function(xhr) {
            const errorMsg = xhr.responseJSON?.message || 'Error al cambiar estado';
            mostrarNotificacion('error', errorMsg);
        }
    });
}

function cambiarLogoActivo(id) {
    $.ajax({
        url: `/gestion/logos/api/activar-logo/${id}`,
        type: 'POST',
        success: function(response) {
            if (response.success) {
                mostrarNotificacion('success', response.message);
                tablaLogos.ajax.reload();
            } else {
                mostrarNotificacion('error', response.message);
            }
        },
        error: function(xhr) {
            const errorMsg = xhr.responseJSON?.message || 'Error al activar el logo';
            mostrarNotificacion('error', errorMsg);
        }
    });
}

function mostrarNotificacion(tipo, mensaje) {
    const alertClass = tipo === 'success' ? 'alert-success' : 'alert-danger';
    const notification = $(`
        <div class="alert ${alertClass} alert-dismissible fade show" role="alert">
            ${mensaje}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `);

    $('#notification-container').append(notification);

    setTimeout(() => {
        notification.alert('close');
    }, 5000);
}