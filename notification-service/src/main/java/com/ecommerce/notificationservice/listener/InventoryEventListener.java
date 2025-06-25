package com.ecommerce.notificationservice.listener;

import com.ecommerce.notificationservice.model.InventoryEvent;
import com.ecommerce.notificationservice.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventListener {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryEventListener.class);
    
    private final NotificationService notificationService;
    
    public InventoryEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }
    
    @KafkaListener(topics = "${topics.inventory-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleInventoryEvent(@Payload InventoryEvent event,
                                   @Header(KafkaHeaders.RECEIVED_KEY) String key,
                                   @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                   @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        
        logger.info("Received inventory event from topic: {}, partition: {}, key: {}, event: {}", 
                   topic, partition, key, event.getOrderId());
        
        try {
            notificationService.sendNotification(event);
        } catch (Exception e) {
            logger.error("Error sending notification for order {}: {}", event.getOrderId(), e.getMessage(), e);
        }
    }
} 