/* Q'Tal Lisura - JavaScript para Reservas (Admin) */

let reservasData = [];
let mesasData = [];
let detalleModal;

document.addEventListener('DOMContentLoaded', function() {
    detalleModal = new bootstrap.Modal(document.getElementById('detalleModal'));
    cargarMesas();
    cargarReservas();
    setupFilters();
});

// Cargar mesas para el filtro
async function cargarMesas() {
    try {
        const response = await fetch('/mesa');
        if (response.ok) {
            mesasData = await response.json();
            const select = document.getElementById('filterMesa');
            if (select) {
                mesasData.forEach(mesa => {
                    const option = document.createElement('option');
                    option.value = mesa.idMesa;
                    option.textContent = `Mesa ${mesa.numeroMesa}`;
                    select.appendChild(option);
                });
            }
        }
    } catch (error) {
        console.error('Error al cargar mesas:', error);
    }
}

// Cargar reservas desde el backend
async function cargarReservas() {
    try {
        const response = await fetch('/reserva');
        if (response.ok) {
            reservasData = await response.json();
            renderReservas(reservasData);
            actualizarEstadisticas();
        } else {
            showNotification('Error al cargar reservas', 'error');
        }
    } catch (error) {
        console.error('Error al cargar reservas:', error);
        showNotification('Error de conexión', 'error');
    }
}

// Actualizar estadísticas
function actualizarEstadisticas() {
    const hoy = new Date().toISOString().split('T')[0];

    const pendientes = reservasData.filter(r => r.estadoSolicitud === 'PENDIENTE').length;
    const confirmadas = reservasData.filter(r => r.estadoSolicitud === 'CONFIRMADA').length;
    const canceladas = reservasData.filter(r => r.estadoSolicitud === 'CANCELADA').length;
    const paraHoy = reservasData.filter(r => r.fechaHora && r.fechaHora.startsWith(hoy)).length;

    document.getElementById('statPendientes').textContent = pendientes;
    document.getElementById('statConfirmadas').textContent = confirmadas;
    document.getElementById('statCanceladas').textContent = canceladas;
    document.getElementById('statHoy').textContent = paraHoy;
}

// Renderizar tabla de reservas
function renderReservas(reservas) {
    const tbody = document.getElementById('reservasTableBody');
    if (!tbody) return;

    if (!reservas || reservas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4">No hay reservas registradas</td></tr>';
        return;
    }

    tbody.innerHTML = reservas.map(reserva => `
        <tr>
            <td>#${reserva.idReserva}</td>
            <td>
                <div>
                    <i class="bi bi-calendar me-1"></i>
                    ${formatFecha(reserva.fechaHora)}
                </div>
                <small class="text-muted">
                    <i class="bi bi-clock me-1"></i>
                    ${formatHora(reserva.fechaHora)}
                </small>
            </td>
            <td>
                <div class="fw-bold">${reserva.clienteNombre || 'Cliente #' + reserva.idCliente}</div>
            </td>
            <td>
                <i class="bi bi-telephone me-1"></i>
                ${reserva.telefono || 'N/A'}
            </td>
            <td>
                <span class="badge bg-primary">Mesa ${reserva.numeroMesa || reserva.idMesa}</span>
            </td>
            <td>
                <span class="badge ${getEstadoClass(reserva.estadoSolicitud)}">
                    ${formatEstado(reserva.estadoSolicitud)}
                </span>
            </td>
            <td>
                <div class="action-btns">
                    <button class="action-btn view" onclick="verDetalle(${reserva.idReserva})" title="Ver detalles">
                        <i class="bi bi-eye"></i>
                    </button>
                    ${reserva.estadoSolicitud === 'PENDIENTE' ? `
                        <button class="action-btn edit" onclick="confirmarReserva(${reserva.idReserva})" title="Confirmar">
                            <i class="bi bi-check-lg"></i>
                        </button>
                        <button class="action-btn delete" onclick="cancelarReserva(${reserva.idReserva})" title="Cancelar">
                            <i class="bi bi-x-lg"></i>
                        </button>
                    ` : ''}
                </div>
            </td>
        </tr>
    `).join('');
}

// Obtener clase CSS según estado
function getEstadoClass(estado) {
    const clases = {
        'PENDIENTE': 'bg-warning text-dark',
        'CONFIRMADA': 'bg-success',
        'CANCELADA': 'bg-danger'
    };
    return clases[estado] || 'bg-secondary';
}

// Formatear estado
function formatEstado(estado) {
    const nombres = {
        'PENDIENTE': 'Pendiente',
        'CONFIRMADA': 'Confirmada',
        'CANCELADA': 'Cancelada'
    };
    return nombres[estado] || estado;
}

// Formatear fecha
function formatFecha(fechaHora) {
    if (!fechaHora) return 'N/A';
    const fecha = new Date(fechaHora);
    return fecha.toLocaleDateString('es-PE', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
    });
}

