package kr.pickple.back.alarm.service;

import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.repository.SseEmitterRepository;
import kr.pickple.back.common.domain.RegistrationStatus;
import kr.pickple.back.crew.domain.Crew;
import kr.pickple.back.crew.exception.CrewException;
import kr.pickple.back.crew.repository.CrewRepository;
import kr.pickple.back.game.domain.Game;
import kr.pickple.back.game.exception.GameException;
import kr.pickple.back.game.repository.GameRepository;
import kr.pickple.back.member.domain.Member;
import kr.pickple.back.member.exception.MemberException;
import kr.pickple.back.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static kr.pickple.back.common.domain.RegistrationStatus.WAITING;
import static kr.pickple.back.crew.exception.CrewExceptionCode.CREW_NOT_FOUND;
import static kr.pickple.back.game.exception.GameExceptionCode.GAME_NOT_FOUND;
import static kr.pickple.back.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseEmitterService {

    private final CrewRepository crewRepository;
    private final GameRepository gameRepository;
    private final MemberRepository memberRepository;
    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter subscribeToSse(final Long loggedInMemberId) {
        final Member member = findMemberById(loggedInMemberId);
        final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

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
            notify(loggedInMemberId, event);
        } catch (Exception e) {
            sseEmitterRepository.saveEventCache(String.valueOf(loggedInMemberId), event);
            sseEmitterRepository.deleteById(loggedInMemberId);
            log.error("알림 전송 중 오류가 발생했습니다.", e);
        }
    }

    public void sendCachedEventToUser(final Long memberId) {
        Map<Long, Object> eventCache = sseEmitterRepository.findAllEventCacheStartWithByMemberId(memberId);
        if (eventCache != null && !eventCache.isEmpty()) {
            eventCache.values().forEach(event -> notify(memberId, event));
            sseEmitterRepository.deleteAllEventCacheStartWithId(memberId);
        }
    }

    public <T> void sendAlarm(final Long memberId, final T responseDto) {
        final Map<Class<?>, Consumer<T>> alarmResponseDto = new HashMap<>();

        alarmResponseDto.put(CrewAlarmResponse.class, response -> emitCrewMessage(memberId, (CrewAlarmResponse) response));
        alarmResponseDto.put(GameAlarmResponse.class, response -> emitGameMessage(memberId, (GameAlarmResponse) response));

        final Consumer<T> processor = alarmResponseDto.get(responseDto.getClass());

        if (processor != null) {
            try {
                processor.accept(responseDto);
            } catch (Exception e) {
                sseEmitterRepository.deleteById(memberId);
                log.error("알람 전송 중 오류가 발생했습니다. : " + memberId, e);
            }
        } else {
            log.error("알 수 없는 알람 타입입니다: " + responseDto.getClass().getSimpleName());
        }
    }

    public void emitCrewMessage(final Long memberId, final CrewAlarmResponse crewAlarm) {
        final Long crewId = crewAlarm.getCrewId();
        final Member crewLeader = getCrewLeaderOfCrew(crewId);
        final List<Member> crewApplyMembers = getCrewMembers(crewId, WAITING);

        sendAlarmToCrewLeader(crewLeader, crewAlarm);
        sendAlarmToCrewApplyMembers(crewApplyMembers, crewAlarm);
    }

    private void sendAlarmToMember(final Member member, final CrewAlarmResponse crewAlarm) {
        try {
            notify(member.getId(), crewAlarm);
        } catch (Exception e) {
            log.error("해당 회원에게 알람 전송 중 오류가 발생했습니다. : " + member.getId(), e);
            deleteById(member.getId());
        }
    }

    private void sendAlarmToCrewLeader(final Member leader, final CrewAlarmResponse crewAlarm) {
        sendAlarmToMember(leader, crewAlarm);
    }

    private void sendAlarmToCrewApplyMembers(final List<Member> members, final CrewAlarmResponse crewAlarm) {

        if (members == null) {
            log.debug("해당 크루에 가입 신청을 한 회원을 찾지 못하였습니다.");
            return;
        }
        members.forEach(member -> sendAlarmToMember(member, crewAlarm));
    }

    private Member getCrewLeaderOfCrew(final Long crewId) {
        final Crew crew = getCrewInfo(crewId);
        return crew.getLeader();
    }

    private Crew getCrewInfo(final Long crewId) {
        final Crew crew = crewRepository.findById(crewId)
                .orElseThrow(() -> new CrewException(CREW_NOT_FOUND, crewId));
        return crew;
    }

    private List<Member> getCrewMembers(final Long crewId, final RegistrationStatus status) {
        final Crew crew = getCrewInfo(crewId);
        return crew.getCrewMembers(status);
    }

    public void emitGameMessage(final Long memberId, final GameAlarmResponse gameAlarm) {
        final Long gameId = gameAlarm.getGameId();
        final Member gameHost = getHostOfGame(gameId);
        final List<Member> gameApplyMembers = getGameMembers(gameId, WAITING);

        sendAlarmToGameHost(gameHost, gameAlarm);
        sendAlarmToGameApplyMembers(gameApplyMembers, gameAlarm);
    }

    private void sendAlarmToMember(final Member member, final GameAlarmResponse gameAlarm) {
        try {
            notify(member.getId(), gameAlarm);
        } catch (Exception e) {
            log.info("해당 회원에게 알람 전송 중 오류가 발생되었습니다. : " + member.getId(), e);
        }
    }

    private void sendAlarmToGameHost(final Member gameHost, final GameAlarmResponse gameAlarm) {
        sendAlarmToMember(gameHost, gameAlarm);
    }

    private void sendAlarmToGameApplyMembers(final List<Member> members, final GameAlarmResponse gameAlarm) {

        if (members == null) {
            log.debug("해당 경기에 참여 신청을 한 회원을 찾지 못하였습니다.");
            return;
        }
        members.forEach(member -> sendAlarmToMember(member, gameAlarm));
    }

    private Member getHostOfGame(final Long gameId) {
        final Game game = getGameInfo(gameId);
        return game.getHost();
    }

    private List<Member> getGameMembers(final Long gameId, final RegistrationStatus status) {
        final Game game = getGameInfo(gameId);
        return game.getMembersByStatus(status);
    }

    private Game getGameInfo(final Long gameId) {
        final Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(GAME_NOT_FOUND, gameId));
        return game;
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
