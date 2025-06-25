package com.ecommerce.orderservice.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "items", columnDefinition = "TEXT")
    private String items;
    
    public Order() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Order(List<OrderItem> orderItems) {
        this();
        setOrderItems(orderItems);
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
        try {
            ObjectMapper mapper = new ObjectMapper();
            this.items = mapper.writeValueAsString(orderItems);
        } catch (JsonProcessingException e) {
            this.items = "[]";
        }
    }
} 