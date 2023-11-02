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
    public void initialize(final PositionsValid constraintAnnotation) {
    }

    @Override
    public boolean isValid(final List<String> positions, final ConstraintValidatorContext context) {
        if (positions == null || isDuplicate(positions) || isInValidAcronyms(positions)) {
            return false;
        }

        return true;
    }

    private boolean isDuplicate(final List<String> positions) {
        List<String> distinctPositions = positions.stream()
                .distinct()
                .toList();

        return positions.size() != distinctPositions.size();
    }

    private boolean isInValidAcronyms(final List<String> positions) {
        return positions.stream()
                .anyMatch(position -> !positionAcronyms.contains(position));
    }
}
