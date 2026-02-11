/* GastroTech - JavaScript para Cliente */
document.addEventListener('DOMContentLoaded', function() {
    initNavbar();
    initCarousel();
    initFilters();
    initResenas();
    initAuth();
});

// Navbar scroll effect
function initNavbar() {
    const navbar = document.querySelector('.navbar-gastro');
    if (navbar) {
        window.addEventListener('scroll', function() {
            if (window.scrollY > 50) {
                navbar.classList.add('scrolled');
            } else {
                navbar.classList.remove('scrolled');
            }
        });
    }
}

// Carrusel de platos
function initCarousel() {
    const carousels = document.querySelectorAll('.dish-carousel');
    carousels.forEach(carousel => {
        new bootstrap.Carousel(carousel, {
            interval: 5000,
            wrap: true
        });
    });
}

// Filtros de categoría
function initFilters() {
    const filterBtns = document.querySelectorAll('.category-filter');
    const products = document.querySelectorAll('.producto-card');

    filterBtns.forEach(btn => {
        btn.addEventListener('click', function() {
            filterBtns.forEach(b => b.classList.remove('active'));
            this.classList.add('active');

            const category = this.getAttribute('data-category');

            products.forEach(product => {
                const productCategory = product.getAttribute('data-category');
                if (category === 'all' || productCategory === category) {
                    product.closest('.col').style.display = 'block';
                    product.classList.add('fade-in');
                } else {
                    product.closest('.col').style.display = 'none';
                }
            });
        });
    });
}

// Sistema de reseñas
function initResenas() {
    const ratingInputs = document.querySelectorAll('.resena-rating');

    ratingInputs.forEach(container => {
        const stars = container.querySelectorAll('i');
        const input = container.querySelector('input[type="hidden"]');

        stars.forEach((star, index) => {
            star.addEventListener('click', function() {
                const rating = index + 1;
                if (input) input.value = rating;

                stars.forEach((s, i) => {
                    if (i < rating) {
                        s.classList.remove('bi-star', 'empty');
                        s.classList.add('bi-star-fill');
                    } else {
                        s.classList.remove('bi-star-fill');
                        s.classList.add('bi-star', 'empty');
                    }
                });
            });

            star.addEventListener('mouseenter', function() {
                stars.forEach((s, i) => {
                    s.style.transform = i <= index ? 'scale(1.2)' : 'scale(1)';
                });
            });

            star.addEventListener('mouseleave', function() {
                stars.forEach(s => s.style.transform = 'scale(1)');
            });
        });
    });
}

// Enviar reseña
function submitResena(pedidoId, productoId) {
    const container = document.querySelector(`[data-pedido="${pedidoId}"][data-producto="${productoId}"]`);
    const rating = container.querySelector('input[name="calificacion"]')?.value;
    const comentario = container.querySelector('textarea')?.value;

    if (!rating || rating === '0') {
        showAlert('Por favor seleccione una calificación', 'warning');
        return;
    }

    fetch('/resena', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            idPedido: pedidoId,
            idProducto: productoId,
            calificacion: parseInt(rating),
            comentario: comentario
        })
    })
    .then(response => response.json())
    .then(data => {
        showAlert('¡Gracias por tu reseña!', 'success');
        container.innerHTML = '<p class="text-success"><i class="bi bi-check-circle"></i> Reseña enviada</p>';
    })
    .catch(error => {
        showAlert('Error al enviar la reseña', 'danger');
    });
}

// Autenticación
function initAuth() {
    const authTabs = document.querySelectorAll('.auth-tab');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');

    authTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            authTabs.forEach(t => t.classList.remove('active'));
            this.classList.add('active');

            const target = this.getAttribute('data-target');
            if (target === 'login') {
                loginForm?.classList.remove('d-none');
                registerForm?.classList.add('d-none');
            } else {
                loginForm?.classList.add('d-none');
                registerForm?.classList.remove('d-none');
            }
        });
    });
}

