package com.example.server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/")
@CrossOrigin(origins = "http://localhost:4200")
public class HelloController {

    private final VerificationService verificationService;
    private final VerificationLogRepository logRepository;

    public HelloController(VerificationService verificationService,
                           VerificationLogRepository logRepository) {
        this.verificationService = verificationService;
        this.logRepository = logRepository;
    }

    // ✅ ROOT endpoint (fixes Whitelabel page)
    @GetMapping("/")
    public String home() {
        return "Backend is running 🚀";
    }

    // ✅ VERIFY API
    @PostMapping("/verify")
    public Map<String, String> verify(@RequestBody Map<String, String> body) {
        String deviceId = body.get("deviceId");
        String apiKey = body.get("apiKey");

        if (deviceId == null || deviceId.isBlank() || apiKey == null || apiKey.isBlank()) {
            return Map.of("status", "REJECTED", "message", "Device ID and API Key are required",
                          "deviceId", deviceId != null ? deviceId : "");
        }

        Map<String, String> result = verificationService.verifyDevice(deviceId, apiKey);
        // Return deviceId back so the frontend result card can display it
        Map<String, String> response = new java.util.HashMap<>(result);
        response.put("deviceId", deviceId);
        return response;
    }

    // ✅ LOGS API — admin token required
    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(
            @RequestHeader(value = "X-Admin-Token", required = false) String token) {
        if (token == null || !AdminController.isValidToken(token)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "UNAUTHORIZED"));
        }
        return ResponseEntity.ok(logRepository.findAll());
    }
}