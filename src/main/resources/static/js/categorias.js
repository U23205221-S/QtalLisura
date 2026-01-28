/* GastroTech - JavaScript para módulo de Categorías */

let categoriasData = [];
let categoriaModal;

document.addEventListener('DOMContentLoaded', function() {
    categoriaModal = new bootstrap.Modal(document.getElementById('categoriaModal'));
    cargarCategorias();
    setupFilters();
});

// Cargar categorías
async function cargarCategorias() {
    try {
        const response = await fetch('/categoria');
        if (response.ok) {
            categoriasData = await response.json();
            renderCategorias(categoriasData);
        } else {
            showNotification('Error al cargar categorías', 'error');
        }
    } catch (error) {
        console.error('Error al cargar categorías:', error);
        showNotification('Error de conexión', 'error');
    }
}

// Renderizar tabla de categorías
function renderCategorias(categorias) {
    const tbody = document.getElementById('categoriasTableBody');
    if (!tbody) return;

    if (!categorias || categorias.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" class="text-center py-4">No hay categorías registradas</td></tr>';
        return;
    }

    tbody.innerHTML = categorias.map(categoria => `
        <tr>
            <td>${categoria.idCategoria}</td>
            <td>${categoria.nombre}</td>
            <td>${categoria.descripcion}</td>
            <td>
                <span class="status-badge ${categoria.estadoBD && categoria.estadoBD.toLowerCase() === 'activo' ? 'active' : 'inactive'}">
                    ${categoria.estadoBD}
                </span>
            </td>
            <td>
                <div class="action-btns">
                    <button class="action-btn view" onclick="verCategoria(${categoria.idCategoria})" title="Ver detalles">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="action-btn edit" onclick="editarCategoria(${categoria.idCategoria})" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="action-btn delete" onclick="eliminarCategoria(${categoria.idCategoria})" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Abrir modal para crear/editar
function openCategoriaModal(action, id = null) {
    const form = document.getElementById('categoriaForm');
    const title = document.getElementById('categoriaModalTitle');

    form.reset();
    form.classList.remove('was-validated');

    if (action === 'create') {
        title.textContent = 'Nueva Categoría';
        document.getElementById('categoriaId').value = '';
    } else if (action === 'edit' && id) {
        title.textContent = 'Editar Categoría';
        cargarDatosCategoria(id);
    }

    categoriaModal.show();
}

// Cargar datos de la categoría
async function cargarDatosCategoria(id) {
    try {
        const response = await fetch(`/categoria/${id}`);
        if (response.ok) {
            const categoria = await response.json();
            document.getElementById('categoriaId').value = categoria.idCategoria;
            document.getElementById('categoriaNombre').value = categoria.nombre;
            document.getElementById('categoriaDescripcion').value = categoria.descripcion;
            document.getElementById('categoriaEstado').value = categoria.estadoBD;
        }
    } catch (error) {
        console.error('Error al cargar categoría:', error);
        showNotification('Error al cargar datos de la categoría', 'error');
    }
}

// Editar categoría
function editarCategoria(id) {
    openCategoriaModal('edit', id);
}

// Ver categoría
function verCategoria(id) {
    const categoria = categoriasData.find(c => c.idCategoria === id);
    if (categoria) {
        alert(`Categoría: ${categoria.nombre}\nDescripción: ${categoria.descripcion}\nEstado: ${categoria.estadoBD}`);
    }
}

// Guardar categoría
async function saveCategoria() {
    const form = document.getElementById('categoriaForm');

    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        showNotification('Por favor complete todos los campos requeridos', 'error');
        return;
    }

    const id = document.getElementById('categoriaId').value;
    const data = {
        nombre: document.getElementById('categoriaNombre').value,
        descripcion: document.getElementById('categoriaDescripcion').value,
        estadoBD: document.getElementById('categoriaEstado').value
    };

    try {
        const url = id ? `/categoria/${id}` : '/categoria';
        const method = id ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            showNotification(`Categoría ${id ? 'actualizada' : 'creada'} exitosamente`, 'success');
            categoriaModal.hide();
            cargarCategorias();
        } else {
            const error = await response.text();
            showNotification(`Error: ${error}`, 'error');
        }
    } catch (error) {
        console.error('Error al guardar categoría:', error);
        showNotification('Error al guardar la categoría', 'error');
    }
}

// Eliminar categoría
async function eliminarCategoria(id) {
    if (!confirm('¿Está seguro de eliminar esta categoría?')) return;

    try {
        const response = await fetch(`/categoria/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Categoría eliminada exitosamente', 'success');
            cargarCategorias();
        } else {
            showNotification('Error al eliminar categoría', 'error');
        }
    } catch (error) {
        console.error('Error al eliminar categoría:', error);
        showNotification('Error al eliminar categoría', 'error');
    }
}

// Configurar filtros
function setupFilters() {
    const searchInput = document.getElementById('searchCategoria');
    const filterEstado = document.getElementById('filterEstado');

    [searchInput, filterEstado].forEach(element => {
        if (element) {
            element.addEventListener('change', aplicarFiltros);
            element.addEventListener('keyup', aplicarFiltros);
        }
    });
}

// Aplicar filtros
function aplicarFiltros() {
    const searchText = document.getElementById('searchCategoria')?.value.toLowerCase() || '';
    const filterEstado = document.getElementById('filterEstado')?.value || '';

    const filtrados = categoriasData.filter(categoria => {
        const matchSearch = !searchText ||
            categoria.nombre.toLowerCase().includes(searchText) ||
            categoria.descripcion.toLowerCase().includes(searchText);

        const matchEstado = !filterEstado || categoria.estadoBD === filterEstado;

        return matchSearch && matchEstado;
    });

    renderCategorias(filtrados);
}

// Limpiar filtros
function clearFilters() {
    document.getElementById('searchCategoria').value = '';
    document.getElementById('filterEstado').value = '';
    renderCategorias(categoriasData);
}

// Exportar categorías
function exportCategorias() {
    showNotification('Función de exportación en desarrollo', 'info');
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

