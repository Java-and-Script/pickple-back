package kr.pickple.back.chat.domain;

import static kr.pickple.back.chat.exception.ChatExceptionCode.*;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.util.RoomTypeAttributeConverter;
import kr.pickple.back.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(length = 20)
    private String name;

    @NotNull
    @Convert(converter = RoomTypeAttributeConverter.class)
    private RoomType type;

    @NotNull
    private Integer memberCount = 0;

    @NotNull
    private Integer maxMemberCount = 2;

    @Builder
    private ChatRoom(final String name, final RoomType type, final Integer maxMemberCount) {
        this.name = name;
        this.type = type;
        this.maxMemberCount = maxMemberCount;
    }

    public void updateMaxMemberCount(final Integer maxMemberCount) {
        if (maxMemberCount < memberCount) {
            throw new ChatException(CHAT_MAX_MEMBER_COUNT_SHOULD_BE_BIGGER_THAN_MEMBER_COUNT, maxMemberCount);
        }

        this.maxMemberCount = maxMemberCount;
    }
}
