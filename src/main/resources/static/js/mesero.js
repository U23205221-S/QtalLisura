/* Q'Tal Lisura - JavaScript para Panel de Mesero */

let pedidosData = [];
let productosDisponibles = [];
let detallesPedido = [];
let meseroId = null;

document.addEventListener('DOMContentLoaded', function() {
    obtenerSesion().then(() => {
        cargarPedidos();
        cargarProductos();
    });
});

async function obtenerSesion() {
    try {
        const response = await fetch('/api/auth/current-user');
        const data = await response.json();
        if (data.success) {
            meseroId = data.idUsuario;
        } else {
            window.location.href = '/mesero-login';
        }
    } catch (error) {
        console.error('Error obteniendo sesión:', error);
        window.location.href = '/mesero-login';
    }
}

async function cargarPedidos() {
    if (!meseroId) return;
    try {
        const response = await fetch(`/pedido/usuario/${meseroId}`);
        pedidosData = await response.json();
        renderizarPedidos(pedidosData);
        actualizarStats(pedidosData);
    } catch (error) {
        console.error('Error cargando pedidos:', error);
        showNotification('Error al cargar los pedidos', 'error');
    }
}

async function cargarProductos() {
    try {
        const response = await fetch('/producto');
        const data = await response.json();
        productosDisponibles = Array.isArray(data) ? data.filter(p => p.estadoBD === 'ACTIVO') : [];
        renderizarSelectProductos();
    } catch (error) {
        console.error('Error cargando productos:', error);
    }
}

async function cargarMesasDisponibles() {
    try {
        const response = await fetch('/mesa/disponibles');
        const mesas = await response.json();
        const select = document.getElementById('pedidoMesa');
        select.innerHTML = '<option value="">Seleccionar mesa...</option>';
        mesas.forEach(mesa => {
            select.innerHTML += `<option value="${mesa.idMesa}">Mesa ${mesa.numeroMesa} - ${mesa.ubicacion} (Cap. ${mesa.capacidadMaxima})</option>`;
        });
    } catch (error) {
        console.error('Error cargando mesas:', error);
    }
}

function renderizarSelectProductos() {
    const select = document.getElementById('selectProducto');
    if (!select) return;
    select.innerHTML = '<option value="">Seleccionar producto...</option>';
    productosDisponibles.forEach(prod => {
        const stockInfo = prod.stockActual > 0 ? `Stock: ${prod.stockActual}` : 'SIN STOCK';
        const disabled = prod.stockActual <= 0 ? 'disabled' : '';
        select.innerHTML += `<option value="${prod.idProducto}" 
            data-precio="${prod.precioVenta}" 
            data-nombre="${prod.nombre}"
            data-stock="${prod.stockActual}"
            ${disabled}>
            ${prod.nombre} - S/ ${prod.precioVenta.toFixed(2)} (${stockInfo})
        </option>`;
    });
}

function renderizarPedidos(pedidos) {
    const tbody = document.getElementById('pedidosTableBody');
    if (!tbody) return;

    if (!pedidos || pedidos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted py-4">No hay pedidos registrados</td></tr>';
        return;
    }

    tbody.innerHTML = pedidos.map(pedido => {
        const estadoBadge = getEstadoBadge(pedido.estadoPedido);
        const fecha = pedido.fechaPedido ? new Date(pedido.fechaPedido).toLocaleString('es-PE', { dateStyle: 'short', timeStyle: 'short' }) : '-';
        const acciones = generarAcciones(pedido);
        return `<tr>
            <td><span class="fw-bold small">${pedido.codigo}</span></td>
            <td><i class="bi bi-diagram-3 me-1"></i><span class="d-none d-sm-inline">Mesa </span>${pedido.numeroMesa}</td>
            <td class="d-none d-md-table-cell small">${pedido.clienteNombre || 'Consumidor Final'}</td>
            <td>${estadoBadge}</td>
            <td class="fw-bold small">S/ ${pedido.total ? pedido.total.toFixed(2) : '0.00'}</td>
            <td class="d-none d-lg-table-cell small">${fecha}</td>
            <td>${acciones}</td>
        </tr>`;
    }).join('');
}

