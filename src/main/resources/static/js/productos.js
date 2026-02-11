/* GastroTech - JavaScript para módulo de Productos */

let productosData = [];
let categoriasData = [];
let productoModal;
let verProductoModal;
let confirmarEliminarProductoModal;
let productoAEliminar = null;

document.addEventListener('DOMContentLoaded', function() {
    productoModal = new bootstrap.Modal(document.getElementById('productoModal'));
    verProductoModal = new bootstrap.Modal(document.getElementById('verProductoModal'));
    confirmarEliminarProductoModal = new bootstrap.Modal(document.getElementById('confirmarEliminarProductoModal'));
    
    // Configurar botón de confirmación de eliminación
    document.getElementById('btnConfirmarEliminarProducto').addEventListener('click', confirmarEliminarProducto);
    
    cargarCategorias();
    cargarProductos();
    setupFilters();
    setupModalHandlers();
});

// Cargar categorías
async function cargarCategorias() {
    try {
        const response = await fetch('/categoria');
        if (response.ok) {
            categoriasData = await response.json();
            const selects = ['#productoCategoria', '#filterCategoria'];
            selects.forEach(selectId => {
                const select = document.querySelector(selectId);
                if (select) {
                    // Limpiar opciones existentes excepto la primera
                    while (select.options.length > 1) {
                        select.remove(1);
                    }
                    categoriasData.forEach(categoria => {
                        const option = document.createElement('option');
                        option.value = categoria.idCategoria;
                        option.textContent = categoria.nombre;
                        select.appendChild(option);
                    });
                }
            });
        }
    } catch (error) {
        console.error('Error al cargar categorías:', error);
    }
}

// Cargar productos
async function cargarProductos() {
    try {
        const response = await fetch('/producto');
        if (response.ok) {
            productosData = await response.json();
            renderProductos(productosData);
        } else {
            showNotification('Error al cargar productos', 'error');
        }
    } catch (error) {
        console.error('Error al cargar productos:', error);
        showNotification('Error de conexión', 'error');
    }
}

