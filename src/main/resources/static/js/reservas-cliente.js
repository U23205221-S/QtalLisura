/* Q'Tal Lisura - JavaScript para Reservas (Cliente) */

let mesasDisponibles = [];
let clienteActual = null;

document.addEventListener('DOMContentLoaded', function() {
    cargarMesasDisponibles();
    setupFormulario();
    setMinFechaHora();
    verificarSesion();
});

// Verificar si hay sesión activa para autocompletar datos
async function verificarSesion() {
    try {
        const response = await fetch('/api/auth/check-session');
        if (response.ok) {
            const data = await response.json();
            if (data.authenticated) {
                clienteActual = data;
                // Autocompletar nombre si está logueado
                const nombreInput = document.getElementById('clienteNombre');
                if (nombreInput && data.nombreCompleto) {
                    nombreInput.value = data.nombreCompleto;
                }
            }
        }
    } catch (error) {
        console.log('No hay sesión activa');
    }
}

// Establecer fecha mínima (hoy + 1 hora)
function setMinFechaHora() {
    const fechaInput = document.getElementById('fechaHora');
    if (fechaInput) {
        const ahora = new Date();
        ahora.setHours(ahora.getHours() + 1);
        const min = ahora.toISOString().slice(0, 16);
        fechaInput.min = min;
    }
}

// Cargar mesas disponibles desde el backend
async function cargarMesasDisponibles() {
    try {
        const response = await fetch('/mesa');
        if (response.ok) {
            const mesas = await response.json();
            // Filtrar solo mesas disponibles y activas
            mesasDisponibles = mesas.filter(m =>
                m.estadoMesa === 'DISPONIBLE' && m.estadoBD === 'ACTIVO'
            );
            actualizarSelectMesas();
        }
    } catch (error) {
        console.error('Error al cargar mesas:', error);
        showMessage('Error al cargar mesas disponibles', 'danger');
    }
}

// Actualizar el select de mesas según la cantidad de personas
function actualizarSelectMesas() {
    const select = document.getElementById('mesaSelect');
    const cantidadPersonas = parseInt(document.getElementById('cantidadPersonas')?.value) || 0;

    if (!select) return;

    // Limpiar opciones existentes
    select.innerHTML = '<option value="">Selecciona una mesa...</option>';

    // Filtrar mesas con capacidad suficiente
    const mesasFiltradas = cantidadPersonas > 0
        ? mesasDisponibles.filter(m => m.capacidadMaxima >= cantidadPersonas)
        : mesasDisponibles;

    if (mesasFiltradas.length === 0) {
        select.innerHTML = '<option value="">No hay mesas disponibles para esa cantidad</option>';
        return;
    }

    mesasFiltradas.forEach(mesa => {
        const option = document.createElement('option');
        option.value = mesa.idMesa;
        option.textContent = `Mesa ${mesa.numeroMesa} - ${mesa.capacidadMaxima} personas (${formatUbicacion(mesa.ubicacion)})`;
        select.appendChild(option);
    });
}

// Formatear ubicación
function formatUbicacion(ubicacion) {
    const nombres = {
        'PRIMER_PISO': 'Primer Piso',
        'SEGUNDO_PISO': 'Segundo Piso'
    };
    return nombres[ubicacion] || ubicacion;
}

// Configurar el formulario
function setupFormulario() {
    const form = document.getElementById('reservaForm');
    const cantidadSelect = document.getElementById('cantidadPersonas');

    // Actualizar mesas cuando cambie la cantidad de personas
    if (cantidadSelect) {
        cantidadSelect.addEventListener('change', actualizarSelectMesas);
    }

    // Manejar envío del formulario
    if (form) {
        form.addEventListener('submit', enviarReserva);
    }
}

// Enviar reserva
async function enviarReserva(e) {
    e.preventDefault();

    const form = e.target;
    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        return;
    }

    const submitBtn = form.querySelector('button[type="submit"]');
    const originalText = submitBtn.innerHTML;
    submitBtn.disabled = true;
    submitBtn.innerHTML = '<i class="bi bi-hourglass-split me-2"></i>Procesando...';

    // Obtener valores del formulario
    const fechaHoraInput = document.getElementById('fechaHora').value;
    const telefono = document.getElementById('clienteTelefono').value;
    const mesaId = document.getElementById('mesaSelect').value;
    const notas = document.getElementById('notasEspeciales').value;

    // Convertir fecha a formato ISO 8601 para Java LocalDateTime
    const fechaHora = new Date(fechaHoraInput).toISOString();

    // Preparar datos de la reserva
    // Nota: idCliente se asignará en el backend basándose en la sesión o se creará un cliente temporal
    const reservaData = {
        idCliente: 1, // Cliente por defecto (se debe ajustar según la lógica de negocio)
        idMesa: parseInt(mesaId),
        fechaHora: fechaHoraInput, // Formato: 2024-01-15T19:30
        telefono: parseInt(telefono),
        estadoSolicitud: 'PENDIENTE',
        notasEspeciales: notas || null,
        estadoBD: 'ACTIVO'
    };

    try {
        const response = await fetch('/reserva', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reservaData)
        });

        if (response.ok) {
            const data = await response.json();
            showMessage('¡Reserva realizada exitosamente! Te contactaremos para confirmar.', 'success');
            form.reset();
            form.classList.remove('was-validated');
            setMinFechaHora();

            // Actualizar mesas disponibles
            cargarMesasDisponibles();
        } else {
            const error = await response.text();
            showMessage(`Error al crear reserva: ${error}`, 'danger');
        }
    } catch (error) {
        console.error('Error:', error);
        showMessage('Error de conexión. Por favor intenta nuevamente.', 'danger');
    } finally {
        submitBtn.disabled = false;
        submitBtn.innerHTML = originalText;
    }
}

// Mostrar mensaje
function showMessage(message, type = 'info') {
    const messageDiv = document.getElementById('reservaMessage');
    if (messageDiv) {
        messageDiv.className = `alert alert-${type} mt-4`;
        messageDiv.innerHTML = `
            <i class="bi bi-${type === 'success' ? 'check-circle' : 'exclamation-triangle'} me-2"></i>
            ${message}
        `;
        messageDiv.classList.remove('d-none');

        // Scroll al mensaje
        messageDiv.scrollIntoView({ behavior: 'smooth', block: 'center' });

        // Ocultar después de 5 segundos si es éxito
        if (type === 'success') {
            setTimeout(() => {
                messageDiv.classList.add('d-none');
            }, 5000);
        }
    }
}

