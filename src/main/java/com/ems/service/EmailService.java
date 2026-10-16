package com.ems.service;

import com.ems.model.Employee;
import com.ems.model.Leave;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends leave-related email notifications:
 *  - When an employee applies for leave -> notifies the admin.
 *  - When the admin approves/rejects a leave -> notifies the employee.
 *
 * Controlled by app.mail.enabled in application.properties. If disabled or
 * misconfigured, emails are skipped with a log message instead of breaking
 * the leave apply/approve/reject flow.
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    @Value("${app.mail.from:}")
    private String fromAddress;

    @Value("${app.mail.admin-email:}")
    private String adminEmail;

    /** Notify the admin that a new leave request has been submitted. */
    public void notifyAdminLeaveApplied(Leave leave, Employee employee) {
        if (!isReady()) return;
        if (adminEmail == null || adminEmail.isBlank()) {
            log.warn("app.mail.admin-email is not set - skipping admin leave notification.");
            return;
        }

        String subject = "New Leave Request - " + employee.getName();
        String body =
                "A new leave request has been submitted.\n\n" +
                "Employee   : " + employee.getName() + "\n" +
                "Leave Type : " + leave.getLeaveType() + "\n" +
                "From       : " + leave.getStartDate() + "\n" +
                "To         : " + leave.getEndDate() + "\n" +
                "Days       : " + leave.getNumberOfDays() + "\n" +
                "Reason     : " + (leave.getReason() == null || leave.getReason().isBlank() ? "-" : leave.getReason()) + "\n\n" +
                "Please log in to the Employee Management System to approve or reject this request.";

        send(adminEmail, subject, body);
    }

    /** Notify the employee that their leave request has been approved or rejected. */
    public void notifyEmployeeLeaveStatus(Leave leave, Employee employee) {
        if (!isReady()) return;
        if (employee.getEmail() == null || employee.getEmail().isBlank()) {
            log.warn("Employee {} has no email on file - skipping leave status notification.", employee.getName());
            return;
        }

        String statusWord = leave.getStatus().name(); // APPROVED / REJECTED
        String subject = "Your Leave Request has been " + capitalize(statusWord);
        String body =
                "Hello " + employee.getName() + ",\n\n" +
                "Your leave request has been " + statusWord.toLowerCase() + ".\n\n" +
                "Leave Type : " + leave.getLeaveType() + "\n" +
                "From       : " + leave.getStartDate() + "\n" +
                "To         : " + leave.getEndDate() + "\n" +
                "Days       : " + leave.getNumberOfDays() + "\n\n" +
                "Regards,\nEmployee Management System";

        send(employee.getEmail(), subject, body);
    }

    private boolean isReady() {
        if (!mailEnabled) {
            log.info("Email notifications are disabled (set app.mail.enabled=true to turn them on).");
            return false;
        }
        if (fromAddress == null || fromAddress.isBlank()) {
            log.warn("app.mail.from is not set - skipping email.");
            return false;
        }
        return true;
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent to {} - {}", to, subject);
        } catch (Exception e) {
            // Never let an email failure break the leave apply/approve/reject flow.
            log.warn("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
