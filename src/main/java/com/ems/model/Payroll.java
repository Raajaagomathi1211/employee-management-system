package com.ems.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "payrolls")
public class Payroll {

    @Id
    private String id;
    private String employeeId;
    private int month;
    private int year;
    private double basicSalary;
    private double allowances;
    private double deductions;
    private double netSalary;
    private LocalDate generatedDate;

    public Payroll() {}

    public Payroll(String employeeId, int month, int year, double basicSalary, double allowances, double deductions) {
        this.employeeId = employeeId;
        this.month = month;
        this.year = year;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.deductions = deductions;
        this.generatedDate = LocalDate.now();
        calculateNetSalary();
    }

    public void calculateNetSalary() {
        this.netSalary = (this.basicSalary + this.allowances) - this.deductions;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public double getAllowances() { return allowances; }
    public void setAllowances(double allowances) { this.allowances = allowances; }
    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; }
    public double getNetSalary() { return netSalary; }
    public void setNetSalary(double netSalary) { this.netSalary = netSalary; }
    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate generatedDate) { this.generatedDate = generatedDate; }
}
