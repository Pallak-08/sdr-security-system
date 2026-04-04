package com.example.server;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class VerificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deviceId;
    private String status;
    private String reason;
    private LocalDateTime timestamp;

    public VerificationLog() {}

    public VerificationLog(String deviceId, String status, String reason) {
        this.deviceId = deviceId;
        this.status = status;
        this.reason = reason;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}