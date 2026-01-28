/* GastroTech - JavaScript para módulo de Productos */

let productosData = [];
let categoriasData = [];
let productoModal;

document.addEventListener('DOMContentLoaded', function() {
    productoModal = new bootstrap.Modal(document.getElementById('productoModal'));
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
                <span class="status-badge ${producto.estadoBD && producto.estadoBD.toLowerCase() === 'activo' ? 'active' : 'inactive'}">
                    ${producto.estadoBD}
                </span>
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
            document.getElementById('productoEstado').value = producto.estadoBD;

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

// Ver producto
function verProducto(id) {
    const producto = productosData.find(p => p.idProducto == id);
    if (producto) {
        alert(`Producto: ${producto.nombre}\nDescripción: ${producto.descripcion}\nPrecio: S/ ${producto.precioVenta}\nStock: ${producto.stockActual}\nCategoría: ${producto.categoriaNombre}`);
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
    formData.append('estadoBD', document.getElementById('productoEstado').value);

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

// Eliminar producto
async function deleteProducto(id) {
    if (!confirm('¿Está seguro de eliminar este producto?')) return;

    try {
        const response = await fetch(`/producto/${id}`, {
            method: 'DELETE'
        });

        if (response.ok) {
            showNotification('Producto eliminado exitosamente', 'success');
            cargarProductos();
        } else {
            showNotification('Error al eliminar producto', 'error');
        }
    } catch (error) {
        console.error('Error al eliminar producto:', error);
        showNotification('Error al eliminar producto', 'error');
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

