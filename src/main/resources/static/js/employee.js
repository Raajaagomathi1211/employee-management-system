var currentEmployee = null;
var currentLeaves = [];
var currentPayslips = [];

document.addEventListener('DOMContentLoaded', function () {
    var user = requireRole('EMPLOYEE');
    if (!user) return;
    document.getElementById('welcomeText').textContent = 'Welcome, ' + user.username;

    setupNav();
    setupLeaveDateLimits();

    // Load profile first (needed for name/department), then everything else.
    loadProfile(user.employeeId).then(function () {
        loadDepartmentDetails();
        return Promise.all([
            loadLeaveBalance(user.employeeId),
            loadLeaveHistory(user.employeeId),
            loadPayslips(user.employeeId)
        ]);
    }).then(function () {
        renderDashboardSummary();
    }).catch(showGlobalError);

    document.getElementById('leaveForm').addEventListener('submit', function (e) {
        handleLeaveSubmit(e, user.employeeId);
    });
    document.getElementById('logoutBtn').addEventListener('click', logout);
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

function loadProfile(employeeId) {
    return apiFetch('/api/employees/' + employeeId).then(function (emp) {
        currentEmployee = emp;
        var el = document.getElementById('profileCard');
        el.innerHTML =
            row('Name', emp.name) +
            row('Email', emp.email) +
            row('Phone', emp.phone) +
            row('Address', emp.address) +
            row('Designation', emp.designation) +
            row('Basic Salary', formatCurrency(emp.basicSalary)) +
            row('Date of Joining', emp.dateOfJoining) +
            row('Status', emp.status);

        document.getElementById('bannerName').textContent = 'Welcome back, ' + emp.name + '!';
        document.getElementById('bannerSub').textContent = 'Here is a quick summary of your account.';
        return emp;
    });
}

function todayISO() {
    var d = new Date();
    var month = String(d.getMonth() + 1).padStart(2, '0');
    var day = String(d.getDate()).padStart(2, '0');
    return d.getFullYear() + '-' + month + '-' + day;
}

function setupLeaveDateLimits() {
    var startInput = document.getElementById('leaveStart');
    var endInput = document.getElementById('leaveEnd');
    var today = todayISO();

    // Cannot apply leave for past dates - earliest selectable day is today.
    startInput.min = today;
    endInput.min = today;

    startInput.addEventListener('change', function () {
        // End date can never be earlier than the chosen start date.
        endInput.min = startInput.value || today;
        if (endInput.value && endInput.value < endInput.min) {
            endInput.value = endInput.min;
        }
    });
}

function row(label, value) {
    return '<div class="profile-row"><span>' + label + '</span><strong>' + (value || String.fromCharCode(8212)) + '</strong></div>';
}

function loadDepartmentDetails() {
    var card = document.getElementById('departmentCard');
    if (!currentEmployee || !currentEmployee.departmentId) {
        card.innerHTML = '<p>No department assigned yet.</p>';
        return;
    }
    apiFetch('/api/departments/' + currentEmployee.departmentId).then(function (dept) {
        var renderCard = function (headName) {
            card.innerHTML =
                row('Department Name', dept.name) +
                row('Description', dept.description) +
                row('Department Head', headName);
        };

        if (dept.managerId) {
            apiFetch('/api/employees/' + dept.managerId)
                .then(function (manager) { renderCard(manager.name); })
                .catch(function () { renderCard(String.fromCharCode(8212)); });
        } else {
            renderCard(String.fromCharCode(8212));
        }
    }).catch(function () {
        card.innerHTML = '<p>Department details are unavailable right now.</p>';
    });
}

function loadLeaveBalance(employeeId) {
    return apiFetch('/api/leaves/balance/' + employeeId).then(function (balance) {
        document.getElementById('casualLeaves').textContent = balance.casualLeaves;
        document.getElementById('sickLeaves').textContent = balance.sickLeaves;
        document.getElementById('earnedLeaves').textContent = balance.earnedLeaves;
        var total = (balance.casualLeaves || 0) + (balance.sickLeaves || 0) + (balance.earnedLeaves || 0);
        document.getElementById('sumLeaveAvailable').textContent = total;
    });
}

function loadLeaveHistory(employeeId) {
    return apiFetch('/api/leaves/employee/' + employeeId).then(function (leaves) {
        currentLeaves = leaves;
        var tbody = document.querySelector('#myLeaveTable tbody');
        tbody.innerHTML = leaves.map(function (l) {
            return '<tr>' +
                '<td>' + l.leaveType + '</td>' +
                '<td>' + l.startDate + ' &rarr; ' + l.endDate + '</td>' +
                '<td>' + l.numberOfDays + '</td>' +
                '<td>' + (l.reason || '') + '</td>' +
                '<td><span class="badge status-' + l.status.toLowerCase() + '">' + l.status + '</span></td>' +
            '</tr>';
        }).join('');
    });
}

function handleLeaveSubmit(e, employeeId) {
    e.preventDefault();

    var submitBtn = document.querySelector('#leaveForm button[type="submit"]');

    // Guard against double-click / double-submit: if a request is already
    // in flight, ignore any further clicks until it finishes.
    if (submitBtn.disabled) return;

    submitBtn.disabled = true;
    var originalText = submitBtn.textContent;
    submitBtn.textContent = 'Submitting...';

    var payload = {
        employeeId: employeeId,
        leaveType: document.getElementById('leaveType').value,
        startDate: document.getElementById('leaveStart').value,
        endDate: document.getElementById('leaveEnd').value,
        reason: document.getElementById('leaveReason').value.trim()
    };

    apiFetch('/api/leaves/apply', { method: 'POST', body: JSON.stringify(payload) })
        .then(function () {
            document.getElementById('leaveForm').reset();
            return Promise.all([loadLeaveHistory(employeeId), loadLeaveBalance(employeeId)]);
        })
        .then(function () { renderDashboardSummary(); })
        .catch(showGlobalError)
        .finally(function () {
            submitBtn.disabled = false;
            submitBtn.textContent = originalText;
        });
}

function loadPayslips(employeeId) {
    return apiFetch('/api/payroll/employee/' + employeeId).then(function (payrolls) {
        currentPayslips = payrolls;
        var tbody = document.querySelector('#payslipTable tbody');
        tbody.innerHTML = payrolls.map(function (p) {
            return '<tr>' +
                '<td>' + p.month + '/' + p.year + '</td>' +
                '<td>' + formatCurrency(p.basicSalary) + '</td>' +
                '<td>' + formatCurrency(p.allowances) + '</td>' +
                '<td>' + formatCurrency(p.deductions) + '</td>' +
                '<td><strong>' + formatCurrency(p.netSalary) + '</strong></td>' +
            '</tr>';
        }).join('');
    });
}

function renderDashboardSummary() {
    document.getElementById('sumPayslipCount').textContent = currentPayslips.length;

    var pendingCount = currentLeaves.filter(function (l) { return l.status === 'PENDING'; }).length;
    document.getElementById('sumLeavePending').textContent = pendingCount;

    var approvedCount = currentLeaves.filter(function (l) { return l.status === 'APPROVED'; }).length;
    document.getElementById('sumLeaveApproved').textContent = approvedCount;

    if (currentPayslips.length > 0) {
        var latest = currentPayslips[currentPayslips.length - 1];
        document.getElementById('sumLatestSalary').textContent = formatCurrency(latest.netSalary);
    } else {
        document.getElementById('sumLatestSalary').textContent = String.fromCharCode(8212);
    }
}

function formatCurrency(value) {
    var num = Number(value || 0);
    return '\u20B9' + num.toLocaleString('en-IN', { maximumFractionDigits: 2 });
}
