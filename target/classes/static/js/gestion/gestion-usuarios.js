/**
 * Script para la gestión de usuarios con Bootstrap 5
 * Archivo: src/main/resources/static/js/gestion-usuarios.js
 */

$(document).ready(function() {
    // Variables globales
    let dataTable;
    let isEditing = false;
    let usuarioModal;

    // Configuración inicial
    const API_BASE = '/gestion/usuarios/api';
    const ENDPOINTS = {
        list: `${API_BASE}/listar`,
        save: `${API_BASE}/guardar`,
        get: (id) => `${API_BASE}/${id}`,
        delete: (id) => `${API_BASE}/eliminar/${id}`,
        profiles: `${API_BASE}/perfiles`,
        toggleStatus: (id) => `${API_BASE}/cambiar-estado/${id}`,
    };

    // Inicializar DataTable
    initializeDataTable();

    // Inicializar Modal de Bootstrap
    usuarioModal = new bootstrap.Modal(document.getElementById('usuarioModal'));

    // Cargar perfiles para el select
    loadProfiles();

    // Event Listeners
    setupEventListeners();

    /**
     * Inicializa DataTable con configuración completa
     */
    function initializeDataTable() {
        dataTable = $('#tablaUsuarios').DataTable({
            responsive: true,
            processing: true,
            ajax: {
                url: ENDPOINTS.list,
                dataSrc: 'data'
            },
            columns: [
                { data: 'id' },
                { data: 'nombre' },
                { data: 'usuario' },
                { data: 'perfil.nombre' },
                { data: 'correo' },
                {
                    data: 'estado',
                    render: function(data, type, row) {
                        return data === 1
                            ? '<span class="badge text-bg-success">Activo</span>'
                            : '<span class="badge text-bg-secondary">Inactivo</span>';
                    }
                },
                {
                    data: null,
                    orderable: false,
                    searchable: false,
                    render: function(data, type, row) {
                        return createActionButtons(row);
                    }
                }
            ],
            columnDefs: [
                { responsivePriority: 1, targets: 1 },
                { responsivePriority: 2, targets: 6 },
            ],
            language: {
                url: "//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json",
            },
            pageLength: 10
        });
    }

    /**
     * Crea los botones de acción para cada fila de la tabla
     */
    function createActionButtons(row) {
        return `
            <div class="d-flex gap-1 justify-content-center">
                <button data-id="${row.id}" class="action-btn action-btn-edit action-edit" title="Editar">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-pencil-square" viewBox="0 0 16 16">
                                    <path d="M15.502 1.94a.5.5 0 0 1 0 .706L14.459 3.69l-2-2L13.502.646a.5.5 0 0 1 .707 0l1.293 1.293zm-1.75 2.456-2-2L4.939 9.21a.5.5 0 0 0-.121.196l-.805 2.414a.25.25 0 0 0 .316.316l2.414-.805a.5.5 0 0 0 .196-.12l6.813-6.814z"/>
                                    <path fill-rule="evenodd" d="M1 13.5A1.5 1.5 0 0 0 2.5 15h11a1.5 1.5 0 0 0 1.5-1.5v-6a.5.5 0 0 0-1 0v6a.5.5 0 0 1-.5.5h-11a.5.5 0 0 1-.5-.5v-11a.5.5 0 0 1 .5-.5H9a.5.5 0 0 0 0-1H2.5A1.5 1.5 0 0 0 1 2.5z"/>
                    </svg>
                </button>
                <div class="form-check form-switch">
                    <input class="form-check-input action-status" type="checkbox" role="switch"
                           data-id="${row.id}" ${row.estado === 1 ? 'checked' : ''}
                           title="${row.estado === 1 ? 'Desactivar' : 'Activar'}">
                </div>
                <button data-id="${row.id}" class="action-btn action-btn-delete action-delete" title="Eliminar">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash3-fill" viewBox="0 0 16 16">
                        <path d="M11 1.5v1h3.5a.5.5 0 0 1 0 1h-.538l-.853 10.66A2 2 0 0 1 11.115 16h-6.23a2 2 0 0 1-1.994-1.84L2.038 3.5H1.5a.5.5 0 0 1 0-1H5v-1A1.5 1.5 0 0 1 6.5 0h3A1.5 1.5 0 0 1 11 1.5m-5 0v1h4v-1a.5.5 0 0 0-.5-.5h-3a.5.5 0 0 0-.5.5M4.5 5.029l.5 8.5a.5.5 0 1 0 .998-.06l-.5-8.5a.5.5 0 1 0-.998.06m6.53-.528a.5.5 0 0 0-.528.47l-.5 8.5a.5.5 0 0 0 .998.058l.5-8.5a.5.5 0 0 0-.47-.528M8 4.5a.5.5 0 0 0-.5.5v8.5a.5.5 0 0 0 1 0V5a.5.5 0 0 0-.5-.5"/>
                    </svg>
                </button>
            </div>
        `;
    }

    /**
     * Configura todos los event listeners
     */
    function setupEventListeners() {
        // Botón nuevo registro
        $('#btnNuevoRegistro').on('click', openModalForNew);

        // Submit form
        $('#formUsuario').on('submit', function(e) {
            e.preventDefault();
            saveUsuario();
        });

        // Eventos de la tabla (delegados)
        $('#tablaUsuarios tbody').on('click', '.action-edit', handleEdit);
        $('#tablaUsuarios tbody').on('change', '.action-status', handleToggleStatus);
        $('#tablaUsuarios tbody').on('click', '.action-delete', handleDelete);
    }

    /**
     * Carga la lista de usuarios desde el backend y redibuja la tabla
     */
    function loadUsuarios() {
        dataTable.ajax.reload();
    }

    /**
     * Carga los perfiles en el select del modal
     */
    function loadProfiles() {
        fetch(ENDPOINTS.profiles)
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const select = $('#id_perfil');
                    select.empty().append('<option value="">Seleccione un perfil...</option>');
                    data.data.forEach(profile => {
                        select.append(`<option value="${profile.id}">${profile.nombre}</option>`);
                    });
                } else {
                    showNotification('Error al cargar perfiles', 'error');
                }
            }).catch(error => {
            console.error('Error cargando perfiles:', error);
            showNotification('Error al cargar la lista de perfiles', 'error');
        });
    }

    /**
     * Guarda un usuario (crear o actualizar)
     */
    function saveUsuario() {
        clearFieldErrors();

        const formData = {
            id: $('#id').val() || null,
            nombre: $('#nombre').val().trim(),
            usuario: $('#usuario').val().trim(),
            clave: $('#clave').val(),
            correo: $('#correo').val().trim(),
            perfil: {
                id: $('#id_perfil').val() ? parseInt($('#id_perfil').val()) : null
            }
        };

        // Validación básica del lado cliente
        if (!validateForm(formData)) {
            return;
        }

        // Si es edición y la clave está vacía, eliminar la propiedad clave del objeto
        if (isEditing && (!formData.clave || formData.clave.trim() === "")) {
            delete formData.clave;
        }

        showLoading(true);

        fetch(ENDPOINTS.save, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(formData)
        })
            .then(response => {
                if (!response.ok) {
                    // Si la respuesta no es OK, intentar leer el mensaje de error
                    return response.json().then(errorData => {
                        throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
                    }).catch(() => {
                        throw new Error(`HTTP error! status: ${response.status}`);
                    });
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    hideModal();
                    showNotification(data.message, 'success');
                    loadUsuarios();
                } else {
                    if (data.errors) {
                        Object.keys(data.errors).forEach(field => {
                            showFieldError(field, data.errors[field]);
                        });
                    }
                    if (data.message) {
                        showNotification(data.message, 'error');
                    }
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Error al guardar usuario: ' + error.message, 'error');
            })
            .finally(() => {
                showLoading(false);
            });
    }

    /**
     * Maneja la edición de un usuario
     */
    function handleEdit(e) {
        e.preventDefault();
        const id = $(this).data('id');

        showLoading(true);

        fetch(ENDPOINTS.get(id))
            .then(response => {
                if (!response.ok) {
                    throw new Error('Usuario no encontrado');
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    openModalForEdit(data.data);
                } else {
                    showNotification('Error al cargar usuario: ' + data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Error al cargar los datos del usuario', 'error');
            })
            .finally(() => {
                showLoading(false);
            });
    }

    /**
     * Maneja el cambio de estado de un usuario
     */
    function handleToggleStatus(e) {
        e.preventDefault();
        const id = $(this).data('id');

        showLoading(true);

        fetch(ENDPOINTS.toggleStatus(id), {
            method: 'POST'
        })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    showNotification(data.message, 'success');
                    loadUsuarios();
                } else {
                    showNotification('Error: ' + data.message, 'error');
                }
            })
            .catch(error => {
                console.error('Error:', error);
                showNotification('Error de conexión al cambiar estado', 'error');
            })
            .finally(() => {
                showLoading(false);
            });
    }

    /**
     * Maneja la eliminación de un usuario
     */
    function handleDelete(e) {
        e.preventDefault();

        const id = $(this).data('id');

        Swal.fire({
            title: '¿Estás seguro?',
            text: "¡No podrás revertir esta acción!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#dc3545',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Sí, ¡eliminar!',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                showLoading(true);

                fetch(ENDPOINTS.delete(id), {
                    method: 'DELETE'
                })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            showNotification(data.message, 'success');
                            loadUsuarios();
                        } else {
                            showNotification('Error: ' + data.message, 'error');
                        }
                    })
                    .catch(error => {
                        console.error('Error:', error);
                        showNotification('Error de conexión al eliminar usuario', 'error');
                    })
                    .finally(() => {
                        showLoading(false);
                    });
            }
        });
    }

    /**
     * Abre el modal para crear nuevo usuario
     */
    function openModalForNew() {
        isEditing = false;
        clearForm();
        $('#modalTitle').text('Agregar Usuario');
        $('#clave').prop('required', true).attr('placeholder', '');

        // LIMPIAR EXPLÍCITAMENTE EL ID - ESTO ES CLAVE
        $('#id').val('');

        showModal();
    }

    /**
     * Abre el modal para editar usuario
     */
    function openModalForEdit(usuario) {
        isEditing = true;
        clearForm();
        $('#modalTitle').text('Editar Usuario');

        $('#id').val(usuario.id);
        $('#nombre').val(usuario.nombre);
        $('#usuario').val(usuario.usuario);
        $('#correo').val(usuario.correo);
        $('#id_perfil').val(usuario.perfil ? usuario.perfil.id : '');

        // Configurar campo de clave para edición
        $('#clave')
            .val('')
            .attr('placeholder', 'Dejar en blanco para conservar la contraseña actual')
            .attr('title', 'Dejar vacío para mantener la contraseña actual');

        showModal();
    }

    /**
     * Muestra el modal
     */
    function showModal() {
        usuarioModal.show();
    }

    /**
     * Oculta el modal
     */
    function hideModal() {
        usuarioModal.hide();
        clearForm();
    }

    /**
     * Limpia el formulario y resetea el estado
     */
    function clearForm() {
        $('#formUsuario')[0].reset();
        $('#formUsuario .form-control').removeClass('is-invalid');
        $('#formUsuario .form-select').removeClass('is-invalid');
        $('.invalid-feedback').text('');

        // LIMPIAR EXPLÍCITAMENTE EL CAMPO ID
        $('#id').val('');

        isEditing = false;
    }

    /**
     * Valida el formulario del lado cliente
     */
    function validateForm(formData) {
        let hasErrors = false;
        clearFieldErrors();

        // Validación de nombre
        if (!formData.nombre) {
            showFieldError('nombre', 'El nombre es obligatorio');
            hasErrors = true;
        } else if (formData.nombre.length < 2) {
            showFieldError('nombre', 'El nombre debe tener al menos 2 caracteres');
            hasErrors = true;
        }

        // Validación de usuario
        if (!formData.usuario) {
            showFieldError('usuario', 'El usuario es obligatorio');
            hasErrors = true;
        } else if (formData.usuario.length < 3) {
            showFieldError('usuario', 'El usuario debe tener al menos 3 caracteres');
            hasErrors = true;
        }

        // Validación de perfil
        if (!formData.perfil || !formData.perfil.id) {
            showFieldError('id_perfil', 'Debe seleccionar un perfil');
            hasErrors = true;
        }

        // Validación de clave - DIFERENCIAR ENTRE CREAR Y EDITAR
        if (!isEditing) {
            // Crear usuario → obligatorio
            if (!formData.clave) {
                showFieldError('clave', 'La contraseña es obligatoria para nuevos usuarios');
                hasErrors = true;
            } else if (formData.clave.length < 6) {
                showFieldError('clave', 'La contraseña debe tener al menos 6 caracteres');
                hasErrors = true;
            }
        } else {
            // Editar usuario → opcional, pero si se ingresa debe cumplir con longitud mínima
            if (formData.clave && formData.clave.length < 6) {
                showFieldError('clave', 'La contraseña debe tener al menos 6 caracteres');
                hasErrors = true;
            }
        }

        // Validación de correo
        if (!formData.correo) {
            showFieldError('correo', 'El correo es obligatorio');
            hasErrors = true;
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.correo)) {
            showFieldError('correo', 'El correo no es válido');
            hasErrors = true;
        }

        return !hasErrors;
    }
    /**
     * Muestra error en un campo específico
     */
    function showFieldError(fieldName, message) {
        const field = $(`#${fieldName}`);
        const errorDiv = $(`#${fieldName}-error`);

        field.addClass('is-invalid');
        errorDiv.text(message);
    }

    /**
     * Limpia todos los errores de campo
     */
    function clearFieldErrors() {
        $('.invalid-feedback').text('');
        $('#formUsuario .form-control').removeClass('is-invalid');
        $('#formUsuario .form-select').removeClass('is-invalid');
    }

    /**
     * Muestra notificaciones toast
     */
    function showNotification(message, type = 'success') {
        const toastClass = type === 'success' ? 'text-bg-success' : 'text-bg-danger';

        const notification = $(`
            <div class="toast align-items-center ${toastClass} border-0" role="alert" aria-live="assertive" aria-atomic="true">
                <div class="d-flex">
                    <div class="toast-body">
                        ${message}
                    </div>
                    <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
                </div>
            </div>
        `);

        $('#notification-container').append(notification);

        const toast = new bootstrap.Toast(notification, {
            delay: 5000
        });
        toast.show();
    }

    /**
     * Muestra/oculta indicador de carga
     */
    function showLoading(show) {
        const overlayId = 'loading-overlay';
        const $overlay = $(`#${overlayId}`);

        if (show) {
            if ($overlay.length === 0) {
                const spinner = $('<div>', { class: 'spinner-border text-primary', role: 'status' })
                    .append($('<span>', { class: 'visually-hidden' }).text('Loading...'));
                const newOverlay = $('<div>', { id: overlayId, class: 'loading-overlay' })
                    .append(spinner);
                $('body').append(newOverlay);
            }
        } else {
            $overlay.remove();
        }
    }
});