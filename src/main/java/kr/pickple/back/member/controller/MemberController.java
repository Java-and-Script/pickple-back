package kr.pickple.back.member.controller;

import static org.springframework.http.HttpStatus.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.pickple.back.member.dto.AuthenticatedMemberResponse;
import kr.pickple.back.member.dto.MemberCreateRequest;
import kr.pickple.back.member.service.MemberService;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<AuthenticatedMemberResponse> createMember(
            @RequestBody MemberCreateRequest memberCreateRequest
    ) {
        return ResponseEntity.status(CREATED)
                .body(memberService.createMember(memberCreateRequest));
    }
}
