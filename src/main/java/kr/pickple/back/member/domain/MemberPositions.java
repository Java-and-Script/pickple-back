package kr.pickple.back.member.domain;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.position.domain.Position;

@Embeddable
public class MemberPositions {

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<MemberPosition> memberPositions = new ArrayList<>();

    public List<Position> getPositions() {
        return memberPositions.stream()
                .map(MemberPosition::getPosition)
                .toList();
    }

    public void updateMemberPositions(final Member member, final List<Position> positions) {
        validateIsDuplicatedPositions(positions);

        positions.stream()
                .map(position -> buildMemberPosition(member, position))
                .forEach(memberPositions::add);
    }

    private void validateIsDuplicatedPositions(final List<Position> positions) {
        long distinctPositionsSize = new HashSet<>(positions).size();

        if (distinctPositionsSize < positions.size()) {
            throw new MemberException(MEMBER_POSITIONS_IS_DUPLICATED, positions);
        }
    }

    private MemberPosition buildMemberPosition(final Member member, final Position position) {
        return MemberPosition.builder()
                .position(position)
                .member(member)
                .build();
    }
}
