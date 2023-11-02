package kr.pickple.back.game.dto.validator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        final Set<String> distinctPositions = new HashSet<>(positions);

        return positions.size() != distinctPositions.size();
    }

    private boolean isInValidAcronyms(final List<String> positions) {
        return positions.stream()
                .anyMatch(position -> !positionAcronyms.contains(position));
    }
}
