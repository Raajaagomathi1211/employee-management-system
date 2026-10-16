var employees = [];
var departments = [];
var payrolls = [];
var leaves = [];
var editingEmployeeId = null;
var editingDepartmentId = null;

document.addEventListener('DOMContentLoaded', function () {
    var user = requireRole('ADMIN');
    if (!user) return;
    document.getElementById('welcomeText').textContent = 'Welcome, ' + user.username;
    document.getElementById('bannerName').textContent = 'Welcome, ' + user.username + '!';

    setupNav();

    // Load departments and employees first (sequentially) so that
    // employee/department names are always available before payroll
    // and leave tables (which reference them) are rendered.
    loadDepartments().then(function () {
        return loadEmployees();
    }).then(function () {
        return Promise.all([loadPayrolls(), loadLeaves()]);
    }).then(function () {
        renderDashboardSummary();
    }).catch(showGlobalError);

    document.getElementById('employeeForm').addEventListener('submit', handleEmployeeSubmit);
    document.getElementById('departmentForm').addEventListener('submit', handleDepartmentSubmit);
    document.getElementById('payrollForm').addEventListener('submit', handlePayrollSubmit);
    document.getElementById('logoutBtn').addEventListener('click', logout);
    document.getElementById('cancelEditEmployee').addEventListener('click', resetEmployeeForm);
    document.getElementById('cancelEditDepartment').addEventListener('click', resetDepartmentForm);
});

function setupNav() {
    var buttons = document.querySelectorAll('.nav-btn');
    buttons.forEach(function (btn) {
        btn.addEventListener('click', function () {
            buttons.forEach(function (b) { b.classList.remove('active'); });
            btn.classList.add('active');
            document.querySelectorAll('.section').forEach(function (sec) { sec.hidden = true; });
            document.getElementById(btn.dataset.target).hidden = false;
        });
    });
}

/* ---------------- Dashboard summary ---------------- */

function renderDashboardSummary() {
    document.getElementById('sumTotalEmployees').textContent = employees.length;

    var activeCount = employees.filter(function (e) { return e.status === 'ACTIVE'; }).length;
    document.getElementById('sumActiveEmployees').textContent = activeCount;

    document.getElementById('sumTotalDepartments').textContent = departments.length;
    document.getElementById('sumPayrollRecords').textContent = payrolls.length;

    var pendingLeaves = leaves.filter(function (l) { return l.status === 'PENDING'; }).length;
    document.getElementById('sumPendingLeaves').textContent = pendingLeaves;
}

/* ---------------- Departments ---------------- */

function loadDepartments() {
    return apiFetch('/api/departments').then(function (data) {
        departments = data;
        renderDepartmentTable();
        renderDepartmentOptions();
    }).catch(showGlobalError);
}

function renderDepartmentOptions() {
    var select = document.getElementById('empDepartment');
    select.innerHTML = '<option value="">-- Select Department --</option>' +
        departments.map(function (d) { return '<option value="' + d.id + '">' + d.name + '</option>'; }).join('');
}

function departmentName(id) {
    var dept = departments.find(function (d) { return d.id === id; });
    return dept ? dept.name : String.fromCharCode(8212);
}

function renderDepartmentTable() {
    var tbody = document.querySelector('#departmentTable tbody');
    tbody.innerHTML = departments.map(function (d) {
        return '<tr>' +
            '<td>' + d.name + '</td>' +
            '<td>' + (d.description || '') + '</td>' +
            '<td>' +
                '<button class="btn-small" onclick="editDepartment(\'' + d.id + '\')">Edit</button>' +
                '<button class="btn-small btn-danger" onclick="deleteDepartment(\'' + d.id + '\')">Delete</button>' +
            '</td>' +
        '</tr>';
    }).join('');
}

function handleDepartmentSubmit(e) {
    e.preventDefault();
    var submitBtn = document.querySelector('#departmentForm button[type="submit"]');
    if (submitBtn.disabled) return;
    submitBtn.disabled = true;

    var payload = {
        name: document.getElementById('deptName').value.trim(),
        description: document.getElementById('deptDescription').value.trim()
    };
    var request = editingDepartmentId
        ? apiFetch('/api/departments/' + editingDepartmentId, { method: 'PUT', body: JSON.stringify(payload) })
        : apiFetch('/api/departments', { method: 'POST', body: JSON.stringify(payload) });

    request.then(function () {
        resetDepartmentForm();
        return loadDepartments();
    }).then(function () {
        renderDashboardSummary();
    }).catch(showGlobalError)
    .finally(function () { submitBtn.disabled = false; });
}

