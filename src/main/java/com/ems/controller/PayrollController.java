package com.ems.controller;

import com.ems.model.Payroll;
import com.ems.service.PayrollService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payroll")
public class PayrollController {

    @Autowired
    private PayrollService payrollService;

    @PostMapping("/generate")
    public Payroll generatePayroll(@RequestBody Map<String, Object> request) {
        String employeeId = (String) request.get("employeeId");
        int month = Integer.parseInt(String.valueOf(request.get("month")));
        int year = Integer.parseInt(String.valueOf(request.get("year")));
        double allowances = request.get("allowances") != null ? Double.parseDouble(String.valueOf(request.get("allowances"))) : 0.0;
        double deductions = request.get("deductions") != null ? Double.parseDouble(String.valueOf(request.get("deductions"))) : 0.0;
        return payrollService.generatePayroll(employeeId, month, year, allowances, deductions);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Payroll> getPayrollsByEmployee(@PathVariable String employeeId) {
        return payrollService.getPayrollsByEmployee(employeeId);
    }

    @GetMapping
    public List<Payroll> getAllPayrolls() {
        return payrollService.getAllPayrolls();
    }
}
