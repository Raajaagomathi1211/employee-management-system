package com.ems.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "employees")
public class Employee {

    @Id
    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String designation;
    private String departmentId;
    private double basicSalary;
    private LocalDate dateOfJoining;
    private String status;
    private LeaveBalance leaveBalance;

    public Employee() {
        this.status = "ACTIVE";
        this.leaveBalance = new LeaveBalance();
    }

    public Employee(String name, String email, String phone, String address, String designation,
                     String departmentId, double basicSalary, LocalDate dateOfJoining) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.designation = designation;
        this.departmentId = departmentId;
        this.basicSalary = basicSalary;
        this.dateOfJoining = dateOfJoining;
        this.status = "ACTIVE";
        this.leaveBalance = new LeaveBalance();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public String getDepartmentId() { return departmentId; }
    public void setDepartmentId(String departmentId) { this.departmentId = departmentId; }
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public LocalDate getDateOfJoining() { return dateOfJoining; }
    public void setDateOfJoining(LocalDate dateOfJoining) { this.dateOfJoining = dateOfJoining; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LeaveBalance getLeaveBalance() { return leaveBalance; }
    public void setLeaveBalance(LeaveBalance leaveBalance) { this.leaveBalance = leaveBalance; }
}
