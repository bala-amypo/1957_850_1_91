package com.example.demo.controller;

import com.example.demo.dto.AuthRequest;
import com.example.demo.dto.AuthResponse;
import com.example.demo.model.User;
import com.example.demo.security.JwtUtil;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Auth", description = "Authentication endpoints")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    
    public AuthController(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) {
        User savedUser = userService.register(user);
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", savedUser.getEmail());
        claims.put("role", savedUser.getRole());
        
        String token = jwtUtil.generateToken(claims, savedUser.getEmail());
        
        return ResponseEntity.ok(new AuthResponse(token, savedUser.getId(), 
                savedUser.getEmail(), savedUser.getRole()));
    }
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        User user = userService.findByEmail(request.getEmail());
        
        if (passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("email", user.getEmail());
            claims.put("role", user.getRole());
            
            String token = jwtUtil.generateToken(claims, user.getEmail());
            
            return ResponseEntity.ok(new AuthResponse(token, user.getId(), 
                    user.getEmail(), user.getRole()));
        }
        
        return ResponseEntity.badRequest().build();
    }
}