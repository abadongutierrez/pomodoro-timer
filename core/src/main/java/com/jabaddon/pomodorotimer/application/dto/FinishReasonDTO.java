package com.jabaddon.pomodorotimer.application.dto;

/**
 * Application DTO representing the reason a timer session ended.
 * Used as a contract between application layer and adapters.
 */
public enum FinishReasonDTO {
    /**
     * Timer ran to completion (reached zero).
     */
    COMPLETED,

    /**
     * Timer was manually stopped or reset before completion.
     */
    STOPPED
}
