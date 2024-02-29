package kr.pickple.back.member.controller;

import static kr.pickple.back.member.exception.MemberExceptionCode.*;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import kr.pickple.back.member.exception.MemberException;

@Aspect
@Component
public class IdentificationAspect {

    @Before(value = "@annotation(Identification) && args(loggedInMemberId, memberId, ..)", argNames = "loggedInMemberId, memberId")
    public void checkIdentification(
            final Long loggedInMemberId,
            final Long memberId
    ) {
        if (!loggedInMemberId.equals(memberId)) {
            throw new MemberException(MEMBER_MISMATCH, loggedInMemberId, memberId);
        }
    }
}
