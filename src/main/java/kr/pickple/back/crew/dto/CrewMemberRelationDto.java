package kr.pickple.back.crew.dto;

import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.CrewMember;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor(staticName = "from")
public class CrewMemberRelationDto {
    private final Long id;
    private final RegistrationStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final Long memberId;
    private final Long crewId;

    public static final CrewMemberRelationDto fromEntity(final CrewMember crewMember) {
        return CrewMemberRelationDto.from(
                crewMember.getId(),
                crewMember.getStatus(),
                crewMember.getCreatedAt(),
                crewMember.getUpdatedAt(),
                crewMember.getMember().getId(),
                crewMember.getCrew().getId()
        );
    }
}
