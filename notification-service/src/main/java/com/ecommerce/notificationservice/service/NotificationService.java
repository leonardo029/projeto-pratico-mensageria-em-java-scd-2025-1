package com.ecommerce.notificationservice.service;

import com.ecommerce.notificationservice.model.InventoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    public void sendNotification(InventoryEvent event) {
        String notificationType = "SUCCESS".equals(event.getStatus()) ? "SUCCESS" : "FAILURE";
        String customerEmail = "customer@example.com";
        
        if ("SUCCESS".equals(event.getStatus())) {
            sendSuccessNotification(event, customerEmail);
        } else {
            sendFailureNotification(event, customerEmail);
        }
    }
    
    private void sendSuccessNotification(InventoryEvent event, String customerEmail) {
        logger.info("NOTIFICAÇÃO POR EMAIL ENVIADA");
        logger.info("  Para: {}", customerEmail);
        logger.info("  Assunto: Confirmação do pedido - Pedido de nº #{}", event.getOrderId());
        logger.info("  Corpo: Ótimas notícias! Seu pedido de nº #{} foi processado com sucesso.", event.getOrderId());
        logger.info("        {}", event.getMessage());
        logger.info("        Seus itens foram reservados e serão enviados em breve!");
        logger.info("  Timestamp: {}", event.getTimestamp());
        logger.info("=============================================");
        
        logger.info("NOTIFICAÇÃO POR SMS ENVIADA");
        logger.info("  Mensagem: Seu pedido #{} foi confirmado e será enviado em breve!", event.getOrderId());
        logger.info("=============================================");
    }
    
    private void sendFailureNotification(InventoryEvent event, String customerEmail) {
        logger.warn("NOTIFICAÇÃO POR EMAIL ENVIADA");
        logger.warn("  Para: {}", customerEmail);
        logger.warn("  Assunto: Problema com o pedido - Pedido de nº #{}", event.getOrderId());
        logger.warn("  Corpo: Lamentamos mas houve um problema com seu pedido #{}.", event.getOrderId());
        logger.warn("        Problema: {}", event.getMessage());
        logger.warn("        Entre em contato com o atendimento ao cliente ou tente fazer seu pedido novamente.");
        logger.warn("  Timestamp: {}", event.getTimestamp());
        logger.warn("=============================================");
        
        logger.warn("NOTIFICAÇÃO POR SMS ENVIADA");
        logger.warn("  Mensagem: Problema com o pedido de nº #{}. Verifique seu e-mail para obter mais detalhes.", event.getOrderId());
        logger.warn("=============================================");
    }
}