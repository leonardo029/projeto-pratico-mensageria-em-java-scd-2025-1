package com.ecommerce.inventoryservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    
    private UUID id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    
    private String items;
    
    private List<OrderItem> orderItems;
    
    public Order() {}
    
    public Order(UUID id, LocalDateTime createdAt, String items) {
        this.id = id;
        this.createdAt = createdAt;
        this.items = items;
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getItems() {
        return items;
    }
    
    public void setItems(String items) {
        this.items = items;
    }
    
    public List<OrderItem> getOrderItems() {
        if (orderItems != null) {
            return orderItems;
        }
        if (items == null) return List.of();
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(items, 
                mapper.getTypeFactory().constructCollectionType(List.class, OrderItem.class));
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
    
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
    
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", items='" + items + '\'' +
                ", orderItems=" + orderItems +
                '}';
    }
} 