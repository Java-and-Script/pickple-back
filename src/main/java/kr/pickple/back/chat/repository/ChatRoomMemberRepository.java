package kr.pickple.back.chat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.chat.domain.ChatRoomMember;
import kr.pickple.back.member.domain.Member;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {

    Boolean existsByActiveTrueAndChatRoomAndMember(final ChatRoom chatRoom, final Member member);

    List<ChatRoomMember> findAllByMember(final Member member);

    List<ChatRoomMember> findAllByActiveTrueAndMember(final Member member);
}