function getEstadoBadge(estado) {
    const badges = {
        'Pendiente': '<span class="badge bg-warning text-dark">Pendiente</span>',
        'En Preparación': '<span class="badge bg-info">En Preparación</span>',
        'Servido': '<span class="badge bg-success">Servido</span>',
        'Pagado': '<span class="badge bg-secondary">Pagado</span>',
        'Cancelado': '<span class="badge bg-danger">Cancelado</span>'
    };
    return badges[estado] || `<span class="badge bg-dark">${estado}</span>`;
}

function generarAcciones(pedido) {
    const estado = pedido.estadoPedido;
    let btns = `<button class="btn btn-sm btn-outline-primary me-1" onclick="MeseroPedidos.verDetalle(${pedido.idPedido})" title="Ver detalle">
        <i class="bi bi-eye"></i>
    </button>`;

    if (estado === 'Pendiente') {
        btns += `<button class="btn btn-sm btn-outline-info me-1" onclick="MeseroPedidos.cambiarEstado(${pedido.idPedido}, 'EN_PREPARACION')" title="Enviar a cocina">
            <i class="bi bi-fire"></i>
        </button>`;
        btns += `<button class="btn btn-sm btn-outline-danger" onclick="MeseroPedidos.cancelarPedido(${pedido.idPedido})" title="Cancelar pedido">
            <i class="bi bi-x-circle"></i>
        </button>`;
    } else if (estado === 'En Preparación') {
        btns += `<button class="btn btn-sm btn-outline-success me-1" onclick="MeseroPedidos.cambiarEstado(${pedido.idPedido}, 'SERVIDO')" title="Marcar como servido">
            <i class="bi bi-check-circle"></i>
        </button>`;
        btns += `<button class="btn btn-sm btn-outline-danger" onclick="MeseroPedidos.cancelarPedido(${pedido.idPedido})" title="Cancelar pedido">
            <i class="bi bi-x-circle"></i>
        </button>`;
    } else if (estado === 'Servido') {
        btns += `<button class="btn btn-sm btn-outline-secondary me-1" onclick="MeseroPedidos.cambiarEstado(${pedido.idPedido}, 'PAGADO')" title="Registrar pago">
            <i class="bi bi-cash-coin"></i>
        </button>`;
        btns += `<button class="btn btn-sm btn-outline-danger" onclick="MeseroPedidos.cancelarPedido(${pedido.idPedido})" title="Cancelar pedido">
            <i class="bi bi-x-circle"></i>
        </button>`;
    } else if (estado === 'Pagado') {
        btns += `<span class="badge bg-success small"><i class="bi bi-check-circle-fill"></i> <span class="d-none d-lg-inline">Completado</span></span>`;
    } else if (estado === 'Cancelado') {
        btns += `<span class="badge bg-danger small"><i class="bi bi-x-circle-fill"></i> <span class="d-none d-lg-inline">Cancelado</span></span>`;
    }

    return btns;
}

function actualizarStats(pedidos) {
    const stats = { pendientes: 0, enPreparacion: 0, servidos: 0, pagados: 0 };
    pedidos.forEach(p => {
        if (p.estadoPedido === 'Pendiente') stats.pendientes++;
        else if (p.estadoPedido === 'En Preparación') stats.enPreparacion++;
        else if (p.estadoPedido === 'Servido') stats.servidos++;
        else if (p.estadoPedido === 'Pagado') stats.pagados++;
    });
    const el = id => document.getElementById(id);
    if (el('statPendientes')) el('statPendientes').textContent = stats.pendientes;
    if (el('statEnPreparacion')) el('statEnPreparacion').textContent = stats.enPreparacion;
    if (el('statServidos')) el('statServidos').textContent = stats.servidos;
    if (el('statPagados')) el('statPagados').textContent = stats.pagados;
}