function editDepartment(id) {
    var dept = departments.find(function (d) { return d.id === id; });
    if (!dept) return;
    editingDepartmentId = id;
    document.getElementById('deptName').value = dept.name;
    document.getElementById('deptDescription').value = dept.description || '';
    document.getElementById('departmentFormTitle').textContent = 'Edit Department';
    document.getElementById('cancelEditDepartment').hidden = false;
}

function resetDepartmentForm() {
    editingDepartmentId = null;
    document.getElementById('departmentForm').reset();
    document.getElementById('departmentFormTitle').textContent = 'Add Department';
    document.getElementById('cancelEditDepartment').hidden = true;
}

function deleteDepartment(id) {
    if (!confirm('Delete this department?')) return;
    apiFetch('/api/departments/' + id, { method: 'DELETE' }).then(function () {
        return loadDepartments();
    }).then(function () {
        renderDashboardSummary();
    }).catch(showGlobalError);
}

/* ---------------- Employees ---------------- */

function loadEmployees() {
    return apiFetch('/api/employees').then(function (data) {
        employees = data;
        renderEmployeeTable();
        renderEmployeeOptions();
        // Employee names may have changed - keep payroll/leave tables in sync too.
        if (payrolls.length) renderPayrollTable(payrolls);
        if (leaves.length) renderLeaveTable(leaves);
    });
}

function renderEmployeeOptions() {
    var select = document.getElementById('payrollEmployee');
    select.innerHTML = '<option value="">-- Select Employee --</option>' +
        employees.map(function (e) { return '<option value="' + e.id + '">' + e.name + '</option>'; }).join('');
}

function renderEmployeeTable() {
    var tbody = document.querySelector('#employeeTable tbody');
    tbody.innerHTML = employees.map(function (e) {
        return '<tr>' +
            '<td>' + e.name + '</td>' +
            '<td>' + (e.email || '') + '</td>' +
            '<td>' + (e.designation || '') + '</td>' +
            '<td>' + departmentName(e.departmentId) + '</td>' +
            '<td>' + formatCurrency(e.basicSalary) + '</td>' +
            '<td><span class="badge ' + (e.status === 'ACTIVE' ? 'badge-active' : 'badge-inactive') + '">' + e.status + '</span></td>' +
            '<td>' +
                '<button class="btn-small" onclick="editEmployee(\'' + e.id + '\')">Edit</button>' +
                '<button class="btn-small btn-danger" onclick="deleteEmployee(\'' + e.id + '\')">Delete</button>' +
            '</td>' +
        '</tr>';
    }).join('');
}

function handleEmployeeSubmit(e) {
    e.preventDefault();
    var submitBtn = document.querySelector('#employeeForm button[type="submit"]');
    if (submitBtn.disabled) return;
    submitBtn.disabled = true;

    var payload = {
        name: document.getElementById('empName').value.trim(),
        email: document.getElementById('empEmail').value.trim(),
        phone: document.getElementById('empPhone').value.trim(),
        address: document.getElementById('empAddress').value.trim(),
        designation: document.getElementById('empDesignation').value.trim(),
        departmentId: document.getElementById('empDepartment').value,
        basicSalary: parseFloat(document.getElementById('empSalary').value),
        dateOfJoining: document.getElementById('empJoining').value,
        status: document.getElementById('empStatus').value
    };

    var request = editingEmployeeId
        ? apiFetch('/api/employees/' + editingEmployeeId, { method: 'PUT', body: JSON.stringify(payload) })
        : apiFetch('/api/employees', { method: 'POST', body: JSON.stringify(payload) });

    request.then(function () {
        resetEmployeeForm();
        return loadEmployees();
    }).then(function () {
        renderDashboardSummary();
    }).catch(showGlobalError)
    .finally(function () { submitBtn.disabled = false; });
}

function editEmployee(id) {
    var emp = employees.find(function (e) { return e.id === id; });
    if (!emp) return;
    editingEmployeeId = id;
    document.getElementById('empName').value = emp.name;
    document.getElementById('empEmail').value = emp.email || '';
    document.getElementById('empPhone').value = emp.phone || '';
    document.getElementById('empAddress').value = emp.address || '';
    document.getElementById('empDesignation').value = emp.designation || '';
    document.getElementById('empDepartment').value = emp.departmentId || '';
    document.getElementById('empSalary').value = emp.basicSalary;
    document.getElementById('empJoining').value = emp.dateOfJoining || '';
    document.getElementById('empStatus').value = emp.status;
    document.getElementById('employeeFormTitle').textContent = 'Edit Employee';
    document.getElementById('cancelEditEmployee').hidden = false;
}

function resetEmployeeForm() {
    editingEmployeeId = null;
    document.getElementById('employeeForm').reset();
    document.getElementById('employeeFormTitle').textContent = 'Register Employee';
    document.getElementById('cancelEditEmployee').hidden = true;
}

