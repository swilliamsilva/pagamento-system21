/* ========================================================
# Classe: LoggingAspect
# Módulo: pagamento-common-observability
# Autor: William Silva
# Contato: williamsilva.codigo@gmail.com
# Website: simuleagora.com
# ======================================================== */

package com.pagamento.common.observability;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Aspecto para logar entrada, saída e tempo de execução dos métodos públicos nos serviços.
 */
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(public * com.pagamento..*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Logger logger = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
        String methodName = joinPoint.getSignature().getName();

        logger.info("🔹 Iniciando execução: {}", methodName);
        long start = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            logger.info("✅ Finalizado: {} | Tempo: {} ms", methodName, System.currentTimeMillis() - start);
            return result;
        } catch (Throwable e) {
            logger.error("❌ Erro em {}: {}", methodName, e.getMessage());
            throw e;
        }
    }
}
