/* GastroTech - JavaScript para módulo de Usuarios */

let usuariosData = [];
let perfilesData = [];
let usuarioModal;

document.addEventListener('DOMContentLoaded', function() {
    usuarioModal = new bootstrap.Modal(document.getElementById('usuarioModal'));
    cargarPerfiles();
    cargarUsuarios();
    setupFilters();
});

// Cargar perfiles para el select
async function cargarPerfiles() {
    try {
        const response = await fetch('/perfil');
        if (response.ok) {
            perfilesData = await response.json();
            const selects = ['#usuarioPerfil', '#filterPerfil'];
            selects.forEach(selectId => {
                const select = document.querySelector(selectId);
                if (select) {
                    perfilesData.forEach(perfil => {
                        const option = document.createElement('option');
                        option.value = perfil.idPerfil;
                        option.textContent = perfil.nombrePerfil;
                        select.appendChild(option);
                    });
                }
            });
        }
    } catch (error) {
        console.error('Error al cargar perfiles:', error);
    }
}

// Cargar usuarios
async function cargarUsuarios() {
    try {
        const response = await fetch('/usuario');
        if (response.ok) {
            usuariosData = await response.json();
            renderUsuarios(usuariosData);
        } else {
            showNotification('Error al cargar usuarios', 'error');
        }
    } catch (error) {
        console.error('Error al cargar usuarios:', error);
        showNotification('Error de conexión', 'error');
    }
}

// Renderizar tabla de usuarios
function renderUsuarios(usuarios) {
    const tbody = document.getElementById('usuariosTableBody');
    if (!tbody) return;

    if (!usuarios || usuarios.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center py-4">No hay usuarios registrados</td></tr>';
        return;
    }

    tbody.innerHTML = usuarios.map(usuario => `
        <tr>
            <td>${usuario.idUsuario}</td>
            <td>
                <div class="product-cell">
                    <img src="${usuario.imagenUrl ? '/uploads/users/' + usuario.imagenUrl : 'https://via.placeholder.com/50'}" 
                         alt="${usuario.nombres}" 
                         onerror="this.src='https://via.placeholder.com/50'">
                    <div class="product-info">
                        <h6>${usuario.nombres} ${usuario.apellidos}</h6>
                    </div>
                </div>
            </td>
            <td>${usuario.DNI}</td>
            <td>${usuario.username}</td>
            <td>${usuario.nombrePerfil || 'N/A'}</td>
            <td>${formatearFecha(usuario.fechaRegistro)}</td>
            <td>
                <span class="status-badge ${usuario.estadoBD && usuario.estadoBD.toLowerCase() === 'activo' ? 'active' : 'inactive'}">
                    ${usuario.estadoBD}
                </span>
            </td>
            <td>
                <div class="action-btns">
                    <button class="action-btn view" onclick="verUsuario(${usuario.idUsuario})" title="Ver detalles">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="action-btn edit" onclick="editarUsuario(${usuario.idUsuario})" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="action-btn delete" onclick="eliminarUsuario(${usuario.idUsuario})" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Abrir modal para crear/editar
function openUsuarioModal(action, id = null) {
    const form = document.getElementById('usuarioForm');
    const title = document.getElementById('usuarioModalTitle');

    form.reset();
    form.classList.remove('was-validated');
    document.getElementById('imagenPreview').src = 'https://via.placeholder.com/120';

    if (action === 'create') {
        title.textContent = 'Nuevo Usuario';
        document.getElementById('usuarioId').value = '';
        document.getElementById('usuarioContrasena').required = true;
    } else if (action === 'edit' && id) {
        title.textContent = 'Editar Usuario';
        document.getElementById('usuarioContrasena').required = false;
        cargarDatosUsuario(id);
    }

    usuarioModal.show();
}

// Cargar datos del usuario para edición
async function cargarDatosUsuario(id) {
    try {
        const response = await fetch(`/usuario/${id}`);
        if (response.ok) {
            const usuario = await response.json();
            document.getElementById('usuarioId').value = usuario.idUsuario;
            document.getElementById('usuarioNombres').value = usuario.nombres;
            document.getElementById('usuarioApellidos').value = usuario.apellidos;
            document.getElementById('usuarioDNI').value = usuario.DNI;
            document.getElementById('usuarioUsername').value = usuario.username;
            document.getElementById('usuarioPerfil').value = usuario.idPerfil;
            document.getElementById('usuarioEstado').value = usuario.estadoBD;

            if (usuario.imagenUrl) {
                document.getElementById('imagenPreview').src = '/uploads/users/' + usuario.imagenUrl;
            }
        }
    } catch (error) {
        console.error('Error al cargar usuario:', error);
        showNotification('Error al cargar datos del usuario', 'error');
    }
}

// Editar usuario
function editarUsuario(id) {
    openUsuarioModal('edit', id);
}

// Ver usuario
function verUsuario(id) {
    const usuario = usuariosData.find(u => u.idUsuario === id);
    if (usuario) {
        alert(`Usuario: ${usuario.nombres} ${usuario.apellidos}\nDNI: ${usuario.DNI}\nUsername: ${usuario.username}\nPerfil: ${usuario.nombrePerfil}`);
    }
}

// Guardar usuario (crear o actualizar)
async function saveUsuario() {
    const form = document.getElementById('usuarioForm');

    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        showNotification('Por favor complete todos los campos requeridos', 'error');
        return;
    }

    const formData = new FormData();
    const id = document.getElementById('usuarioId').value;

    formData.append('nombresUsuario', document.getElementById('usuarioNombres').value);
    formData.append('apellidosUsuario', document.getElementById('usuarioApellidos').value);
    formData.append('DNI', document.getElementById('usuarioDNI').value);
    formData.append('username', document.getElementById('usuarioUsername').value);

    const contrasena = document.getElementById('usuarioContrasena').value;
    if (contrasena) {
        formData.append('contrasena', contrasena);
    }

    formData.append('idPerfil', document.getElementById('usuarioPerfil').value);
    formData.append('estadoBD', document.getElementById('usuarioEstado').value);

    const imagenInput = document.getElementById('usuarioImagen');
    if (imagenInput.files.length > 0) {
        formData.append('imagen', imagenInput.files[0]);
    }

    try {
        const url = id ? `/usuario/${id}` : '/usuario';
        const method = id ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            body: formData
        });

        if (response.ok) {
            showNotification(`Usuario ${id ? 'actualizado' : 'creado'} exitosamente`, 'success');
            usuarioModal.hide();
            cargarUsuarios();
        } else {
            const error = await response.text();
            showNotification(`Error: ${error}`, 'error');
        }
    } catch (error) {
        console.error('Error al guardar usuario:', error);
        showNotification('Error al guardar el usuario', 'error');
    }
}

// Eliminar usuario
async function eliminarUsuario(id) {
    if (!confirm('¿Está seguro de eliminar este usuario?')) return;

    try {
        const response = await fetch(`/usuario/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Usuario eliminado exitosamente', 'success');
            cargarUsuarios();
        } else {
            showNotification('Error al eliminar usuario', 'error');
        }
    } catch (error) {
        console.error('Error al eliminar usuario:', error);
        showNotification('Error al eliminar usuario', 'error');
    }
}

