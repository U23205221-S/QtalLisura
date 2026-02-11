/* Q'Tal Lisura - JavaScript para módulo de Mesas (Admin) */

let mesasData = [];
let mesaModal;
let verMesaModal;
let confirmarEliminarMesaModal;
let mesaAEliminar = null;

document.addEventListener('DOMContentLoaded', function() {
    mesaModal = new bootstrap.Modal(document.getElementById('mesaModal'));
    verMesaModal = new bootstrap.Modal(document.getElementById('verMesaModal'));
    confirmarEliminarMesaModal = new bootstrap.Modal(document.getElementById('confirmarEliminarMesaModal'));
    
    // Configurar botón de confirmación de eliminación
    document.getElementById('btnConfirmarEliminarMesa').addEventListener('click', confirmarEliminarMesa);
    
    cargarMesas();
    setupFilters();
});

// Cargar mesas desde el backend
async function cargarMesas() {
    try {
        const response = await fetch('/mesa');
        if (response.ok) {
            mesasData = await response.json();
            renderMesas(mesasData);
        } else {
            showNotification('Error al cargar mesas', 'error');
        }
    } catch (error) {
        console.error('Error al cargar mesas:', error);
        showNotification('Error de conexión', 'error');
    }
}

// Renderizar tabla de mesas
function renderMesas(mesas) {
    const tbody = document.getElementById('mesasTableBody');
    if (!tbody) return;

    if (!mesas || mesas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4">No hay mesas registradas</td></tr>';
        return;
    }

    tbody.innerHTML = mesas.map(mesa => `
        <tr>
            <td>${mesa.idMesa}</td>
            <td>
                <span class="badge bg-primary fs-6">Mesa ${mesa.numeroMesa}</span>
            </td>
            <td>
                <i class="bi bi-people-fill me-1"></i> ${mesa.capacidadMaxima} personas
            </td>
            <td>${formatUbicacion(mesa.ubicacion)}</td>
            <td>
                <span class="badge ${getEstadoMesaClass(mesa.estadoMesa)}">
                    ${formatEstadoMesa(mesa.estadoMesa)}
                </span>
            </td>
            <td>
                <div class="form-check form-switch d-flex align-items-center justify-content-center">
                    <input class="form-check-input" type="checkbox" role="switch" 
                           id="switchMesa${mesa.idMesa}"
                           ${mesa.estadoBD === 'ACTIVO' ? 'checked' : ''}
                           onchange="toggleEstadoMesa(${mesa.idMesa}, this.checked)"
                           title="${mesa.estadoBD === 'ACTIVO' ? 'Desactivar' : 'Activar'} mesa">
                </div>
            </td>
            <td>
                <div class="action-btns">
                    <button class="action-btn view" onclick="verMesa(${mesa.idMesa})" title="Ver detalles">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="action-btn edit" onclick="editarMesa(${mesa.idMesa})" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="action-btn delete" onclick="eliminarMesa(${mesa.idMesa})" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Obtener clase CSS según estado de mesa
function getEstadoMesaClass(estado) {
    const clases = {
        'DISPONIBLE': 'bg-success',
        'OCUPADA': 'bg-danger',
        'RESERVADA': 'bg-warning text-dark',
        'EN_MANTENIMIENTO': 'bg-secondary'
    };
    return clases[estado] || 'bg-secondary';
}

// Formatear estado de mesa
function formatEstadoMesa(estado) {
    const nombres = {
        'DISPONIBLE': 'Disponible',
        'OCUPADA': 'Ocupada',
        'RESERVADA': 'Reservada',
        'EN_MANTENIMIENTO': 'En Mantenimiento'
    };
    return nombres[estado] || estado;
}

// Formatear ubicación
function formatUbicacion(ubicacion) {
    const nombres = {
        'PRIMER_PISO': 'Primer Piso',
        'SEGUNDO_PISO': 'Segundo Piso'
    };
    return nombres[ubicacion] || ubicacion;
}

// Abrir modal para crear/editar
function openMesaModal(action, id = null) {
    const form = document.getElementById('mesaForm');
    const title = document.getElementById('mesaModalTitle');

    form.reset();
    form.classList.remove('was-validated');

    if (action === 'create') {
        title.textContent = 'Nueva Mesa';
        document.getElementById('mesaId').value = '';
    } else if (action === 'edit' && id) {
        title.textContent = 'Editar Mesa';
        cargarDatosMesa(id);
    }

    mesaModal.show();
}

// Cargar datos de la mesa para edición
async function cargarDatosMesa(id) {
    try {
        const response = await fetch(`/mesa/${id}`);
        if (response.ok) {
            const mesa = await response.json();
            document.getElementById('mesaId').value = mesa.idMesa;
            document.getElementById('mesaNumero').value = mesa.numeroMesa;
            document.getElementById('mesaCapacidad').value = mesa.capacidadMaxima;
            document.getElementById('mesaUbicacion').value = mesa.ubicacion;
            document.getElementById('mesaEstadoMesa').value = mesa.estadoMesa;
        }
    } catch (error) {
        console.error('Error al cargar mesa:', error);
        showNotification('Error al cargar datos de la mesa', 'error');
    }
}

// Editar mesa
function editarMesa(id) {
    openMesaModal('edit', id);
}

// Ver mesa (modal Bootstrap)
function verMesa(id) {
    const mesa = mesasData.find(m => m.idMesa === id);
    if (mesa) {
        document.getElementById('verMesaNumero').textContent = `Mesa ${mesa.numeroMesa}`;
        document.getElementById('verMesaId').textContent = mesa.idMesa;
        document.getElementById('verMesaCapacidad').textContent = `${mesa.capacidadMaxima} personas`;
        document.getElementById('verMesaUbicacion').textContent = formatUbicacion(mesa.ubicacion);
        
        const estadoMesaBadge = document.getElementById('verMesaEstadoMesa');
        estadoMesaBadge.textContent = formatEstadoMesa(mesa.estadoMesa);
        estadoMesaBadge.className = `badge ${getEstadoMesaClass(mesa.estadoMesa)}`;
        
        const estadoBadge = document.getElementById('verMesaEstado');
        estadoBadge.textContent = mesa.estadoBD === 'ACTIVO' ? 'Activo' : 'Inactivo';
        estadoBadge.className = `badge ${mesa.estadoBD === 'ACTIVO' ? 'bg-success' : 'bg-secondary'}`;
        
        verMesaModal.show();
    }
}

// Toggle estado de la mesa
async function toggleEstadoMesa(id, activo) {
    const nuevoEstado = activo ? 'ACTIVO' : 'INACTIVO';
    const mesa = mesasData.find(m => m.idMesa === id);
    
    if (!mesa) return;
    
    try {
        const data = {
            numeroMesa: mesa.numeroMesa,
            capacidadMaxima: mesa.capacidadMaxima,
            ubicacion: mesa.ubicacion,
            estadoMesa: mesa.estadoMesa,
            estadoBD: nuevoEstado
        };

        const response = await fetch(`/mesa/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showNotification(`Mesa ${activo ? 'activada' : 'desactivada'} exitosamente`, 'success');
            cargarMesas();
        } else {
            const errorText = await response.text();
            console.error('Error response:', errorText);
            showNotification('Error al cambiar estado', 'error');
            document.getElementById(`switchMesa${id}`).checked = !activo;
        }
    } catch (error) {
        console.error('Error al cambiar estado:', error);
        showNotification('Error al cambiar estado', 'error');
        document.getElementById(`switchMesa${id}`).checked = !activo;
    }
}

