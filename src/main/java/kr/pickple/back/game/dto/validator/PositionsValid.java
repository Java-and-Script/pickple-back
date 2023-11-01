package kr.pickple.back.game.dto.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PositionsValidator.class)
public @interface PositionsValid {

    String message() default "포지션 목록은 null일 수 없음\n포지션 목록은 [C, PF, SF, PG, SG, 없음] 중 복수 선택. 같은 포지션을 2회 이상 중복 선택은 불가";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
