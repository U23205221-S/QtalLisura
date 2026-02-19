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
        select.innerHTML += `<option value="${prod.idProducto}" data-precio="${prod.precioVenta}" data-nombre="${prod.nombre}">
            ${prod.nombre} - S/ ${prod.precioVenta.toFixed(2)}
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
            <td><span class="fw-bold">${pedido.codigo}</span></td>
            <td><i class="bi bi-diagram-3 me-1"></i>Mesa ${pedido.numeroMesa}</td>
            <td>${pedido.clienteNombre || 'Consumidor Final'}</td>
            <td>${estadoBadge}</td>
            <td class="fw-bold">S/ ${pedido.total ? pedido.total.toFixed(2) : '0.00'}</td>
            <td>${fecha}</td>
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
        btns += `<button class="btn btn-sm btn-outline-danger me-1" onclick="MeseroPedidos.cancelarPedido(${pedido.idPedido})" title="Cancelar">
            <i class="bi bi-x-circle"></i>
        </button>`;
    } else if (estado === 'En Preparación') {
        btns += `<button class="btn btn-sm btn-outline-success me-1" onclick="MeseroPedidos.cambiarEstado(${pedido.idPedido}, 'SERVIDO')" title="Marcar como servido">
            <i class="bi bi-check-circle"></i>
        </button>`;
    } else if (estado === 'Servido') {
        btns += `<button class="btn btn-sm btn-outline-secondary me-1" onclick="MeseroPedidos.cambiarEstado(${pedido.idPedido}, 'PAGADO')" title="Marcar como pagado">
            <i class="bi bi-cash-coin"></i>
        </button>`;
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

    // Verificar si ya existe
    const existente = detallesPedido.find(d => d.idProducto === productoId);
    if (existente) {
        existente.cantidad += cantidad;
        existente.subtotal = existente.precioUnitario * existente.cantidad;
    } else {
        detallesPedido.push({
            idProducto: productoId,
            nombre: nombre,
            precioUnitario: precio,
            cantidad: cantidad,
            subtotal: precio * cantidad
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

    const pedidoData = {
        idUsuario: meseroId,
        idCliente: null,
        idMesa: parseInt(mesaId),
        codigo: codigo,
        estadoPedido: 'PENDIENTE',
        total: total,
        fechaPedido: new Date().toISOString(),
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
            await fetch('/detalle-pedido', {
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
        }

        showNotification('Pedido creado exitosamente', 'success');
        bootstrap.Modal.getInstance(document.getElementById('pedidoModal')).hide();
        cargarPedidos();
    } catch (error) {
        console.error('Error guardando pedido:', error);
        showNotification(error.message || 'Error al guardar el pedido', 'error');
    }
}

async function cambiarEstado(idPedido, nuevoEstado) {
    const estadoTexto = {
        'EN_PREPARACION': 'En Preparación',
        'SERVIDO': 'Servido',
        'PAGADO': 'Pagado',
        'CANCELADO': 'Cancelado'
    };

    if (!confirm(`¿Cambiar el estado del pedido a "${estadoTexto[nuevoEstado]}"?`)) return;

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

        showNotification(`Estado cambiado a ${estadoTexto[nuevoEstado]}`, 'success');
        cargarPedidos();
    } catch (error) {
        console.error('Error cambiando estado:', error);
        showNotification(error.message || 'Error al cambiar estado', 'error');
    }
}

function cancelarPedido(idPedido) {
    cambiarEstado(idPedido, 'CANCELADO');
}

async function verDetalle(idPedido) {
    try {
        const response = await fetch(`/pedido/${idPedido}`);
        const pedido = await response.json();

        const detallesResp = await fetch('/detalle-pedido');
        const todosDetalles = await detallesResp.json();
        const detalles = Array.isArray(todosDetalles) ? todosDetalles.filter(d => d.idPedido === idPedido) : [];

        let detallesHtml = '';
        if (detalles.length > 0) {
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
    const toast = document.createElement('div');
    toast.className = `alert alert-${type === 'success' ? 'success' : type === 'error' ? 'danger' : 'info'} position-fixed`;
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    toast.innerHTML = `<i class="bi bi-${type === 'success' ? 'check-circle' : 'x-circle'}"></i> ${message}`;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
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


