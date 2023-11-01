package kr.pickple.back.game.dto.validator;

import java.util.Arrays;
import java.util.List;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import kr.pickple.back.position.domain.Position;

public class PositionsValidator implements ConstraintValidator<PositionsValid, List<String>> {

    private static final List<String> positionAcronyms = Arrays.stream(Position.values())
            .map(Position::getAcronym)
            .toList();

    @Override
    public void initialize(PositionsValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<String> positions, ConstraintValidatorContext context) {
        if (positions == null) {
            return false;
        }

        if (hasDuplicate(positions)) {
            return false;
        }

        if (hasNotValidAcronyms(positions)) {
            return false;
        }

        return true;
    }

    private boolean hasDuplicate(List<String> positions) {
        List<String> distinctPositions = positions.stream()
                .distinct()
                .toList();

        return positions.size() != distinctPositions.size();
    }

    private boolean hasNotValidAcronyms(List<String> positions) {
        return positions.stream()
                .anyMatch(position -> !positionAcronyms.contains(position));
    }
}
