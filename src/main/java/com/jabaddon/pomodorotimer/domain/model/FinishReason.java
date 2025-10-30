package com.jabaddon.pomodorotimer.domain.model;

/**
 * Enum representing the reason a timer session ended.
 */
public enum FinishReason {
    /**
     * Timer ran to completion (reached zero).
     */
    COMPLETED,

    /**
     * Timer was manually stopped or reset before completion.
     */
    STOPPED
}
