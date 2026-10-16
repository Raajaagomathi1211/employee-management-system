function apiFetch(url, options) {
    options = options || {};
    options.headers = options.headers || {};
    if (options.body) {
        options.headers['Content-Type'] = 'application/json';
    }
    return fetch(url, options).then(function (res) {
        return res.json().catch(function () { return null; }).then(function (data) {
            if (!res.ok) {
                var message = (data && data.error) ? data.error : 'Something went wrong';
                throw new Error(message);
            }
            return data;
        });
    });
}

function getSession() {
    var raw = localStorage.getItem('emsUser');
    return raw ? JSON.parse(raw) : null;
}

function setSession(user) {
    localStorage.setItem('emsUser', JSON.stringify(user));
}

function clearSession() {
    localStorage.removeItem('emsUser');
}

function requireRole(role) {
    var user = getSession();
    if (!user || user.role !== role) {
        window.location.href = 'login.html';
        return null;
    }
    return user;
}

function logout() {
    clearSession();
    window.location.href = 'login.html';
}

function showGlobalError(err) {
    alert(err.message || 'Something went wrong');
}
