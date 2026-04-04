package com.example.server;

import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")   // ⭐ VERY IMPORTANT
@RestController
public class HelloController {

    private final VerificationService verificationService;
    private final VerificationLogRepository logRepository;

    public HelloController(VerificationService verificationService,
                           VerificationLogRepository logRepository) {
        this.verificationService = verificationService;
        this.logRepository = logRepository;
    }

    @GetMapping("/verify")
    public Map<String, String> verify(
            @RequestParam String deviceId,
            @RequestParam String apiKey) {

        return verificationService.verifyDevice(deviceId, apiKey);
    }

    @GetMapping("/logs")
    public List<VerificationLog> getLogs() {
        return logRepository.findAll();
    }
}