// Guardar mesa (crear o actualizar)
async function saveMesa() {
    const form = document.getElementById('mesaForm');

    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        showNotification('Por favor complete todos los campos requeridos', 'error');
        return;
    }

    const id = document.getElementById('mesaId').value;
    
    // Si es edición, mantener el estado actual; si es nuevo, ACTIVO por defecto
    let estadoBD = 'ACTIVO';
    if (id) {
        const mesa = mesasData.find(m => m.idMesa == id);
        estadoBD = mesa ? mesa.estadoBD : 'ACTIVO';
    }
    
    const data = {
        numeroMesa: parseInt(document.getElementById('mesaNumero').value),
        capacidadMaxima: parseInt(document.getElementById('mesaCapacidad').value),
        ubicacion: document.getElementById('mesaUbicacion').value,
        estadoMesa: document.getElementById('mesaEstadoMesa').value,
        estadoBD: estadoBD
    };

    try {
        const url = id ? `/mesa/${id}` : '/mesa';
        const method = id ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showNotification(`Mesa ${id ? 'actualizada' : 'creada'} exitosamente`, 'success');
            mesaModal.hide();
            cargarMesas();
        } else {
            const error = await response.text();
            showNotification(`Error: ${error}`, 'error');
        }
    } catch (error) {
        console.error('Error al guardar mesa:', error);
        showNotification('Error al guardar la mesa', 'error');
    }
}

