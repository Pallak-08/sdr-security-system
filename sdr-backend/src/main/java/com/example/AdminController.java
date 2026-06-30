package com.example.server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.io.*;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:4200")
public class AdminController {

    private static final String FILE_PATH = "devices.properties";

    // Session tokens issued on login (in-memory; sufficient for demo scope)
    private static final Set<String> activeSessions = new HashSet<>();

    // LOGIN
    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {
        if ("admin".equals(req.get("username")) &&
            "drdo@2024".equals(req.get("password"))) {
            String token = UUID.randomUUID().toString();
            activeSessions.add(token);
            return Map.of("status", "SUCCESS", "token", token);
        }
        return Map.of("status", "FAIL");
    }

    // LOGOUT
    @PostMapping("/logout")
    public Map<String, String> logout(@RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (token != null) activeSessions.remove(token);
        return Map.of("status", "LOGGED_OUT");
    }

    // ADD DEVICE (requires valid session token)
    @PostMapping("/add-device")
    public ResponseEntity<Map<String, String>> addDevice(
            @RequestHeader(value = "X-Admin-Token", required = false) String token,
            @RequestBody Map<String, String> req) {

        if (token == null || !activeSessions.contains(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "UNAUTHORIZED"));
        }

        try {
            File file = new File(FILE_PATH);
            file.createNewFile();

            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            }

            props.setProperty(req.get("deviceId"), req.get("apiKey"));

            try (FileOutputStream fos = new FileOutputStream(file)) {
                props.store(fos, null);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(Map.of("status", "ADDED"));
    }

    // GET DEVICES (requires valid session token)
    @GetMapping("/devices")
    public ResponseEntity<?> getDevices(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {

        if (token == null || !activeSessions.contains(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "UNAUTHORIZED"));
        }

        Map<String, String> result = new HashMap<>();

        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return ResponseEntity.ok(result);

            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(file)) {
                props.load(fis);
            }

            for (String key : props.stringPropertyNames()) {
                result.put(key, props.getProperty(key));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(result);
    }
}
