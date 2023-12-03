package kr.pickple.back.alarm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.pickple.back.alarm.dto.response.AlarmResponse;
import kr.pickple.back.alarm.dto.response.CrewAlarmResponse;
import kr.pickple.back.alarm.dto.response.GameAlarmResponse;
import kr.pickple.back.alarm.exception.AlarmException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import java.util.Map;

import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_CONVERT_TYPE_NOT_FOUND;
import static kr.pickple.back.alarm.exception.AlarmExceptionCode.ALARM_TYPE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RedisAlarmSubscriber implements MessageListener {

    private final SseEmitterService sseEmitterService;
    private final Map<Character, Class<? extends AlarmResponse>> alarmTypeMap = Map.of(
            'C', CrewAlarmResponse.class,
            'G', GameAlarmResponse.class
    );

    public void onMessage(final Message message, final byte[] pattern) {
        final String receivedMessage = message.toString();
        final Long memberId = Long.valueOf(receivedMessage.split(":")[0]);
        final String messageContent = receivedMessage.split(":")[1];

        final AlarmResponse alarmResponse = convertStringToAlarmResponse(messageContent);
        if (alarmResponse != null) {
            sseEmitterService.sendAlarm(memberId, alarmResponse);
        }
    }

    private AlarmResponse convertStringToAlarmResponse(final String actualMessage) {
        final ObjectMapper mapper = new ObjectMapper();
        final Class<? extends AlarmResponse> alarmResponseType = alarmTypeMap.get(actualMessage.charAt(0));

        if (alarmResponseType != null) {
            try {
                return mapper.readValue(actualMessage.substring(1), alarmResponseType);
            } catch (JsonProcessingException e) {
                throw new AlarmException(ALARM_CONVERT_TYPE_NOT_FOUND, alarmResponseType.getSimpleName());
            }
        }
        throw new AlarmException(ALARM_TYPE_NOT_FOUND, alarmResponseType);
    }
}
