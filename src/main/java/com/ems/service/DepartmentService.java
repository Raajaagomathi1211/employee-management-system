package com.ems.service;

import com.ems.model.Department;
import com.ems.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public Department createDepartment(Department department) {
        return departmentRepository.save(department);
    }

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    public Optional<Department> getDepartmentById(String id) {
        return departmentRepository.findById(id);
    }

    public Department updateDepartment(String id, Department updated) {
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found with id: " + id));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setManagerId(updated.getManagerId());
        return departmentRepository.save(existing);
    }

    public void deleteDepartment(String id) {
        departmentRepository.deleteById(id);
    }
}