function filtrarPedidos() {
    const search = (document.getElementById('searchPedido')?.value || '').toLowerCase();
    const estado = document.getElementById('filterEstadoPedido')?.value || '';
    const fecha = document.getElementById('filterFechaPedido')?.value || '';

    const filtrados = pedidosData.filter(p => {
        const matchSearch = !search ||
            p.codigo.toLowerCase().includes(search) ||
            String(p.numeroMesa).includes(search) ||
            (p.clienteNombre || '').toLowerCase().includes(search);
        const matchEstado = !estado || p.estadoPedido === estado;
        const matchFecha = !fecha || (p.fechaPedido && p.fechaPedido.startsWith(fecha));
        return matchSearch && matchEstado && matchFecha;
    });
    renderizarPedidos(filtrados);
}

function limpiarFiltros() {
    const el = id => document.getElementById(id);
    if (el('searchPedido')) el('searchPedido').value = '';
    if (el('filterEstadoPedido')) el('filterEstadoPedido').value = '';
    if (el('filterFechaPedido')) el('filterFechaPedido').value = '';
    renderizarPedidos(pedidosData);
}

// ============ CRUD de Pedidos ============

function generarCodigo() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789';
    let codigo = '';
    for (let i = 0; i < 6; i++) {
        codigo += chars.charAt(Math.floor(Math.random() * chars.length));
    }
    return codigo;
}

function abrirNuevoPedido() {
    document.getElementById('pedidoModalTitle').textContent = 'Nuevo Pedido';
    document.getElementById('pedidoId').value = '';
    document.getElementById('pedidoEstado').value = 'PENDIENTE';
    document.getElementById('pedidoEstado').disabled = true;
    document.getElementById('pedidoMesa').disabled = false;
    detallesPedido = [];
    renderizarDetalles();
    cargarMesasDisponibles();
}

function agregarProducto() {
    const select = document.getElementById('selectProducto');
    const cantidadInput = document.getElementById('cantidadProducto');
    const productoId = parseInt(select.value);
    const cantidad = parseInt(cantidadInput.value);

    if (!productoId) {
        showNotification('Selecciona un producto', 'error');
        return;
    }
    if (!cantidad || cantidad < 1) {
        showNotification('Ingresa una cantidad válida', 'error');
        return;
    }

    const option = select.options[select.selectedIndex];
    const nombre = option.getAttribute('data-nombre');
    const precio = parseFloat(option.getAttribute('data-precio'));
    const stockDisponible = parseInt(option.getAttribute('data-stock'));

    // Calcular cantidad ya agregada de este producto en el pedido actual
    const existente = detallesPedido.find(d => d.idProducto === productoId);
    const cantidadYaAgregada = existente ? existente.cantidad : 0;
    const cantidadTotal = cantidadYaAgregada + cantidad;

    // Validar stock disponible
    if (cantidadTotal > stockDisponible) {
        showNotification(`Stock insuficiente para '${nombre}'. Disponible: ${stockDisponible}, Ya agregado: ${cantidadYaAgregada}, Solicitado: ${cantidad}`, 'error');
        return;
    }

    // Verificar si ya existe y actualizar
    if (existente) {
        existente.cantidad = cantidadTotal;
        existente.subtotal = existente.precioUnitario * existente.cantidad;
    } else {
        detallesPedido.push({
            idProducto: productoId,
            nombre: nombre,
            precioUnitario: precio,
            cantidad: cantidad,
            subtotal: precio * cantidad,
            stockDisponible: stockDisponible
        });
    }

    renderizarDetalles();
    select.value = '';
    cantidadInput.value = 1;
}

function quitarProducto(index) {
    detallesPedido.splice(index, 1);
    renderizarDetalles();
}

function renderizarDetalles() {
    const tbody = document.getElementById('detallesTableBody');
    const totalEl = document.getElementById('pedidoTotal');
    if (!tbody) return;

    if (detallesPedido.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">Agrega productos al pedido</td></tr>';
        if (totalEl) totalEl.textContent = 'S/ 0.00';
        return;
    }

    let total = 0;
    tbody.innerHTML = detallesPedido.map((d, i) => {
        total += d.subtotal;
        return `<tr>
            <td>${d.nombre}</td>
            <td>S/ ${d.precioUnitario.toFixed(2)}</td>
            <td>${d.cantidad}</td>
            <td>S/ ${d.subtotal.toFixed(2)}</td>
            <td><button class="btn btn-sm btn-outline-danger" onclick="MeseroPedidos.quitarProducto(${i})"><i class="bi bi-trash"></i></button></td>
        </tr>`;
    }).join('');

    if (totalEl) totalEl.textContent = `S/ ${total.toFixed(2)}`;
}

