/* Q'Tal Lisura - JavaScript para Pedidos en Admin */

let pedidosData = [];

document.addEventListener('DOMContentLoaded', function() {
    cargarPedidos();
});

async function cargarPedidos() {
    try {
        const response = await fetch('/pedido');
        pedidosData = await response.json();
        renderizarPedidos(pedidosData);
        actualizarStats(pedidosData);
    } catch (error) {
        console.error('Error cargando pedidos:', error);
        showNotification('Error al cargar los pedidos', 'error');
    }
}

function renderizarPedidos(pedidos) {
    const tbody = document.getElementById('pedidosTableBody');
    if (!tbody) return;

    if (!pedidos || pedidos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted py-4">No hay pedidos registrados</td></tr>';
        return;
    }

    tbody.innerHTML = pedidos.map(pedido => {
        const estadoBadge = getEstadoBadge(pedido.estadoPedido);
        const fecha = pedido.fechaPedido ? new Date(pedido.fechaPedido).toLocaleString('es-PE', { dateStyle: 'short', timeStyle: 'short' }) : '-';
        return `<tr>
            <td><span class="fw-bold">${pedido.codigo}</span></td>
            <td>${pedido.usuarioNombre || '-'}</td>
            <td><i class="bi bi-diagram-3 me-1"></i>Mesa ${pedido.numeroMesa}</td>
            <td>${pedido.clienteNombre || 'Consumidor Final'}</td>
            <td>${estadoBadge}</td>
            <td class="fw-bold">S/ ${pedido.total ? pedido.total.toFixed(2) : '0.00'}</td>
            <td>${fecha}</td>
            <td>
                <button class="btn btn-sm btn-outline-primary" onclick="AdminPedidos.verDetalle(${pedido.idPedido})" title="Ver detalle">
                    <i class="bi bi-eye"></i>
                </button>
            </td>
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
    const mesero = (document.getElementById('filterMesero')?.value || '').toLowerCase();

    const filtrados = pedidosData.filter(p => {
        const matchSearch = !search ||
            p.codigo.toLowerCase().includes(search) ||
            String(p.numeroMesa).includes(search) ||
            (p.clienteNombre || '').toLowerCase().includes(search) ||
            (p.usuarioNombre || '').toLowerCase().includes(search);
        const matchEstado = !estado || p.estadoPedido === estado;
        const matchFecha = !fecha || (p.fechaPedido && p.fechaPedido.startsWith(fecha));
        const matchMesero = !mesero || (p.usuarioNombre || '').toLowerCase().includes(mesero);
        return matchSearch && matchEstado && matchFecha && matchMesero;
    });
    renderizarPedidos(filtrados);
}

function limpiarFiltros() {
    const el = id => document.getElementById(id);
    if (el('searchPedido')) el('searchPedido').value = '';
    if (el('filterEstadoPedido')) el('filterEstadoPedido').value = '';
    if (el('filterFechaPedido')) el('filterFechaPedido').value = '';
    if (el('filterMesero')) el('filterMesero').value = '';
    renderizarPedidos(pedidosData);
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
                <strong>Mesero:</strong> ${pedido.usuarioNombre || '-'}<br>
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

window.AdminPedidos = {
    cargarPedidos,
    filtrarPedidos,
    limpiarFiltros,
    verDetalle,
    showNotification
};