// Eliminar mesa (abrir modal de confirmación)
function eliminarMesa(id) {
    const mesa = mesasData.find(m => m.idMesa === id);
    if (mesa) {
        mesaAEliminar = id;
        document.getElementById('eliminarMesaNombre').textContent = `Mesa ${mesa.numeroMesa}`;
        confirmarEliminarMesaModal.show();
    }
}

// Confirmar eliminación de mesa
async function confirmarEliminarMesa() {
    if (!mesaAEliminar) return;
    
    try {
        const response = await fetch(`/mesa/${mesaAEliminar}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Mesa eliminada exitosamente', 'success');
            confirmarEliminarMesaModal.hide();
            cargarMesas();
        } else {
            showNotification('Error al eliminar mesa', 'error');
        }
    } catch (error) {
        console.error('Error al eliminar mesa:', error);
        showNotification('Error al eliminar mesa', 'error');
    } finally {
        mesaAEliminar = null;
    }
}

// Configurar filtros
function setupFilters() {
    const searchInput = document.getElementById('searchMesa');
    const filterEstadoMesa = document.getElementById('filterEstadoMesa');
    const filterUbicacion = document.getElementById('filterUbicacion');

    [searchInput, filterEstadoMesa, filterUbicacion].forEach(element => {
        if (element) {
            element.addEventListener('change', aplicarFiltros);
            element.addEventListener('keyup', aplicarFiltros);
        }
    });
}

// Aplicar filtros
function aplicarFiltros() {
    const searchText = document.getElementById('searchMesa')?.value.toLowerCase() || '';
    const filterEstadoMesa = document.getElementById('filterEstadoMesa')?.value || '';
    const filterUbicacion = document.getElementById('filterUbicacion')?.value || '';

    const filtrados = mesasData.filter(mesa => {
        const matchSearch = !searchText ||
            mesa.numeroMesa.toString().includes(searchText);

        const matchEstadoMesa = !filterEstadoMesa || mesa.estadoMesa === filterEstadoMesa;
        const matchUbicacion = !filterUbicacion || mesa.ubicacion === filterUbicacion;

        return matchSearch && matchEstadoMesa && matchUbicacion;
    });

    renderMesas(filtrados);
}

// Limpiar filtros
function clearFilters() {
    document.getElementById('searchMesa').value = '';
    document.getElementById('filterEstadoMesa').value = '';
    document.getElementById('filterUbicacion').value = '';
    renderMesas(mesasData);
}

// Formatear estado BD para mostrar
function formatEstadoBD(estado) {
    const nombres = {
        'ACTIVO': 'Activo',
        'INACTIVO': 'Inactivo',
        'ELIMINADO': 'Eliminado'
    };
    return nombres[estado] || estado;
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
