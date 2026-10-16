package com.ems.service;

import com.ems.model.Employee;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private UserRepository userRepository;

    public Employee registerEmployee(Employee employee) {
        Employee saved = employeeRepository.save(employee);
        String username = generateUsername(saved.getName());
        User user = new User(username, "123", Role.EMPLOYEE, saved.getId());
        userRepository.save(user);
        return saved;
    }

    private String generateUsername(String name) {
        String base = name == null ? "employee" : name.trim().toLowerCase().replaceAll("[^a-z0-9]", "");
        if (base.isEmpty()) {
            base = "employee";
        }
        String username = base;
        int counter = 1;
        while (userRepository.findByUsername(username).isPresent()) {
            username = base + counter;
            counter++;
        }
        return username;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(String id) {
        return employeeRepository.findById(id);
    }

    public List<Employee> getEmployeesByDepartment(String departmentId) {
        return employeeRepository.findByDepartmentId(departmentId);
    }

    public Employee updateEmployee(String id, Employee updated) {
        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
        existing.setName(updated.getName());
        existing.setEmail(updated.getEmail());
        existing.setPhone(updated.getPhone());
        existing.setAddress(updated.getAddress());
        existing.setDesignation(updated.getDesignation());
        existing.setDepartmentId(updated.getDepartmentId());
        existing.setBasicSalary(updated.getBasicSalary());
        if (updated.getDateOfJoining() != null) {
            existing.setDateOfJoining(updated.getDateOfJoining());
        }
        if (updated.getStatus() != null) {
            existing.setStatus(updated.getStatus());
        }
        return employeeRepository.save(existing);
    }

    public void deleteEmployee(String id) {
        employeeRepository.deleteById(id);
        userRepository.findByEmployeeId(id).ifPresent(user -> userRepository.deleteById(user.getId()));
    }
}
