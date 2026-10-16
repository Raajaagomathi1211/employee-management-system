package com.ems.controller;

import com.ems.dto.LoginRequest;
import com.ems.dto.LoginResponse;
import com.ems.model.User;
import com.ems.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        Optional<User> userOpt = authService.login(request.getUsername(), request.getPassword());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            return new LoginResponse(true, "Login successful", user.getUsername(),
                    user.getRole().name(), user.getEmployeeId());
        }
        return new LoginResponse(false, "Invalid username or password", null, null, null);
    }
}
