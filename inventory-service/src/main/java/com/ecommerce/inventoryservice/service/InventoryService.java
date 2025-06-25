package com.ecommerce.inventoryservice.service;

import com.ecommerce.inventoryservice.model.*;
import com.ecommerce.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    
    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String inventoryEventsTopic;
    
    public InventoryService(InventoryRepository inventoryRepository,
                           KafkaTemplate<String, Object> kafkaTemplate,
                           @Value("${topics.inventory-events}") String inventoryEventsTopic) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryEventsTopic = inventoryEventsTopic;
    }
    
    @Transactional
    public void processOrder(Order order) {
        logger.info("Processing order: {}", order.getId());
        
        try {
            List<OrderItem> orderItems = order.getOrderItems();
            boolean allItemsAvailable = true;
            StringBuilder resultMessage = new StringBuilder();
            
            for (OrderItem item : orderItems) {
                Optional<Inventory> inventoryOpt = inventoryRepository.findByItemName(item.getItemName());
                
                if (inventoryOpt.isEmpty()) {
                    allItemsAvailable = false;
                    resultMessage.append("Item not found: ").append(item.getItemName()).append("; ");
                } else {
                    Inventory inventory = inventoryOpt.get();
                    if (inventory.getQuantity() < item.getQuantity()) {
                        allItemsAvailable = false;
                        resultMessage.append("Insufficient stock for ").append(item.getItemName())
                                   .append(" (requested: ").append(item.getQuantity())
                                   .append(", available: ").append(inventory.getQuantity()).append("); ");
                    }
                }
            }
            
            if (!allItemsAvailable) {
                InventoryEvent failureEvent = InventoryEvent.failure(order.getId(), 
                    "Order failed: " + resultMessage.toString());
                publishInventoryEvent(failureEvent);
                return;
            }
            
            for (OrderItem item : orderItems) {
                Optional<Inventory> inventoryOpt = inventoryRepository.findByItemName(item.getItemName());
                if (inventoryOpt.isPresent()) {
                    Inventory inventory = inventoryOpt.get();
                    inventory.setQuantity(inventory.getQuantity() - item.getQuantity());
                    inventoryRepository.save(inventory);
                    
                    logger.info("Updated inventory for {}: new quantity = {}", 
                              item.getItemName(), inventory.getQuantity());
                }
            }
            
            InventoryEvent successEvent = InventoryEvent.success(order.getId(), 
                "Order processed successfully. All items reserved.");
            publishInventoryEvent(successEvent);
            
        } catch (Exception e) {
            logger.error("Error processing order {}: {}", order.getId(), e.getMessage());
            InventoryEvent errorEvent = InventoryEvent.failure(order.getId(), 
                "System error while processing order: " + e.getMessage());
            publishInventoryEvent(errorEvent);
        }
    }
    
    private void publishInventoryEvent(InventoryEvent event) {
        try {
            kafkaTemplate.send(inventoryEventsTopic, event.getOrderId().toString(), event);
            logger.info("Published inventory event: {}", event);
        } catch (Exception e) {
            logger.error("Failed to publish inventory event: {}", e.getMessage());
        }
    }
}