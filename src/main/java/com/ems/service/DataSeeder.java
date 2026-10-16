package com.ems.service;

import com.ems.model.Department;
import com.ems.model.Employee;
import com.ems.model.Role;
import com.ems.model.User;
import com.ems.repository.DepartmentRepository;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) {
        if (userRepository.findByUsername("admin").isEmpty()) {
            userRepository.save(new User("admin", "123", Role.ADMIN, null));
        }

        if (departmentRepository.count() == 0) {
            Department engineering = departmentRepository.save(new Department("Engineering", "Software development and technical operations"));
            departmentRepository.save(new Department("Human Resources", "Employee welfare and recruitment"));
            departmentRepository.save(new Department("Finance", "Accounts and payroll management"));

            Employee sample = new Employee("Arun Kumar", "arun.kumar@company.com", "9876543210",
                    "Madurai, Tamil Nadu", "Software Engineer", engineering.getId(), 45000.0, LocalDate.now());
            sample = employeeRepository.save(sample);

            if (userRepository.findByUsername("arunkumar").isEmpty()) {
                userRepository.save(new User("arunkumar", "123", Role.EMPLOYEE, sample.getId()));
            }
        }
    }
}
