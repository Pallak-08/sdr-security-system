package com.example.server;

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
        return verificationService.verifyDevice(body.get("deviceId"), body.get("apiKey"));
    }

    // ✅ LOGS API
    @GetMapping("/logs")
    public List<VerificationLog> getLogs() {
        return logRepository.findAll();
    }
}