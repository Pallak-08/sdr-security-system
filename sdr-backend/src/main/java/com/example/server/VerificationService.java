package com.example.server;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class VerificationService {

    private final VerificationLogRepository logRepository;
    private static final String FILE_PATH = "devices.properties";

    private static final int MAX_FAILURES   = 3;
    private static final int LOCKOUT_MINUTES = 5;

    // per-deviceId failure tracking
    private final Map<String, Integer>       failureCount  = new HashMap<>();
    private final Map<String, LocalDateTime> lockedUntil   = new HashMap<>();

    public VerificationService(VerificationLogRepository logRepository) {
        this.logRepository = logRepository;
    }

    private Map<String, String> loadDevices() {
        Properties props = new Properties();
        try {
            File file = new File(FILE_PATH);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    props.load(fis);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Map<String, String> devices = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (!key.isBlank()) {
                devices.put(key, props.getProperty(key));
            }
        }
        return devices;
    }

    public Map<String, String> verifyDevice(String deviceId, String apiKey) {

        // --- lockout check ---
        if (lockedUntil.containsKey(deviceId)) {
            if (LocalDateTime.now().isBefore(lockedUntil.get(deviceId))) {
                logRepository.save(new VerificationLog(deviceId, "LOCKED", "Device locked after repeated failures"));
                return Map.of(
                    "status",  "LOCKED",
                    "message", "Device is temporarily locked due to multiple failed attempts. Try again in " + LOCKOUT_MINUTES + " minutes."
                );
            } else {
                // lockout expired — reset
                lockedUntil.remove(deviceId);
                failureCount.remove(deviceId);
            }
        }

        Map<String, String> validDevices = loadDevices();

        if (!validDevices.containsKey(deviceId)) {
            recordFailure(deviceId);
            logRepository.save(new VerificationLog(deviceId, "REJECTED", "Unknown device"));
            return Map.of("status", "REJECTED", "message", "Unknown device");
        }

        String validKey = validDevices.get(deviceId);

        if (validKey.equals(apiKey)) {
            // success — reset failure counter
            failureCount.remove(deviceId);
            lockedUntil.remove(deviceId);
            logRepository.save(new VerificationLog(deviceId, "APPROVED", ""));
            return Map.of("status", "APPROVED", "message", "Device verification successful");
        } else {
            recordFailure(deviceId);
            logRepository.save(new VerificationLog(deviceId, "REJECTED", "Invalid API key"));
            return Map.of("status", "REJECTED", "message", "Invalid API key");
        }
    }

    private void recordFailure(String deviceId) {
        int count = failureCount.getOrDefault(deviceId, 0) + 1;
        failureCount.put(deviceId, count);
        if (count >= MAX_FAILURES) {
            lockedUntil.put(deviceId, LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
            failureCount.remove(deviceId);
        }
    }

    public boolean isLocked(String deviceId) {
        if (!lockedUntil.containsKey(deviceId)) return false;
        if (LocalDateTime.now().isBefore(lockedUntil.get(deviceId))) return true;
        lockedUntil.remove(deviceId);
        failureCount.remove(deviceId);
        return false;
    }
}
