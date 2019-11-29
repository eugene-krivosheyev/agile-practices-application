package com.acme.dbo.config;

import java.time.LocalDateTime;

public class ErrorInfo {
    public final LocalDateTime timestamp;
    public final int status;
    public final String message;

    public ErrorInfo(LocalDateTime timestamp, int status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }
}
