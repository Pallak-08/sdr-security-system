package com.example.server;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.HashMap;

@Service
public class VerificationService {

    private final VerificationLogRepository logRepository;

    private final Map<String, String> validDevices = new HashMap<>();

    public VerificationService(VerificationLogRepository logRepository) {

        this.logRepository = logRepository;

        validDevices.put("SDR001", "12345");
        validDevices.put("SDR002", "ABCDE");
        validDevices.put("SDR003", "99999");
    }

    public Map<String, String> verifyDevice(String deviceId, String apiKey) {

        if (!validDevices.containsKey(deviceId)) {

            logRepository.save(
                new VerificationLog(deviceId, "REJECTED", "Unknown device")
            );

            return Map.of(
                    "status", "REJECTED",
                    "message", "Unknown device"
            );
        }

        String validKey = validDevices.get(deviceId);

        if (validKey.equals(apiKey)) {

            logRepository.save(
                new VerificationLog(deviceId, "AUTHORIZED", "")
            );

            return Map.of(
                    "status", "AUTHORIZED",
                    "message", "Device verification successful"
            );

        } else {

            logRepository.save(
                new VerificationLog(deviceId, "REJECTED", "Invalid API key")
            );

            return Map.of(
                    "status", "REJECTED",
                    "message", "Invalid API key"
            );
        }
    }
}