package kr.pickple.back.chat.implement;

import static kr.pickple.back.chat.domain.RoomType.*;
import static kr.pickple.back.chat.exception.ChatExceptionCode.*;
import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import kr.pickple.back.address.dto.response.MainAddress;
import kr.pickple.back.address.implement.AddressReader;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomDomain;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.chat.domain.PersonalChatRoomStatus;
import kr.pickple.back.chat.exception.ChatException;
import kr.pickple.back.chat.repository.ChatRoomMemberRepository;
import kr.pickple.back.chat.repository.ChatRoomRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.domain.MemberDomain;
import kr.pickple.back.member.domain.MemberPosition;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.implement.MemberMapper;
import kr.pickple.back.member.repository.MemberPositionRepository;
import kr.pickple.back.member.repository.MemberRepository;
import kr.pickple.back.position.domain.Position;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatReader {

    private final AddressReader addressReader;
    private final MemberRepository memberRepository;
    private final MemberPositionRepository memberPositionRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;

    public ChatRoomDomain readRoom(final Long chatRoomId) {
        final ChatRoom chatRoomEntity = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND, chatRoomId));

        return ChatMapper.mapChatRoomEntityToDomain(chatRoomEntity);
    }

    public PersonalChatRoomStatus readPersonalRoomStatus(final Long senderId, final Long receiverId) {
        final ChatRoomMember receiverEntity = chatRoomMemberRepository.findAllByMemberId(senderId)
                .stream()
                .filter(chatRoomMemberEntity -> {
                    final ChatRoomDomain chatRoom = readRoom(chatRoomMemberEntity.getChatRoomId());
                    final Long chatRoomId = chatRoom.getChatRoomId();

                    return chatRoom.isMatchedRoomType(PERSONAL)
                            && chatRoomMemberRepository.existsByChatRoomIdAndMemberId(chatRoomId, receiverId);
                })
                .findFirst()
                .orElseThrow(() -> new ChatException(CHAT_ROOM_NOT_FOUND));

        return PersonalChatRoomStatus.builder()
                .roomId(receiverEntity.getChatRoomId())
                .isSenderActive(receiverEntity.getActive())
                .build();
    }

    public List<MemberDomain> readRoomMembers(final ChatRoom chatRoom) {
        return chatRoomMemberRepository.findAllByActiveTrueAndChatRoomId(chatRoom.getId())
                .stream()
                .map(chatRoomMember -> readMemberById(chatRoomMember.getMemberId()))
                .toList();
    }

    private MemberDomain readMemberById(final Long memberId) {
        final Member memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));

        final MainAddress mainAddress = addressReader.readMainAddressById(
                memberEntity.getAddressDepth1Id(),
                memberEntity.getAddressDepth2Id()
        );

        final List<Position> positions = memberPositionRepository.findAllByMemberId(memberId)
                .stream()
                .map(MemberPosition::getPosition)
                .toList();

        return MemberMapper.mapToMemberDomain(memberEntity, mainAddress, positions);
    }
}
