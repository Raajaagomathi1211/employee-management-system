package com.ems.repository;

import com.ems.model.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends MongoRepository<Employee, String> {
    List<Employee> findByDepartmentId(String departmentId);
    Optional<Employee> findByEmail(String email);
}
