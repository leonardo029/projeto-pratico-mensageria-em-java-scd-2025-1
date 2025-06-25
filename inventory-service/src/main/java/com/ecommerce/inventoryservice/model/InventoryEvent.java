package com.ecommerce.inventoryservice.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class InventoryEvent {
    
    private UUID orderId;
    private String status;
    private String message;
    private LocalDateTime timestamp;
    
    public InventoryEvent() {
        this.timestamp = LocalDateTime.now();
    }
    
    public InventoryEvent(UUID orderId, String status, String message) {
        this();
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }
    
    public static InventoryEvent success(UUID orderId, String message) {
        return new InventoryEvent(orderId, "SUCCESS", message);
    }
    
    public static InventoryEvent failure(UUID orderId, String message) {
        return new InventoryEvent(orderId, "FAILURE", message);
    }
    
    public UUID getOrderId() {
        return orderId;
    }
    
    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    @Override
    public String toString() {
        return "InventoryEvent{" +
                "orderId=" + orderId +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
} 