package com.pagamento.pix.infrastructure.integration;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SecurityLoggingAspect {

    public String maskSensitiveData(String input) {
        // Mascara CPF/CNPJ
        return input.replaceAll("\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", "***")
                   .replaceAll("\\d{2}\\.?\\d{3}\\.?\\d{3}/?\\d{4}-?\\d{2}", "***");
    }

    @Around("execution(* com.pagamento..*.*(..))")
    public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        
        // Mascarar dados sensíveis nos argumentos
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String stringArg) {
                args[i] = maskSensitiveData(stringArg);
            }
        }
        
        Object result = joinPoint.proceed(args);
        
        // Mascarar dados sensíveis no retorno
        if (result instanceof String stringResult) {
            return maskSensitiveData(stringResult);
        }
        return result;
    }
}