// Renderizar tabla de productos
function renderProductos(productos) {
    const tbody = document.querySelector('#productosTable tbody, .table-gastro tbody');
    if (!tbody) return;

    if (!productos || productos.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center py-4">No hay productos registrados</td></tr>';
        return;
    }

    tbody.innerHTML = productos.map(producto => `
        <tr>
            <td>${producto.idProducto}</td>
            <td>
                <div class="product-cell">
                    <img src="${producto.imagenUrl ? '/uploads/products/' + producto.imagenUrl : 'https://via.placeholder.com/50'}" 
                         alt="${producto.nombre}" 
                         onerror="this.src='https://via.placeholder.com/50'">
                    <div class="product-info">
                        <h6>${producto.nombre}</h6>
                        <span>${producto.descripcion.substring(0, 30)}...</span>
                    </div>
                </div>
            </td>
            <td>${producto.categoriaNombre || 'Sin categoría'}</td>
            <td>S/ ${Number(producto.precioVenta).toFixed(2)}</td>
            <td>
                <span class="${producto.stockActual <= producto.stockMinimo ? 'text-danger fw-bold' : ''}">
                    ${producto.stockActual}
                </span>
            </td>
            <td>
                <div class="form-check form-switch d-flex align-items-center justify-content-center">
                    <input class="form-check-input" type="checkbox" role="switch" 
                           id="switchProducto${producto.idProducto}"
                           ${producto.estadoBD === 'ACTIVO' ? 'checked' : ''}
                           onchange="toggleEstadoProducto(${producto.idProducto}, this.checked)"
                           title="${producto.estadoBD === 'ACTIVO' ? 'Desactivar' : 'Activar'} producto">
                </div>
            </td>
            <td>
                <div class="action-btns">
                    <button class="action-btn view" onclick="verProducto(${producto.idProducto})" title="Ver detalles">
                        <i class="bi bi-eye"></i>
                    </button>
                    <button class="action-btn edit" onclick="editarProducto(${producto.idProducto})" title="Editar">
                        <i class="bi bi-pencil"></i>
                    </button>
                    <button class="action-btn delete" onclick="deleteProducto(${producto.idProducto})" title="Eliminar">
                        <i class="bi bi-trash"></i>
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}

// Configurar manejadores del modal
function setupModalHandlers() {
    const modal = document.getElementById('productoModal');
    if (modal) {
        modal.addEventListener('show.bs.modal', function(event) {
            const button = event.relatedTarget;
            const action = button?.getAttribute('data-action');
            const id = button?.getAttribute('data-id');

            if (action === 'create') {
                openProductoModal('create');
            } else if (action === 'edit' && id) {
                editarProducto(id);
            }
        });
    }
}

// Abrir modal
function openProductoModal(action, id = null) {
    const form = document.getElementById('productoForm');
    const title = document.getElementById('productoModalTitle');

    form.reset();
    form.classList.remove('was-validated');
    document.getElementById('productoImgPreview').src = 'https://via.placeholder.com/200x200?text=Imagen';

    if (action === 'create') {
        title.textContent = 'Nuevo Producto';
        document.getElementById('productoId').value = '';
    } else if (action === 'edit' && id) {
        title.textContent = 'Editar Producto';
        cargarDatosProducto(id);
    }

    productoModal.show();
}

// Cargar datos del producto
async function cargarDatosProducto(id) {
    try {
        const response = await fetch(`/producto/${id}`);
        if (response.ok) {
            const producto = await response.json();
            document.getElementById('productoId').value = producto.idProducto;
            document.getElementById('productoNombre').value = producto.nombre;
            document.getElementById('productoDescripcion').value = producto.descripcion;
            document.getElementById('productoCategoria').value = producto.idCategoria;
            document.getElementById('productoPrecio').value = producto.precioVenta;
            document.getElementById('productoStock').value = producto.stockActual;
            document.getElementById('productoStockMin').value = producto.stockMinimo;
            document.getElementById('productoCosto').value = producto.costoUnitario;

            if (producto.imagenUrl) {
                document.getElementById('productoImgPreview').src = '/uploads/products/' + producto.imagenUrl;
            }
        }
    } catch (error) {
        console.error('Error al cargar producto:', error);
        showNotification('Error al cargar datos del producto', 'error');
    }
}

// Editar producto
function editarProducto(id) {
    openProductoModal('edit', id);
}

// Ver producto (modal Bootstrap)
function verProducto(id) {
    const producto = productosData.find(p => p.idProducto == id);
    if (producto) {
        document.getElementById('verProductoImagen').src = producto.imagenUrl 
            ? '/uploads/products/' + producto.imagenUrl 
            : 'https://via.placeholder.com/150';
        document.getElementById('verProductoNombre').textContent = producto.nombre;
        document.getElementById('verProductoDescripcion').textContent = producto.descripcion;
        document.getElementById('verProductoCategoria').textContent = producto.categoriaNombre || 'Sin categoría';
        document.getElementById('verProductoPrecio').textContent = `S/ ${Number(producto.precioVenta).toFixed(2)}`;
        document.getElementById('verProductoStock').textContent = producto.stockActual;
        document.getElementById('verProductoStockMin').textContent = producto.stockMinimo;
        document.getElementById('verProductoCosto').textContent = `S/ ${Number(producto.costoUnitario).toFixed(2)}`;
        
        const estadoBadge = document.getElementById('verProductoEstado');
        estadoBadge.textContent = producto.estadoBD === 'ACTIVO' ? 'Activo' : 'Inactivo';
        estadoBadge.className = `badge ${producto.estadoBD === 'ACTIVO' ? 'bg-success' : 'bg-secondary'}`;
        
        verProductoModal.show();
    }
}

// Toggle estado del producto
async function toggleEstadoProducto(id, activo) {
    const nuevoEstado = activo ? 'ACTIVO' : 'INACTIVO';
    
    try {
        // Solo enviamos el estadoBD, el backend acepta campos opcionales
        const formData = new FormData();
        formData.append('estadoBD', nuevoEstado);

        const response = await fetch(`/producto/${id}`, {
            method: 'PUT',
            body: formData
        });

        if (response.ok) {
            showNotification(`Producto ${activo ? 'activado' : 'desactivado'} exitosamente`, 'success');
            cargarProductos();
        } else {
            showNotification('Error al cambiar estado', 'error');
            // Revertir el switch
            document.getElementById(`switchProducto${id}`).checked = !activo;
        }
    } catch (error) {
        console.error('Error al cambiar estado:', error);
        showNotification('Error al cambiar estado', 'error');
        document.getElementById(`switchProducto${id}`).checked = !activo;
    }
}

// Guardar producto
async function saveProducto() {
    const form = document.getElementById('productoForm');

    if (!form.checkValidity()) {
        form.classList.add('was-validated');
        showNotification('Por favor complete todos los campos requeridos', 'error');
        return;
    }

    const formData = new FormData();
    const id = document.getElementById('productoId').value;

    formData.append('nombre', document.getElementById('productoNombre').value);
    formData.append('descripcion', document.getElementById('productoDescripcion').value);
    formData.append('idCategoria', document.getElementById('productoCategoria').value);
    formData.append('precioVenta', document.getElementById('productoPrecio').value);
    formData.append('stockActual', document.getElementById('productoStock').value);
    formData.append('stockMinimo', document.getElementById('productoStockMin').value);
    formData.append('costoUnitario', document.getElementById('productoCosto').value);
    
    // Si es nuevo, estado ACTIVO por defecto; si es edición, mantener el estado actual
    if (id) {
        const producto = productosData.find(p => p.idProducto == id);
        formData.append('estadoBD', producto ? producto.estadoBD : 'ACTIVO');
    } else {
        formData.append('estadoBD', 'ACTIVO');
    }

    const imagenInput = document.getElementById('productoImagen');
    if (imagenInput.files.length > 0) {
        formData.append('imagen', imagenInput.files[0]);
    }

    try {
        const url = id ? `/producto/${id}` : '/producto';
        const method = id ? 'PUT' : 'POST';

        const response = await fetch(url, {
            method: method,
            body: formData
        });

        if (response.ok) {
            showNotification(`Producto ${id ? 'actualizado' : 'creado'} exitosamente`, 'success');
            productoModal.hide();
            cargarProductos();
        } else {
            const error = await response.text();
            showNotification(`Error: ${error}`, 'error');
        }
    } catch (error) {
        console.error('Error al guardar producto:', error);
        showNotification('Error al guardar el producto', 'error');
    }
}

// Eliminar producto (abrir modal de confirmación)
function deleteProducto(id) {
    const producto = productosData.find(p => p.idProducto == id);
    if (producto) {
        productoAEliminar = id;
        document.getElementById('eliminarProductoNombre').textContent = producto.nombre;
        confirmarEliminarProductoModal.show();
    }
}

// Confirmar eliminación del producto
async function confirmarEliminarProducto() {
    if (!productoAEliminar) return;
    
    try {
        const response = await fetch(`/producto/${productoAEliminar}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Producto eliminado exitosamente', 'success');
            confirmarEliminarProductoModal.hide();
            cargarProductos();
        } else {
            showNotification('Error al eliminar producto', 'error');
        }
    } catch (error) {
        console.error('Error al eliminar producto:', error);
        showNotification('Error al eliminar producto', 'error');
    } finally {
        productoAEliminar = null;
    }
}

// Preview de imagen
function previewImageProducto(input) {
    const preview = document.getElementById('productoImgPreview');
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
    const searchInput = document.getElementById('searchProduct');
    const filterCategoria = document.getElementById('filterCategoria');
    const filterEstado = document.getElementById('filterEstado');

    [searchInput, filterCategoria, filterEstado].forEach(element => {
        if (element) {
            element.addEventListener('change', aplicarFiltros);
            element.addEventListener('keyup', aplicarFiltros);
        }
    });
}

// Aplicar filtros
function aplicarFiltros() {
    const searchText = document.getElementById('searchProduct')?.value.toLowerCase() || '';
    const filterCategoria = document.getElementById('filterCategoria')?.value || '';
    const filterEstado = document.getElementById('filterEstado')?.value || '';

    const filtrados = productosData.filter(producto => {
        const matchSearch = !searchText ||
            producto.nombre.toLowerCase().includes(searchText) ||
            producto.descripcion.toLowerCase().includes(searchText);

        const matchCategoria = !filterCategoria || producto.idCategoria == filterCategoria;
        const matchEstado = !filterEstado || producto.estadoBD === filterEstado;

        return matchSearch && matchCategoria && matchEstado;
    });

    renderProductos(filtrados);
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
