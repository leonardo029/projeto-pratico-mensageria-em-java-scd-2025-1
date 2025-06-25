package com.ecommerce.inventoryservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderItem {
    private String itemName;
    private Integer quantity;
    
    public OrderItem() {}
    
    @JsonCreator
    public OrderItem(@JsonProperty("itemName") String itemName, 
                     @JsonProperty("quantity") Integer quantity) {
        this.itemName = itemName;
        this.quantity = quantity;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    
    @Override
    public String toString() {
        return "OrderItem{" +
                "itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                '}';
    }
} 