// Login cliente
function loginCliente(event) {
    event.preventDefault();
    const form = event.target;
    const dni = form.querySelector('#dni').value;
    const password = form.querySelector('#password').value;

    fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: dni, password: password })
    })
    .then(response => {
        if (response.ok) return response.json();
        throw new Error('Credenciales incorrectas');
    })
    .then(data => {
        if (data.success) {
            // Redirigir según el perfil
            window.location.href = data.redirectUrl || '/catalogo';
        } else {
            throw new Error(data.message || 'Error en el login');
        }
    })
    .catch(error => {
        showAlert(error.message, 'danger');
    });
}

// Registro cliente
function registerCliente(event) {
    event.preventDefault();
    const form = event.target;

    const data = {
        nombre: form.querySelector('#regNombre').value,
        apellido: form.querySelector('#regApellido').value,
        DNI: form.querySelector('#regDni').value,
        contrasena: form.querySelector('#regPassword').value
    };

    const confirmPassword = form.querySelector('#regConfirmPassword').value;

    if (data.contrasena !== confirmPassword) {
        showAlert('Las contraseñas no coinciden', 'warning');
        return;
    }

    if (data.DNI.length !== 8) {
        showAlert('El DNI debe tener 8 dígitos', 'warning');
        return;
    }

    fetch('/api/auth/registro', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data)
    })
    .then(response => {
        // Verificar si la respuesta es JSON
        const contentType = response.headers.get('content-type');
        if (!contentType || !contentType.includes('application/json')) {
            throw new Error('El servidor no devolvió JSON. Verifica la consola del backend.');
        }

        if (response.ok) {
            return response.json();
        }

        // Si hay error, intentar obtener el mensaje de error
        return response.json().then(err => {
            throw new Error(err.message || 'Error en el registro');
        });
    })
    .then(data => {
        if (data.success) {
            showAlert('¡Registro exitoso! Ya puedes iniciar sesión', 'success');
            document.querySelector('[data-target="login"]')?.click();
        } else {
            showAlert(data.message || 'Error en el registro', 'danger');
        }
    })
    .catch(error => {
        console.error('Error completo:', error);
        showAlert(error.message, 'danger');
    });
}

// Login admin
function loginAdmin(event) {
    event.preventDefault();
    const form = event.target;
    const username = form.querySelector('#username').value;
    const password = form.querySelector('#password').value;

    fetch('/api/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password })
    })
    .then(response => {
        if (response.ok) return response.json();
        return response.json().then(err => { throw new Error(err.message || 'Credenciales incorrectas'); });
    })
    .then(data => {
        if (data.success) {
            sessionStorage.setItem('userName', username);
            sessionStorage.setItem('perfil', data.perfil);
            window.location.href = data.redirectUrl || '/admin/dashboard';
        } else {
            throw new Error(data.message || 'Error al iniciar sesión');
        }
    })
    .catch(error => {
        showAlert(error.message, 'danger');
    });
}

// Logout
function logout() {
    // Llamar al endpoint de logout del backend para invalidar la sesión
    fetch('/api/auth/logout', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' }
    })
    .then(response => response.json())
    .then(data => {
        // Limpiar sessionStorage local
        sessionStorage.clear();
        // Redirigir al inicio
        window.location.href = '/';
    })
    .catch(error => {
        console.error('Error en logout:', error);
        // Incluso si hay error, limpiar y redirigir
        sessionStorage.clear();
        window.location.href = '/';
    });
}

// Mostrar alertas
function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    alertDiv.style.cssText = 'top: 100px; right: 20px; z-index: 9999; min-width: 300px;';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    document.body.appendChild(alertDiv);
    setTimeout(() => alertDiv.remove(), 4000);
}

// Favoritos
function toggleFavorite(btn, productoId) {
    btn.classList.toggle('active');
    const icon = btn.querySelector('i');

    if (btn.classList.contains('active')) {
        icon.classList.remove('bi-heart');
        icon.classList.add('bi-heart-fill');
        showAlert('Añadido a favoritos', 'success');
    } else {
        icon.classList.remove('bi-heart-fill');
        icon.classList.add('bi-heart');
    }
}

// Exportar funciones
window.GastroCliente = {
    submitResena,
    loginCliente,
    registerCliente,
    loginAdmin,
    logout,
    toggleFavorite,
    showAlert
};
