package kr.pickple.back.alarm.service;

public interface AlarmPublisher {

    void publish(final Long memberId, final String alarm);
}