async function guardarPedido() {
    const mesaId = document.getElementById('pedidoMesa').value;

    if (!mesaId) {
        showNotification('Selecciona una mesa', 'error');
        return;
    }
    if (detallesPedido.length === 0) {
        showNotification('Agrega al menos un producto', 'error');
        return;
    }

    const total = detallesPedido.reduce((sum, d) => sum + d.subtotal, 0);
    const codigo = generarCodigo();

    // Formatear fecha como LocalDateTime (sin zona horaria)
    const now = new Date();
    const fechaLocal = now.getFullYear() + '-' +
        String(now.getMonth() + 1).padStart(2, '0') + '-' +
        String(now.getDate()).padStart(2, '0') + 'T' +
        String(now.getHours()).padStart(2, '0') + ':' +
        String(now.getMinutes()).padStart(2, '0') + ':' +
        String(now.getSeconds()).padStart(2, '0');

    const pedidoData = {
        idUsuario: meseroId,
        idMesa: parseInt(mesaId),
        codigo: codigo,
        estadoPedido: 'PENDIENTE',
        total: total,
        fechaPedido: fechaLocal,
        estadoBD: 'ACTIVO'
    };

    try {
        // Crear el pedido
        const response = await fetch('/pedido', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(pedidoData)
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || 'Error al crear el pedido');
        }

        const pedidoCreado = await response.json();

        // Crear los detalles del pedido
        for (const detalle of detallesPedido) {
            const detalleRes = await fetch('/detalle-pedido', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    idPedido: pedidoCreado.idPedido,
                    idProducto: detalle.idProducto,
                    cantidad: detalle.cantidad,
                    precioUnitario: detalle.precioUnitario,
                    subtotal: detalle.subtotal
                })
            });
            if (!detalleRes.ok) {
                const errDetalle = await detalleRes.json().catch(() => ({}));
                console.error('Error al guardar detalle:', errDetalle);
                throw new Error(errDetalle.message || 'Error al guardar los productos del pedido');
            }
        }

        showNotification('Pedido creado exitosamente', 'success');
        bootstrap.Modal.getInstance(document.getElementById('pedidoModal')).hide();
        cargarPedidos();
    } catch (error) {
        console.error('Error guardando pedido:', error);
        showNotification(error.message || 'Error al guardar el pedido', 'error');
    }
}

// Configuración de estilos para cada estado
const estadoConfig = {
    'EN_PREPARACION': {
        texto: 'En Preparación',
        icon: 'bi-fire',
        btnClass: 'btn-info',
        bgClass: 'bg-info',
        mensaje: '¡Pedido enviado a cocina!',
        descripcion: 'El equipo de cocina comenzará a preparar los productos.'
    },
    'SERVIDO': {
        texto: 'Servido',
        icon: 'bi-check-circle',
        btnClass: 'btn-success',
        bgClass: 'bg-success',
        mensaje: '¡Pedido servido!',
        descripcion: 'Los productos han sido entregados al cliente.'
    },
    'PAGADO': {
        texto: 'Pagado',
        icon: 'bi-cash-coin',
        btnClass: 'btn-secondary',
        bgClass: 'bg-secondary',
        mensaje: '¡Pago registrado!',
        descripcion: 'El pedido ha sido completado exitosamente.'
    },
    'CANCELADO': {
        texto: 'Cancelado',
        icon: 'bi-x-circle',
        btnClass: 'btn-danger',
        bgClass: 'bg-danger',
        mensaje: 'Pedido cancelado',
        descripcion: 'El pedido ha sido cancelado y el stock fue devuelto.'
    }
};

