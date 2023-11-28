package kr.pickple.back.member.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.OneToMany;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.domain.CrewMember;

@Embeddable
public class MemberCrews {

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<CrewMember> memberCrews = new ArrayList<>();

    public RegistrationStatus findRegistationStatus(final Crew crew) {
        return memberCrews.stream()
                .filter(memberCrew -> memberCrew.equalsCrew(crew))
                .findFirst()
                .map(CrewMember::getStatus)
                .orElse(RegistrationStatus.NONE);
    }

    public List<Crew> getCrewsByStatus(final RegistrationStatus status) {
        return memberCrews.stream()
                .filter(memberCrew -> memberCrew.equalsStatus(status))
                .map(CrewMember::getCrew)
                .toList();
    }

    public List<Crew> getCreatedCrewsByMember(final Member member) {
        return memberCrews.stream()
                .map(CrewMember::getCrew)
                .filter(crew -> crew.isLeader(member))
                .toList();
    }

    public void addMemberCrew(final CrewMember memberCrew) {
        memberCrews.add(memberCrew);
    }

    public Long getCreatedCrewsCountByMember(final Member member) {
        return memberCrews.stream()
                .map(CrewMember::getCrew)
                .filter(crew -> crew.isLeader(member))
                .count();
    }
}
