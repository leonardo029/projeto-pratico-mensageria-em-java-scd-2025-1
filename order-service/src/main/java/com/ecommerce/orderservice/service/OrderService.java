package com.ecommerce.orderservice.service;

import com.ecommerce.orderservice.model.Order;
import com.ecommerce.orderservice.model.OrderItem;
import com.ecommerce.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String ordersTopic;
    
    public OrderService(OrderRepository orderRepository, 
                       KafkaTemplate<String, Object> kafkaTemplate,
                       @Value("${topics.orders}") String ordersTopic) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.ordersTopic = ordersTopic;
    }
    
    public Order createOrder(List<OrderItem> items) {
        Order order = new Order(items);
        Order savedOrder = orderRepository.save(order);
        
        logger.info("Order created with ID: {}", savedOrder.getId());
        
        try {
            kafkaTemplate.send(ordersTopic, savedOrder.getId().toString(), savedOrder);
            logger.info("Order published to Kafka topic: {}", ordersTopic);
        } catch (Exception e) {
            logger.error("Failed to publish order to Kafka: {}", e.getMessage());
        }
        
        return savedOrder;
    }
    
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
} 