async function cambiarEstado(idPedido, nuevoEstado) {
    const config = estadoConfig[nuevoEstado];
    if (!config) return;

    // Buscar el pedido para obtener su código
    const pedido = pedidosData.find(p => p.idPedido === idPedido);
    const codigoPedido = pedido ? pedido.codigo : `#${idPedido}`;

    // Mostrar modal de confirmación con Bootstrap
    const confirmed = await mostrarModalConfirmacion(codigoPedido, config, nuevoEstado);
    if (!confirmed) return;

    // Mostrar estado de carga en el botón
    const btnOriginal = event?.target;
    let btnHtmlOriginal = '';
    if (btnOriginal) {
        btnHtmlOriginal = btnOriginal.innerHTML;
        btnOriginal.disabled = true;
        btnOriginal.innerHTML = '<span class="spinner-border spinner-border-sm" role="status"></span>';
    }

    try {
        const response = await fetch(`/pedido/${idPedido}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ estadoPedido: nuevoEstado })
        });

        if (!response.ok) {
            const err = await response.json();
            throw new Error(err.message || 'Error al cambiar estado');
        }

        // Notificación de éxito con estilo según el estado
        showNotificationEstado(codigoPedido, config);
        cargarPedidos();
        // Recargar productos para actualizar stock (en caso de cancelación)
        if (nuevoEstado === 'CANCELADO') {
            cargarProductos();
        }
    } catch (error) {
        console.error('Error cambiando estado:', error);
        showNotification(error.message || 'Error al cambiar estado', 'error');
    } finally {
        // Restaurar botón
        if (btnOriginal) {
            btnOriginal.disabled = false;
            btnOriginal.innerHTML = btnHtmlOriginal;
        }
    }
}

function mostrarModalConfirmacion(codigoPedido, config, nuevoEstado) {
    return new Promise((resolve) => {
        // Crear modal dinámicamente
        const modalId = 'confirmEstadoModal';
        let modal = document.getElementById(modalId);
        
        // Remover modal anterior si existe
        if (modal) modal.remove();

        const esCancelacion = nuevoEstado === 'CANCELADO';
        const headerClass = esCancelacion ? 'bg-danger text-white' : config.bgClass + ' text-white';
        const btnConfirmClass = esCancelacion ? 'btn-danger' : config.btnClass;

        const modalHtml = `
            <div class="modal fade" id="${modalId}" tabindex="-1" data-bs-backdrop="static">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header ${headerClass}">
                            <h5 class="modal-title">
                                <i class="bi ${config.icon} me-2"></i>
                                Confirmar cambio de estado
                            </h5>
                            <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                        </div>
                        <div class="modal-body text-center py-4">
                            <div class="mb-3">
                                <span class="badge ${config.bgClass} fs-6 px-3 py-2">
                                    <i class="bi ${config.icon} me-1"></i> ${config.texto}
                                </span>
                            </div>
                            <p class="mb-1">¿Cambiar el estado del pedido <strong>${codigoPedido}</strong>?</p>
                            <p class="text-muted small mb-0">${config.descripcion}</p>
                        </div>
                        <div class="modal-footer justify-content-center">
                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal" id="btnCancelarConfirm">
                                <i class="bi bi-arrow-left me-1"></i> Volver
                            </button>
                            <button type="button" class="btn ${btnConfirmClass}" id="btnConfirmarEstado">
                                <i class="bi ${config.icon} me-1"></i> Confirmar
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        `;

        document.body.insertAdjacentHTML('beforeend', modalHtml);
        modal = document.getElementById(modalId);
        const bsModal = new bootstrap.Modal(modal);

        // Manejar confirmación
        document.getElementById('btnConfirmarEstado').onclick = () => {
            bsModal.hide();
            resolve(true);
        };

        // Manejar cancelación
        document.getElementById('btnCancelarConfirm').onclick = () => {
            resolve(false);
        };
        modal.addEventListener('hidden.bs.modal', () => {
            modal.remove();
        });

        bsModal.show();
    });
}

function showNotificationEstado(codigoPedido, config) {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) return;

    const alertId = 'alert-' + Date.now();
    const alertType = config.bgClass.replace('bg-', '');
    
    const alertHTML = `
        <div id="${alertId}" class="alert alert-${alertType} alert-dismissible fade show shadow" role="alert">
            <div class="d-flex align-items-center">
                <i class="bi ${config.icon} fs-4 me-3"></i>
                <div>
                    <strong>${config.mensaje}</strong>
                    <div class="small">Pedido ${codigoPedido} → ${config.texto}</div>
                </div>
            </div>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    `;

    alertContainer.insertAdjacentHTML('beforeend', alertHTML);

    // Auto-remover después de 4 segundos
    setTimeout(() => {
        const alert = document.getElementById(alertId);
        if (alert) {
            alert.classList.add('fade-out');
            setTimeout(() => {
                const bsAlert = bootstrap.Alert.getOrCreateInstance(alert);
                bsAlert?.close();
            }, 300);
        }
    }, 4000);
}

function cancelarPedido(idPedido) {
    cambiarEstado(idPedido, 'CANCELADO');
}

async function verDetalle(idPedido) {
    try {
        const [pedidoRes, detallesRes] = await Promise.all([
            fetch(`/pedido/${idPedido}`),
            fetch(`/detalle-pedido/pedido/${idPedido}`)
        ]);

        if (!pedidoRes.ok) throw new Error('No se pudo obtener el pedido');
        if (!detallesRes.ok) throw new Error('No se pudo obtener los productos del pedido');

        const pedido   = await pedidoRes.json();
        const detalles = await detallesRes.json();

        let detallesHtml = '';
        if (Array.isArray(detalles) && detalles.length > 0) {
            detallesHtml = `<table class="table table-sm">
                <thead><tr><th>Producto</th><th>Cant.</th><th>P.Unit.</th><th>Subtotal</th></tr></thead>
                <tbody>${detalles.map(d => `<tr>
                    <td>${d.productoNombre}</td>
                    <td>${d.cantidad}</td>
                    <td>S/ ${d.precioUnitario.toFixed(2)}</td>
                    <td>S/ ${d.subtotal.toFixed(2)}</td>
                </tr>`).join('')}</tbody>
            </table>`;
        } else {
            detallesHtml = '<p class="text-muted">Sin productos registrados</p>';
        }

        const content = document.getElementById('detallePedidoContent');
        const fecha = pedido.fechaPedido ? new Date(pedido.fechaPedido).toLocaleString('es-PE') : '-';
        content.innerHTML = `
            <div class="mb-3">
                <strong>Código:</strong> ${pedido.codigo}<br>
                <strong>Mesa:</strong> ${pedido.numeroMesa}<br>
                <strong>Cliente:</strong> ${pedido.clienteNombre || 'Consumidor Final'}<br>
                <strong>Estado:</strong> ${getEstadoBadge(pedido.estadoPedido)}<br>
                <strong>Fecha:</strong> ${fecha}<br>
                <strong>Total:</strong> <span class="fw-bold">S/ ${pedido.total ? pedido.total.toFixed(2) : '0.00'}</span>
            </div>
            <h6>Productos:</h6>
            ${detallesHtml}
        `;

        new bootstrap.Modal(document.getElementById('detallePedidoModal')).show();
    } catch (error) {
        console.error('Error cargando detalle:', error);
        showNotification('Error al cargar detalle del pedido', 'error');
    }
}

function showNotification(message, type = 'info') {
    const alertContainer = document.getElementById('alertContainer');
    if (!alertContainer) {
        console.error('Alert container not found');
        return;
    }

    // Mapear tipos
    const alertType = type === 'error' ? 'danger' : type === 'success' ? 'success' : 'info';
    const icon = type === 'success' ? 'check-circle-fill' : type === 'error' ? 'exclamation-triangle-fill' : 'info-circle-fill';

    // Crear el alert
    const alertId = 'alert-' + Date.now();
    const alertHTML = `
        <div id="${alertId}" class="alert alert-${alertType} alert-dismissible fade show shadow-sm" role="alert">
            <i class="bi bi-${icon} me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    `;

    alertContainer.insertAdjacentHTML('beforeend', alertHTML);

    // Auto-remover después de 5 segundos
    setTimeout(() => {
        const alert = document.getElementById(alertId);
        if (alert) {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }
    }, 5000);
}

// Exportar funciones
window.MeseroPedidos = {
    cargarPedidos,
    filtrarPedidos,
    limpiarFiltros,
    abrirNuevoPedido,
    agregarProducto,
    quitarProducto,
    guardarPedido,
    cambiarEstado,
    cancelarPedido,
    verDetalle,
    showNotification
};


