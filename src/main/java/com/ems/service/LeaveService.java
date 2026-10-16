package com.ems.service;

import com.ems.model.Employee;
import com.ems.model.Leave;
import com.ems.model.LeaveBalance;
import com.ems.model.LeaveStatus;
import com.ems.repository.EmployeeRepository;
import com.ems.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaveService {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmailService emailService;

    public Leave applyLeave(Leave leave) {
        Employee employee = employeeRepository.findById(leave.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + leave.getEmployeeId()));
        leave.setStatus(LeaveStatus.PENDING);
        Leave saved = leaveRepository.save(leave);

        // Notify the admin that a new leave request is waiting for action.
        emailService.notifyAdminLeaveApplied(saved, employee);

        return saved;
    }

    public Leave approveLeave(String leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with id: " + leaveId));

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Leave request has already been processed");
        }

        Employee employee = employeeRepository.findById(leave.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + leave.getEmployeeId()));

        LeaveBalance balance = employee.getLeaveBalance();
        boolean deducted = balance.deduct(leave.getLeaveType(), (int) leave.getNumberOfDays());
        if (!deducted) {
            throw new RuntimeException("Insufficient leave balance for this leave type");
        }

        employee.setLeaveBalance(balance);
        employeeRepository.save(employee);

        leave.setStatus(LeaveStatus.APPROVED);
        Leave saved = leaveRepository.save(leave);

        // Notify the employee that their leave was approved.
        emailService.notifyEmployeeLeaveStatus(saved, employee);

        return saved;
    }

    public Leave rejectLeave(String leaveId) {
        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave request not found with id: " + leaveId));

        Employee employee = employeeRepository.findById(leave.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + leave.getEmployeeId()));

        leave.setStatus(LeaveStatus.REJECTED);
        Leave saved = leaveRepository.save(leave);

        // Notify the employee that their leave was rejected.
        emailService.notifyEmployeeLeaveStatus(saved, employee);

        return saved;
    }

    public List<Leave> getLeavesByEmployee(String employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }

    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    public LeaveBalance getLeaveBalance(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + employeeId));
        return employee.getLeaveBalance();
    }
}