// Preview de imagen
function previewImageUsuario(input) {
    const preview = document.getElementById('imagenPreview');
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
        };
        reader.readAsDataURL(input.files[0]);
    }
}

// Configurar filtros
function setupFilters() {
    const searchInput = document.getElementById('searchUsuario');
    const filterPerfil = document.getElementById('filterPerfil');
    const filterEstado = document.getElementById('filterEstado');

    [searchInput, filterPerfil, filterEstado].forEach(element => {
        if (element) {
            element.addEventListener('change', aplicarFiltros);
            element.addEventListener('keyup', aplicarFiltros);
        }
    });
}

// Aplicar filtros
function aplicarFiltros() {
    const searchText = document.getElementById('searchUsuario').value.toLowerCase();
    const filterPerfil = document.getElementById('filterPerfil').value;
    const filterEstado = document.getElementById('filterEstado').value;

    const filtrados = usuariosData.filter(usuario => {
        const matchSearch = !searchText ||
            usuario.nombres.toLowerCase().includes(searchText) ||
            usuario.apellidos.toLowerCase().includes(searchText) ||
            usuario.DNI.includes(searchText) ||
            usuario.username.toLowerCase().includes(searchText);

        const matchPerfil = !filterPerfil || usuario.idPerfil == filterPerfil;
        const matchEstado = !filterEstado || usuario.estadoBD === filterEstado;

        return matchSearch && matchPerfil && matchEstado;
    });

    renderUsuarios(filtrados);
}

// Limpiar filtros
function clearFilters() {
    document.getElementById('searchUsuario').value = '';
    document.getElementById('filterPerfil').value = '';
    document.getElementById('filterEstado').value = '';
    renderUsuarios(usuariosData);
}

// Exportar usuarios
function exportUsuarios() {
    showNotification('Función de exportación en desarrollo', 'info');
}

// Formatear fecha
function formatearFecha(fecha) {
    if (!fecha) return 'N/A';
    const date = new Date(fecha);
    return date.toLocaleDateString('es-PE', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Notificaciones
function showNotification(message, type = 'info') {
    const toast = document.createElement('div');
    const bgClass = type === 'success' ? 'bg-success' : type === 'error' ? 'bg-danger' : 'bg-info';
    toast.className = `alert ${bgClass} text-white position-fixed`;
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    toast.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'error' ? 'x-circle' : 'info-circle'}"></i> 
        ${message}
    `;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

