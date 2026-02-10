/* GastroTech - JavaScript para catálogo de clientes */

let todosLosProductos = [];
let categoriaActual = 'all';

document.addEventListener('DOMContentLoaded', function() {
    cargarProductos();
    setupCategoryFilters();
    setupSearch();
});

// Cargar productos del backend
async function cargarProductos() {
    try {
        const response = await fetch('/producto');
        if (response.ok) {
            const productos = await response.json();
            // Filtrar productos activos (comparar ignorando mayúsculas/minúsculas)
            todosLosProductos = productos.filter(p =>
                p.estadoBD && p.estadoBD.toLowerCase() === 'activo'
            );
            renderizarProductos(todosLosProductos);
        }
    } catch (error) {
        console.error('Error al cargar productos:', error);
    }
}

// Configurar filtros de categoría
function setupCategoryFilters() {
    const filterButtons = document.querySelectorAll('.category-filter');
    filterButtons.forEach(button => {
        button.addEventListener('click', function() {
            // Actualizar clase activa
            filterButtons.forEach(btn => btn.classList.remove('active'));
            this.classList.add('active');

            // Filtrar productos
            categoriaActual = this.getAttribute('data-category');
            aplicarFiltros();
        });
    });
}

// Configurar búsqueda
function setupSearch() {
    const searchInput = document.getElementById('searchCatalog');
    if (searchInput) {
        searchInput.addEventListener('input', aplicarFiltros);
    }
}

// Aplicar filtros
function aplicarFiltros() {
    const searchText = document.getElementById('searchCatalog')?.value.toLowerCase() || '';

    let productosFiltrados = todosLosProductos;

    // Filtrar por categoría
    if (categoriaActual !== 'all') {
        productosFiltrados = productosFiltrados.filter(p =>
            p.categoriaNombre?.toLowerCase().replace(/\s+/g, '-') === categoriaActual ||
            p.categoriaNombre?.toLowerCase() === categoriaActual
        );
    }

    // Filtrar por búsqueda
    if (searchText) {
        productosFiltrados = productosFiltrados.filter(p =>
            p.nombre.toLowerCase().includes(searchText) ||
            p.descripcion.toLowerCase().includes(searchText)
        );
    }

    renderizarProductos(productosFiltrados);

    // Actualizar contador
    const contador = document.querySelector('.text-muted');
    if (contador) {
        contador.textContent = `${productosFiltrados.length} producto${productosFiltrados.length !== 1 ? 's' : ''}`;
    }
}

// Renderizar productos
function renderizarProductos(productos) {
    const container = document.querySelector('.row.g-4');
    if (!container) return;

    // Limpiar productos existentes pero mantener el contenedor
    const productosExistentes = container.querySelectorAll('.col-lg-3.col-md-4.col-sm-6');
    productosExistentes.forEach(el => el.remove());

    if (productos.length === 0) {
        container.innerHTML = '<div class="col-12 text-center py-5"><p class="text-muted">No se encontraron productos</p></div>';
        return;
    }

    productos.forEach(producto => {
        const col = document.createElement('div');
        col.className = 'col-lg-3 col-md-4 col-sm-6';

        const imagenUrl = producto.imagenUrl
            ? `/uploads/products/${producto.imagenUrl}`
            : 'https://via.placeholder.com/400x300?text=Producto';

        col.innerHTML = `
            <div class="producto-card" data-category="${producto.categoriaNombre?.toLowerCase().replace(/\s+/g, '-') || 'sin-categoria'}">
                <div class="card-img-wrapper">
                    <img src="${imagenUrl}" alt="${producto.nombre}" 
                         onerror="this.src='https://via.placeholder.com/400x300?text=Producto'">
                    <span class="category-badge">${producto.categoriaNombre || 'Sin categoría'}</span>
                    <button class="favorite-btn" onclick="toggleFavorite(this, ${producto.idProducto})">
                        <i class="bi bi-heart"></i>
                    </button>
                </div>
                <div class="card-body">
                    <h5 class="card-title">${producto.nombre}</h5>
                    <p class="card-description">${producto.descripcion}</p>
                    <div class="rating-stars">
                        <i class="bi bi-star-fill"></i>
                        <i class="bi bi-star-fill"></i>
                        <i class="bi bi-star-fill"></i>
                        <i class="bi bi-star-fill"></i>
                        <i class="bi bi-star-half"></i>
                        <span class="ms-2 text-muted">(4.5)</span>
                    </div>
                </div>
                <div class="card-footer">
                    <span class="price">S/ ${Number(producto.precioVenta).toFixed(2)}</span>
                    <button class="add-to-cart" onclick="agregarAlCarrito(${producto.idProducto})">
                        <i class="bi bi-cart-plus"></i>
                    </button>
                </div>
            </div>
        `;

        container.appendChild(col);
    });
}

// Toggle favorito
function toggleFavorite(button, productoId) {
    button.classList.toggle('active');
    const icon = button.querySelector('i');
    if (button.classList.contains('active')) {
        icon.classList.remove('bi-heart');
        icon.classList.add('bi-heart-fill');
        console.log('Producto agregado a favoritos:', productoId);
    } else {
        icon.classList.remove('bi-heart-fill');
        icon.classList.add('bi-heart');
        console.log('Producto removido de favoritos:', productoId);
    }
}


// Notificación
function mostrarNotificacion(mensaje, tipo = 'info') {
    const toast = document.createElement('div');
    const bgClass = tipo === 'success' ? 'bg-success' : tipo === 'error' ? 'bg-danger' : 'bg-info';
    toast.className = `alert ${bgClass} text-white position-fixed`;
    toast.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px;';
    toast.innerHTML = `<i class="bi bi-check-circle"></i> ${mensaje}`;
    document.body.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

