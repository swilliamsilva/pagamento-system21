package com.pagamento.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.function.Supplier;

public final class PaymentLogger {
    private static final Logger LOG = LoggerFactory.getLogger(PaymentLogger.class);
    
    private PaymentLogger() {
        // Construtor privado para evitar instanciação
    }

    // Métodos para logging estruturado
    public static void error(String message, Throwable throwable) {
        LOG.error(message, throwable);
    }
    
    public static void error(String message, Object... args) {
        LOG.error(message, args);
    }
    
    public static void error(Throwable throwable, String message, Object... args) {
        LOG.error(formatMessage(message, args), throwable);
    }
    
    public static void warn(String message, Object... args) {
        LOG.warn(message, args);
    }
    
    public static void info(String message, Object... args) {
        LOG.info(message, args);
    }
    
    public static void debug(String message, Object... args) {
        LOG.debug(message, args);
    }
    
    public static void trace(String message, Object... args) {
        LOG.trace(message, args);
    }
    
    // Logging com contexto MDC
    public static void withContext(Map<String, String> context, Runnable loggingAction) {
        Map<String, String> previousContext = MDC.getCopyOfContextMap();
        
        try {
            if (context != null) {
                MDC.setContextMap(context);
            }
            loggingAction.run();
        } finally {
            if (previousContext != null) {
                MDC.setContextMap(previousContext);
            } else {
                MDC.clear();
            }
        }
    }
    
    // Logging condicional para melhor performance
    public static void debug(Supplier<String> messageSupplier) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(messageSupplier.get());
        }
    }
    
    public static void trace(Supplier<String> messageSupplier) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(messageSupplier.get());
        }
    }
    
    // Formatação de mensagens complexas
    private static String formatMessage(String template, Object... args) {
        try {
            return String.format(template, args);
        } catch (Exception e) {
            LOG.warn("Formatação de log inválida: '{}' com argumentos {}", template, args, e);
            return template;
        }
    }
}