// Formatear hora
function formatHora(fechaHora) {
    if (!fechaHora) return '';
    const fecha = new Date(fechaHora);
    return fecha.toLocaleTimeString('es-PE', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Ver detalle de reserva
function verDetalle(id) {
    const reserva = reservasData.find(r => r.idReserva === id);
    if (!reserva) return;

    const content = document.getElementById('detalleContent');
    content.innerHTML = `
        <div class="mb-3">
            <h6 class="text-muted">Información de la Reserva</h6>
            <hr>
        </div>
        <div class="row mb-2">
            <div class="col-5 text-muted">ID Reserva:</div>
            <div class="col-7 fw-bold">#${reserva.idReserva}</div>
        </div>
        <div class="row mb-2">
            <div class="col-5 text-muted">Fecha:</div>
            <div class="col-7">${formatFecha(reserva.fechaHora)}</div>
        </div>
        <div class="row mb-2">
            <div class="col-5 text-muted">Hora:</div>
            <div class="col-7">${formatHora(reserva.fechaHora)}</div>
        </div>
        <div class="row mb-2">
            <div class="col-5 text-muted">Cliente:</div>
            <div class="col-7">${reserva.clienteNombre || 'Cliente #' + reserva.idCliente}</div>
        </div>
        <div class="row mb-2">
            <div class="col-5 text-muted">Teléfono:</div>
            <div class="col-7">${reserva.telefono || 'N/A'}</div>
        </div>
        <div class="row mb-2">
            <div class="col-5 text-muted">Mesa:</div>
            <div class="col-7">Mesa ${reserva.numeroMesa || reserva.idMesa}</div>
        </div>
        <div class="row mb-2">
            <div class="col-5 text-muted">Estado:</div>
            <div class="col-7">
                <span class="badge ${getEstadoClass(reserva.estadoSolicitud)}">${formatEstado(reserva.estadoSolicitud)}</span>
            </div>
        </div>
        ${reserva.notasEspeciales ? `
            <div class="mt-3">
                <h6 class="text-muted">Notas Especiales</h6>
                <p class="mb-0 bg-light p-2 rounded">${reserva.notasEspeciales}</p>
            </div>
        ` : ''}
    `;

    detalleModal.show();
}

// Confirmar reserva
async function confirmarReserva(id) {
    if (!confirm('¿Confirmar esta reserva?')) return;
    await actualizarEstadoReserva(id, 'CONFIRMADA');
}

// Cancelar reserva
async function cancelarReserva(id) {
    if (!confirm('¿Cancelar esta reserva?')) return;
    await actualizarEstadoReserva(id, 'CANCELADA');
}

// Actualizar estado de reserva
async function actualizarEstadoReserva(id, nuevoEstado) {
    const reserva = reservasData.find(r => r.idReserva === id);
    if (!reserva) return;

    const data = {
        idCliente: reserva.idCliente,
        idMesa: reserva.idMesa,
        fechaHora: reserva.fechaHora,
        telefono: reserva.telefono,
        estadoSolicitud: nuevoEstado,
        notasEspeciales: reserva.notasEspeciales,
        estadoBD: reserva.estadoBD || 'ACTIVO'
    };

    try {
        const response = await fetch(`/reserva/${id}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showNotification(`Reserva ${nuevoEstado === 'CONFIRMADA' ? 'confirmada' : 'cancelada'} exitosamente`, 'success');
            cargarReservas();
        } else {
            showNotification('Error al actualizar reserva', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        showNotification('Error de conexión', 'error');
    }
}

// Configurar filtros
function setupFilters() {
    const searchInput = document.getElementById('searchReserva');
    const filterEstado = document.getElementById('filterEstado');
    const filterFecha = document.getElementById('filterFecha');
    const filterMesa = document.getElementById('filterMesa');

    [searchInput, filterEstado, filterFecha, filterMesa].forEach(element => {
        if (element) {
            element.addEventListener('change', aplicarFiltros);
            element.addEventListener('keyup', aplicarFiltros);
        }
    });
}

// Aplicar filtros
function aplicarFiltros() {
    const searchText = document.getElementById('searchReserva')?.value.toLowerCase() || '';
    const filterEstado = document.getElementById('filterEstado')?.value || '';
    const filterFecha = document.getElementById('filterFecha')?.value || '';
    const filterMesa = document.getElementById('filterMesa')?.value || '';

    const filtrados = reservasData.filter(reserva => {
        const matchSearch = !searchText ||
            (reserva.clienteNombre && reserva.clienteNombre.toLowerCase().includes(searchText)) ||
            (reserva.telefono && reserva.telefono.toString().includes(searchText));

        const matchEstado = !filterEstado || reserva.estadoSolicitud === filterEstado;

        const matchFecha = !filterFecha ||
            (reserva.fechaHora && reserva.fechaHora.startsWith(filterFecha));

        const matchMesa = !filterMesa || reserva.idMesa == filterMesa;

        return matchSearch && matchEstado && matchFecha && matchMesa;
    });

    renderReservas(filtrados);
}

// Limpiar filtros
function clearFilters() {
    document.getElementById('searchReserva').value = '';
    document.getElementById('filterEstado').value = '';
    document.getElementById('filterFecha').value = '';
    document.getElementById('filterMesa').value = '';
    renderReservas(reservasData);
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

