package kr.pickple.back.crew.controller;

import static org.springframework.http.HttpStatus.*;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import kr.pickple.back.auth.config.resolver.Login;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.CrewDomain;
import kr.pickple.back.crew.domain.NewCrew;
import kr.pickple.back.crew.dto.mapper.CrewRequestMapper;
import kr.pickple.back.crew.dto.mapper.CrewResponseMapper;
import kr.pickple.back.crew.dto.request.CrewCreateRequest;
import kr.pickple.back.crew.dto.request.CrewMemberUpdateStatusRequest;
import kr.pickple.back.crew.dto.response.CrewIdResponse;
import kr.pickple.back.crew.dto.response.CrewProfileResponse;
import kr.pickple.back.crew.service.CrewMemberService;
import kr.pickple.back.crew.service.CrewService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/crews")
public class CrewController {

    private final CrewService crewService;
    private final CrewMemberService crewMemberService;

    @PostMapping
    public ResponseEntity<CrewIdResponse> createCrew(
            @Login final Long loggedInMemberId,
            @Valid @RequestBody final CrewCreateRequest crewCreateRequest
    ) {
        final NewCrew newCrew = CrewRequestMapper.mapToNewCrewDomain(crewCreateRequest);
        final Long crewId = crewService.createCrew(loggedInMemberId, newCrew);
        final CrewIdResponse crewIdResponse = CrewResponseMapper.mapToCrewIdResponseDto(crewId);

        return ResponseEntity.status(CREATED)
                .body(crewIdResponse);
    }

    @GetMapping("/{crewId}")
    public ResponseEntity<CrewProfileResponse> findCrewById(
            @PathVariable final Long crewId
    ) {
        final CrewDomain crew = crewService.findCrewById(crewId);
        final CrewProfileResponse crewProfileResponse = CrewResponseMapper.mapToCrewProfileResponseDto(crew);

        return ResponseEntity.status(OK)
                .body(crewProfileResponse);
    }

    @PostMapping("/{crewId}/members")
    public ResponseEntity<Void> registerCrewMember(
            @Login final Long loggedInMemberId,
            @PathVariable final Long crewId
    ) {
        crewMemberService.registerCrewMember(crewId, loggedInMemberId);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @GetMapping("/{crewId}/members")
    public ResponseEntity<CrewProfileResponse> findAllCrewMembers(
            @Login final Long loggedInMemberId,
            @PathVariable final Long crewId,
            @RequestParam final RegistrationStatus status
    ) {
        return ResponseEntity.status(OK)
                .body(crewMemberService.findAllCrewMembers(loggedInMemberId, crewId, status));
    }

    @PatchMapping("/{crewId}/members/{memberId}")
    public ResponseEntity<Void> updateCrewMemberRegistrationStatus(
            @Login final Long loggedInMemberId,
            @PathVariable final Long crewId,
            @PathVariable final Long memberId,
            @Valid @RequestBody final CrewMemberUpdateStatusRequest crewMemberStatusUpdateRequest
    ) {
        crewMemberService.updateCrewMemberRegistrationStatus(
                loggedInMemberId,
                crewId,
                memberId,
                crewMemberStatusUpdateRequest
        );

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @DeleteMapping("/{crewId}/members/{memberId}")
    public ResponseEntity<Void> deleteCrewMember(
            @Login final Long loggedInMemberId,
            @PathVariable final Long crewId,
            @PathVariable final Long memberId
    ) {
        crewMemberService.deleteCrewMember(loggedInMemberId, crewId, memberId);

        return ResponseEntity.status(NO_CONTENT)
                .build();
    }

    @GetMapping
    public ResponseEntity<List<CrewProfileResponse>> findCrewsByAddress(
            @RequestParam final String addressDepth1,
            @RequestParam final String addressDepth2,
            final Pageable pageable
    ) {
        return ResponseEntity.status(OK)
                .body(crewService.findCrewsByAddress(addressDepth1, addressDepth2, pageable));
    }
}
