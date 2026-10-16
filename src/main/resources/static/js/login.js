document.addEventListener('DOMContentLoaded', function () {
    var existing = getSession();
    if (existing) {
        window.location.href = existing.role === 'ADMIN' ? 'admin-dashboard.html' : 'employee-dashboard.html';
        return;
    }

    document.getElementById('loginForm').addEventListener('submit', function (e) {
        e.preventDefault();
        var username = document.getElementById('username').value.trim();
        var password = document.getElementById('password').value;
        var errorEl = document.getElementById('loginError');
        errorEl.textContent = '';

        apiFetch('/api/auth/login', {
            method: 'POST',
            body: JSON.stringify({ username: username, password: password })
        }).then(function (res) {
            if (res.success) {
                setSession(res);
                window.location.href = res.role === 'ADMIN' ? 'admin-dashboard.html' : 'employee-dashboard.html';
            } else {
                errorEl.textContent = res.message || 'Invalid credentials';
            }
        }).catch(function (err) {
            errorEl.textContent = err.message;
        });
    });
});
