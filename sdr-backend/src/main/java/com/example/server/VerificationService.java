package com.example.server;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

@Service
public class VerificationService {

    private final VerificationLogRepository logRepository;
    private static final String FILE_PATH = "devices.properties";

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
        Map<String, String> devices = new java.util.HashMap<>();
        for (String key : props.stringPropertyNames()) {
            devices.put(key, props.getProperty(key));
        }
        return devices;
    }

    public Map<String, String> verifyDevice(String deviceId, String apiKey) {

        Map<String, String> validDevices = loadDevices();

        if (!validDevices.containsKey(deviceId)) {
            logRepository.save(new VerificationLog(deviceId, "REJECTED", "Unknown device"));
            return Map.of("status", "REJECTED", "message", "Unknown device");
        }

        String validKey = validDevices.get(deviceId);

        if (validKey.equals(apiKey)) {
            logRepository.save(new VerificationLog(deviceId, "APPROVED", ""));
            return Map.of("status", "APPROVED", "message", "Device verification successful");
        } else {
            logRepository.save(new VerificationLog(deviceId, "REJECTED", "Invalid API key"));
            return Map.of("status", "REJECTED", "message", "Invalid API key");
        }
    }
}