package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.repository.SseEmitterRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final SseEmitterRepository sseEmitterRepository;
    private final MemberRepository memberRepository;

    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
        final Member member = findMemberById(loggedInMemberId);
        final SseEmitter emitter = new SseEmitter();

        try {
            emitter.send(SseEmitter.event()
                    .name("AlarmSseConnect")
                    .data("사용자에 대한 알람 SSE 연결이 정상적으로 처리되었습니다."));
        } catch (IOException e) {
            sseEmitterRepository.deleteById(loggedInMemberId);
            emitter.completeWithError(e);
        }

        sseEmitterRepository.save(String.valueOf(loggedInMemberId), emitter);
        return emitter;
    }

    public void notify(final Long loggedInMemberId, final Object event) {
        try {
            sseEmitterRepository.notify(loggedInMemberId, event);
        } catch (Exception e) {
            sseEmitterRepository.deleteById(loggedInMemberId);
            log.error("알림 전송 중 오류가 발생했습니다.", e);
        }
    }

    private Member findMemberById(final Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND, memberId));
    }

    public void deleteById(final Long id) {
        sseEmitterRepository.deleteById(id);
        log.info("아이디 {}에 해당하는 Emitter가 성공적으로 삭제되었습니다.", id);
    }
}
