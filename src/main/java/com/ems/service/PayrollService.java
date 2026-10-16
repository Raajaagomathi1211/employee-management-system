package com.ems.service;

import com.ems.model.Employee;
import com.ems.model.Payroll;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.PayrollRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PayrollService {

    @Autowired
    private PayrollRepository payrollRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Payroll generatePayroll(String employeeId, int month, int year, double allowances, double deductions) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));

        List<Payroll> existing = payrollRepository.findByEmployeeIdAndMonthAndYear(employeeId, month, year);
        if (!existing.isEmpty()) {
            throw new RuntimeException("Payroll already generated for this employee for " + month + "/" + year);
        }

        Payroll payroll = new Payroll(employeeId, month, year, employee.getBasicSalary(), allowances, deductions);
        return payrollRepository.save(payroll);
    }

    public List<Payroll> getPayrollsByEmployee(String employeeId) {
        return payrollRepository.findByEmployeeId(employeeId);
    }

    public List<Payroll> getAllPayrolls() {
        return payrollRepository.findAll();
    }
}
