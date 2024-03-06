package kr.pickple.back.game.domain;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.position.domain.Position;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NewGame {

    private String content;
    private LocalDate playDate;
    private LocalTime playStartTime;
    private LocalTime playEndTime;
    private Integer playTimeMinutes;
    private String mainAddress;
    private String detailAddress;
    private Integer cost;
    private Integer maxMemberCount;
    private List<Position> positions;
    private String addressDepth1Name;
    private String addressDepth2Name;
    private MemberDomain host;
    private ChatRoom chatRoom;

    @Builder
    private NewGame(
            final String content,
            final LocalDate playDate,
            final LocalTime playStartTime,
            final LocalTime playEndTime,
            final Integer playTimeMinutes,
            final String mainAddress,
            final String detailAddress,
            final Integer cost,
            final Integer maxMemberCount,
            final List<Position> positions,
            final String addressDepth1Name,
            final String addressDepth2Name
    ) {
        this.content = content;
        this.playDate = playDate;
        this.playStartTime = playStartTime;
        this.playEndTime = playEndTime;
        this.playTimeMinutes = playTimeMinutes;
        this.mainAddress = mainAddress;
        this.detailAddress = detailAddress;
        this.cost = cost;
        this.maxMemberCount = maxMemberCount;
        this.positions = positions;
        this.addressDepth1Name = addressDepth1Name;
        this.addressDepth2Name = addressDepth2Name;
    }

    public void assignHost(final MemberDomain host) {
        this.host = host;
    }

    public void assignChatRoom(final ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }
}
