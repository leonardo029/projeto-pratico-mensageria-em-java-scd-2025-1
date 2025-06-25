package com.ecommerce.inventoryservice.listener;

import com.ecommerce.inventoryservice.model.Order;
import com.ecommerce.inventoryservice.service.InventoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderListener.class);
    
    private final InventoryService inventoryService;
    
    public OrderListener(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @KafkaListener(topics = "${topics.orders}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleOrder(@Payload Order order, 
                           @Header(KafkaHeaders.RECEIVED_KEY) String key,
                           @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                           @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        
        logger.info("Received order from topic: {}, partition: {}, key: {}, order: {}", 
                   topic, partition, key, order.getId());
        
        try {
            inventoryService.processOrder(order);
        } catch (Exception e) {
            logger.error("Error processing order {}: {}", order.getId(), e.getMessage(), e);
        }
    }
} 