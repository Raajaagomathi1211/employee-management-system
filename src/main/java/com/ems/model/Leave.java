package com.ems.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Document(collection = "leaves")
public class Leave {

    @Id
    private String id;
    private String employeeId;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private long numberOfDays;
    private String reason;
    private LeaveStatus status;
    private LocalDate appliedDate;

    public Leave() {
        this.status = LeaveStatus.PENDING;
        this.appliedDate = LocalDate.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        recalculateDays();
    }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        recalculateDays();
    }
    public long getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(long numberOfDays) { this.numberOfDays = numberOfDays; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LeaveStatus getStatus() { return status; }
    public void setStatus(LeaveStatus status) { this.status = status; }
    public LocalDate getAppliedDate() { return appliedDate; }
    public void setAppliedDate(LocalDate appliedDate) { this.appliedDate = appliedDate; }

    private void recalculateDays() {
        if (this.startDate != null && this.endDate != null) {
            this.numberOfDays = ChronoUnit.DAYS.between(this.startDate, this.endDate) + 1;
        }
    }
}
