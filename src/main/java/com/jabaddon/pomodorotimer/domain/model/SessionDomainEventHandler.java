package com.jabaddon.pomodorotimer.domain.model;

public interface SessionDomainEventHandler {

    public void onSessionStarted();

    public void onCustomSessionStarted(int minutes);

}