function deleteEmployee(id) {
    if (!confirm('Delete this employee? This also removes their login account.')) return;
    apiFetch('/api/employees/' + id, { method: 'DELETE' }).then(function () {
        return loadEmployees();
    }).then(function () {
        return Promise.all([loadPayrolls(), loadLeaves()]);
    }).then(function () {
        renderDashboardSummary();
    }).catch(showGlobalError);
}

/* ---------------- Payroll ---------------- */

function loadPayrolls() {
    return apiFetch('/api/payroll').then(function (data) {
        payrolls = data;
        renderPayrollTable(payrolls);
    });
}

function employeeName(id) {
    var emp = employees.find(function (e) { return e.id === id; });
    return emp ? emp.name : String.fromCharCode(8212);
}

function renderPayrollTable(list) {
    var tbody = document.querySelector('#payrollTable tbody');
    tbody.innerHTML = list.map(function (p) {
        return '<tr>' +
            '<td>' + employeeName(p.employeeId) + '</td>' +
            '<td>' + p.month + '/' + p.year + '</td>' +
            '<td>' + formatCurrency(p.basicSalary) + '</td>' +
            '<td>' + formatCurrency(p.allowances) + '</td>' +
            '<td>' + formatCurrency(p.deductions) + '</td>' +
            '<td><strong>' + formatCurrency(p.netSalary) + '</strong></td>' +
        '</tr>';
    }).join('');
}

function handlePayrollSubmit(e) {
    e.preventDefault();
    var submitBtn = document.querySelector('#payrollForm button[type="submit"]');
    if (submitBtn.disabled) return;
    submitBtn.disabled = true;

    var payload = {
        employeeId: document.getElementById('payrollEmployee').value,
        month: parseInt(document.getElementById('payrollMonth').value, 10),
        year: parseInt(document.getElementById('payrollYear').value, 10),
        allowances: parseFloat(document.getElementById('payrollAllowances').value || '0'),
        deductions: parseFloat(document.getElementById('payrollDeductions').value || '0')
    };
    apiFetch('/api/payroll/generate', { method: 'POST', body: JSON.stringify(payload) })
        .then(function () {
            document.getElementById('payrollForm').reset();
            return loadPayrolls();
        })
        .then(function () { renderDashboardSummary(); })
        .catch(showGlobalError)
        .finally(function () { submitBtn.disabled = false; });
}

/* ---------------- Leaves ---------------- */

function loadLeaves() {
    return apiFetch('/api/leaves').then(function (data) {
        leaves = data;
        renderLeaveTable(leaves);
    });
}

function renderLeaveTable(list) {
    var tbody = document.querySelector('#leaveTable tbody');
    tbody.innerHTML = list.map(function (l) {
        var actions = l.status === 'PENDING'
            ? '<button class="btn-small" onclick="approveLeave(\'' + l.id + '\', this)">Approve</button>' +
              '<button class="btn-small btn-danger" onclick="rejectLeave(\'' + l.id + '\', this)">Reject</button>'
            : String.fromCharCode(8212);
        return '<tr>' +
            '<td>' + employeeName(l.employeeId) + '</td>' +
            '<td>' + l.leaveType + '</td>' +
            '<td>' + l.startDate + ' &rarr; ' + l.endDate + '</td>' +
            '<td>' + l.numberOfDays + '</td>' +
            '<td>' + (l.reason || '') + '</td>' +
            '<td><span class="badge status-' + l.status.toLowerCase() + '">' + l.status + '</span></td>' +
            '<td>' + actions + '</td>' +
        '</tr>';
    }).join('');
}

function approveLeave(id, btn) {
    if (btn && btn.disabled) return;
    if (btn) btn.disabled = true;
    apiFetch('/api/leaves/' + id + '/approve', { method: 'PUT' }).then(function () {
        return loadLeaves();
    }).then(function () {
        renderDashboardSummary();
    }).catch(function (err) {
        if (btn) btn.disabled = false;
        showGlobalError(err);
    });
}

function rejectLeave(id, btn) {
    if (btn && btn.disabled) return;
    if (btn) btn.disabled = true;
    apiFetch('/api/leaves/' + id + '/reject', { method: 'PUT' }).then(function () {
        return loadLeaves();
    }).then(function () {
        renderDashboardSummary();
    }).catch(function (err) {
        if (btn) btn.disabled = false;
        showGlobalError(err);
    });
}

function formatCurrency(value) {
    var num = Number(value || 0);
    return '\u20B9' + num.toLocaleString('en-IN', { maximumFractionDigits: 2 });
}
