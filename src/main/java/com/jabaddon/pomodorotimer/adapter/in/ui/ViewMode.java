package com.jabaddon.pomodorotimer.adapter.in.ui;

/**
 * Represents the display mode of the timer UI.
 * This is a UI-layer concept, not part of the domain.
 */
public enum ViewMode {
    /**
     * Full view with all controls visible.
     * Displayed when the window has focus.
     */
    FULL,

    /**
     * Compact view showing only the timer.
     * Displayed when the window loses focus.
     * Always stays on top across all desktops.
     */
    COMPACT
}
