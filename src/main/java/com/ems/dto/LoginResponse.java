package com.ems.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String username;
    private String role;
    private String employeeId;

    public LoginResponse() {}

    public LoginResponse(boolean success, String message, String username, String role, String employeeId) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.role = role;
        this.employeeId = employeeId;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
}
