package com.ems.repository;

import com.ems.model.Payroll;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PayrollRepository extends MongoRepository<Payroll, String> {
    List<Payroll> findByEmployeeId(String employeeId);
    List<Payroll> findByEmployeeIdAndMonthAndYear(String employeeId, int month, int year);
}
