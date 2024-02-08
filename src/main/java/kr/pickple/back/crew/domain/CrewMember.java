package kr.pickple.back.crew.domain;

import static kr.pickple.back.common.domain.RegistrationStatus.*;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.common.domain.BaseEntity;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.common.util.RegistrationStatusAttributeConverter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CrewMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Convert(converter = RegistrationStatusAttributeConverter.class)
    @Column(length = 10)
    private RegistrationStatus status = WAITING;

    @NotNull
    private Long memberId;

    @NotNull
    private Long crewId;

    @Builder
    private CrewMember(final Long memberId, final Long crewId) {
        this.memberId = memberId;
        this.crewId = crewId;
    }

    public void confirmRegistration() {
        this.status = CONFIRMED;
    }

    public void updateStatus(final RegistrationStatus status) {
        this.status = status;
    }

    public ChatRoom getCrewChatRoom() {
        return crew.getChatRoom();
    }
}
