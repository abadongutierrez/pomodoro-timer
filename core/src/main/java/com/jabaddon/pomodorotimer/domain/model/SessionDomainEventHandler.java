package com.jabaddon.pomodorotimer.domain.model;

public interface SessionDomainEventHandler {

    public void onSessionStarted(SessionType sessionType, int minutes);
}
