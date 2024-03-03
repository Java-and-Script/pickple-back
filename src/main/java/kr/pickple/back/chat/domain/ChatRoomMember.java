package kr.pickple.back.chat.domain;

import static java.lang.Boolean.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import kr.pickple.back.common.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "chat_room_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = {"memberId", "chatRoomId"}, callSuper = false)
public class ChatRoomMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private Boolean active = TRUE;

    @NotNull
    @Column(name = "member_id")
    private Long memberId;

    @NotNull
    @Column(name = "chat_room_id")
    private Long chatRoomId;

    @Builder
    private ChatRoomMember(final Long memberId, final Long chatRoomId) {
        this.memberId = memberId;
        this.chatRoomId = chatRoomId;
    }

    public void activate() {
        active = TRUE;
    }

    public void deactivate() {
        active = FALSE;
    }
}
