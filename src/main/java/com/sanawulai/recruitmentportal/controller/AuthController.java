package com.sanawulai.recruitmentportal.controller;

import com.sanawulai.recruitmentportal.dto.request.LoginRequest;
import com.sanawulai.recruitmentportal.dto.response.LoginResponse;
import com.sanawulai.recruitmentportal.dto.response.RegisterRequest;
import com.sanawulai.recruitmentportal.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponse>register(@RequestBody RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>login(@RequestBody LoginRequest request){
        return ResponseEntity.ok(authService.authenticate(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization")String token){
        authService.logout(token);
        return ResponseEntity.ok("Successfully logged out");

    }
}
