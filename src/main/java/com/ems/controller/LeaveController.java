package com.ems.controller;

import com.ems.model.Leave;
import com.ems.model.LeaveBalance;
import com.ems.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    @PostMapping("/apply")
    public Leave applyLeave(@RequestBody Leave leave) {
        return leaveService.applyLeave(leave);
    }

    @PutMapping("/{id}/approve")
    public Leave approveLeave(@PathVariable String id) {
        return leaveService.approveLeave(id);
    }

    @PutMapping("/{id}/reject")
    public Leave rejectLeave(@PathVariable String id) {
        return leaveService.rejectLeave(id);
    }

    @GetMapping("/employee/{employeeId}")
    public List<Leave> getLeavesByEmployee(@PathVariable String employeeId) {
        return leaveService.getLeavesByEmployee(employeeId);
    }

    @GetMapping("/balance/{employeeId}")
    public LeaveBalance getLeaveBalance(@PathVariable String employeeId) {
        return leaveService.getLeaveBalance(employeeId);
    }

    @GetMapping
    public List<Leave> getAllLeaves() {
        return leaveService.getAllLeaves();
    }
}
