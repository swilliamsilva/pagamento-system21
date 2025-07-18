package com.pagamento.common.observability;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;



@Aspect
@Component
public class LoggingAspect {

    // Exceção dedicada para erros de execução de métodos
    public static class MethodExecutionException extends RuntimeException {
        public MethodExecutionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @Around("execution(public * com.pagamento..*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("🔹 Iniciando execução: {}", methodName);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            logger.info(" Finalizado: {} | Tempo: {} ms", methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
        	
        	
        	
            // Convertendo argumentos para string de forma segura
            String argsString = safeArgsToString(args);
            String errorMessage = "❌ Erro em " + methodName + " - Argumentos: " + argsString;
            
            // Logando a exceção com contexto completo
            logger.error(errorMessage, e);
            
            // Lançando exceção com informação contextual
            throw new MethodExecutionException(errorMessage, e);
        }
    }

    /**
     * Converte os argumentos para string de forma segura, tratando possíveis exceções
     * durante a conversão de objetos individuais.
     */
    private String safeArgsToString(Object[] args) {
        if (args == null) {
            return "null";
        }
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            
            try {
                sb.append(args[i] != null ? args[i].toString() : "null");
            } catch (Exception ex) {
                sb.append("{erro na conversão: ").append(ex.getMessage()).append("}");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}