/* GastroTech - JavaScript para Admin */
document.addEventListener('DOMContentLoaded', function() {
    initSidebar();
    initModals();
    initTables();
});

function initSidebar() {
    const sidebar = document.querySelector('.admin-sidebar');
    const toggleBtn = document.querySelector('.toggle-sidebar');
    const overlay = document.querySelector('.sidebar-overlay');

    if (toggleBtn) {
        toggleBtn.addEventListener('click', function() {
            if (window.innerWidth <= 992) {
                sidebar.classList.toggle('active');
                overlay?.classList.toggle('active');
            } else {
                sidebar.classList.toggle('collapsed');
            }
        });
    }

    if (overlay) {
        overlay.addEventListener('click', function() {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
        });
    }
}

function initModals() {
    const productoModal = document.getElementById('productoModal');
    if (productoModal) {
        productoModal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const action = button?.getAttribute('data-action');
            const modalTitle = productoModal.querySelector('.modal-title');
            const form = productoModal.querySelector('form');

            if (action === 'create') {
                modalTitle.textContent = 'Nuevo Producto';
                form?.reset();
            } else if (action === 'edit') {
                modalTitle.textContent = 'Editar Producto';
                const productoId = button.getAttribute('data-id');
                loadProductoData(productoId);
            }
        });
    }
}

function loadProductoData(id) {
    fetch(`/producto/${id}`)
        .then(response => response.json())
        .then(data => {
            const form = document.querySelector('#productoModal form');
            if (form) {
                form.querySelector('#productoId').value = data.idProducto || '';
                form.querySelector('#productoNombre').value = data.nombre || '';
                form.querySelector('#productoDescripcion').value = data.descripcion || '';
                form.querySelector('#productoPrecio').value = data.precioVenta || '';
                form.querySelector('#productoStock').value = data.stockActual || '';
            }
        })
        .catch(error => console.log('Error cargando producto:', error));
}

function saveProducto() {
    const form = document.querySelector('#productoModal form');
    const formData = new FormData(form);
    const data = Object.fromEntries(formData);
    const isEdit = data.id && data.id !== '';

    fetch(isEdit ? `/producto/${data.id}` : '/producto', {
        method: isEdit ? 'PUT' : 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(() => {
        showNotification('Producto guardado exitosamente', 'success');
        bootstrap.Modal.getInstance(document.getElementById('productoModal')).hide();
        location.reload();
    })
    .catch(() => showNotification('Error al guardar el producto', 'error'));
}

function deleteProducto(id) {
    if (confirm('¿Está seguro de eliminar este producto?')) {
        fetch(`/producto/${id}`, { method: 'DELETE' })
            .then(() => {
                showNotification('Producto eliminado', 'success');
                location.reload();
            })
            .catch(() => showNotification('Error al eliminar', 'error'));
    }
}

function initTables() {
    document.querySelectorAll('.table-gastro th[data-sort]').forEach(header => {
        header.style.cursor = 'pointer';
        header.addEventListener('click', function() {
            const table = this.closest('table');
            const index = this.cellIndex;
            sortTable(table, index);
        });
    });
}

function sortTable(table, columnIndex) {
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const isAsc = table.getAttribute('data-sort-dir') === 'asc';

    rows.sort((a, b) => {
        const aVal = a.cells[columnIndex]?.textContent.trim() || '';
        const bVal = b.cells[columnIndex]?.textContent.trim() || '';
        return isAsc ? aVal.localeCompare(bVal) : bVal.localeCompare(aVal);
    });

    rows.forEach(row => tbody.appendChild(row));
    table.setAttribute('data-sort-dir', isAsc ? 'desc' : 'asc');
}

function showNotification(message, type = 'info') {
    const toast = document.createElement('div');
    toast.className = `alert alert-${type === 'success' ? 'success' : type === 'error' ? 'danger' : 'info'} position-fixed`;
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    toast.innerHTML = `<i class="bi bi-${type === 'success' ? 'check-circle' : 'x-circle'}"></i> ${message}`;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

function previewImage(input, previewId) {
    const preview = document.getElementById(previewId);
    if (input.files && input.files[0] && preview) {
        const reader = new FileReader();
        reader.onload = e => { preview.src = e.target.result; preview.style.display = 'block'; };
        reader.readAsDataURL(input.files[0]);
    }
}

window.GastroAdmin = { saveProducto, deleteProducto, showNotification, previewImage };
