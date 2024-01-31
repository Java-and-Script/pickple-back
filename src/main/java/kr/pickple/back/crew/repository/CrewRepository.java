package kr.pickple.back.crew.repository;

import static kr.pickple.back.crew.exception.CrewExceptionCode.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.pickple.back.address.domain.AddressDepth1;
import kr.pickple.back.address.domain.AddressDepth2;
import kr.pickple.back.chat.domain.ChatRoom;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.exception.CrewException;

public interface CrewRepository extends JpaRepository<Crew, Long> {

    Boolean existsByName(final String name);
    
    Page<Crew> findByAddressDepth1AndAddressDepth2(
            final AddressDepth1 addressDepth1,
            final AddressDepth2 addressDepth2,
            final Pageable pageable
    );

    Optional<Crew> findByChatRoom(final ChatRoom chatRoom);

    @Query("select c from Crew c left join CrewMember cm on c.leader.id = cm.member.id where cm.member.id = :id")
    List<Crew> findCreatedAllByMemberId(final Long id);

    default Crew getCrewById(final Long crewId) {
        return findById(crewId).orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
    }
}
