// ==========================
// ANNOTATION: ValidCPF.java
// ==========================
package com.pagamento.common.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented

@Constraint(validatedBy = {CPFValidator.class})
/**
 * Type mismatch: cannot convert from Class<CPFValidator> to Class<? extends ConstraintValidator<?,?>>[]
 * 
 * ***/

@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidCPF {
    String message() default "CPF inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}