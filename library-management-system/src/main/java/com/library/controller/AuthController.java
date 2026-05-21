package com.library.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    // Hardcoded demo users — replace with DB-backed UserDetailsService later
    private static final Map<String, Map<String, String>> USERS = new HashMap<>();

    static {
        Map<String, String> admin = new HashMap<>();
        admin.put("password", "admin123");
        admin.put("role", "ADMIN");
        admin.put("name", "Admin User");
        USERS.put("admin@library.com", admin);

        Map<String, String> librarian = new HashMap<>();
        librarian.put("password", "librarian123");
        librarian.put("role", "LIBRARIAN");
        librarian.put("name", "Librarian User");
        USERS.put("librarian@library.com", librarian);

        Map<String, String> member = new HashMap<>();
        member.put("password", "member123");
        member.put("role", "MEMBER");
        member.put("name", "Member User");
        USERS.put("member@library.com", member);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email and password are required"));
        }

        Map<String, String> user = USERS.get(email.toLowerCase());

        if (user == null || !user.get("password").equals(password)) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        }

        // Simple token — in production use JWT
        String token = UUID.randomUUID().toString();

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("name", user.get("name"));
        userInfo.put("role", user.get("role"));

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", userInfo);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}
