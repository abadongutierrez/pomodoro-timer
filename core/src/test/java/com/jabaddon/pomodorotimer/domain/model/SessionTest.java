package com.jabaddon.pomodorotimer.domain.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Session Domain Object Tests")
class SessionTest {

    // private Session session;

    // @BeforeEach
    // void setUp() {
    //     session = new Session();
    // }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize with default values")
        void shouldInitializeWithDefaults() {
            Session session = new Session();

            assertThat(session.getCompletedPomodoros(), is(equalTo(0)));
            assertThat(session.getCurrentCycle(), is(equalTo(0)));
            assertThat(session.getCurrentSessionType(), is(equalTo(SessionType.WORK)));
            assertThat(session.isTimerRunning(), is(false));
            assertThat(session.isTimerPaused(), is(false));
        }

        @Test
        @DisplayName("Should initialize from today's statistics")
        void shouldInitializeFromTodayStats() {
            Session session = new Session();
            session.initializeFromTodayStats(new DailyStatistics(
                java.time.LocalDate.now(),
                2
            ));
            assertThat(session.getCompletedPomodoros(), is(equalTo(2)));
            assertThat(session.getCurrentCycle(), is(equalTo(2)));
            assertThat(session.getCurrentSessionType(), is(equalTo(SessionType.WORK)));
            assertThat(session.isTimerRunning(), is(false));
            assertThat(session.isTimerPaused(), is(false)); 
        }
    }

    @Nested
    @DisplayName("When session object is created")
    class CompleteWorkSessionTests {
        
        private Session session;
        private TestEventHandler eventHandler;
        private int completePomodoros;

        @BeforeEach
        void setUp() {
            eventHandler = new TestEventHandler();
            session = new Session(eventHandler);
            completePomodoros = session.getCompletedPomodoros();
        }

        @Nested
        @DisplayName("Normal session starts")
        class SessionStartedTests {

            @BeforeEach
            void setUp() {
                session.startSession();
            }

            @Test
            @DisplayName("Should invoke event handler on session started")
            void shouldInvokeEventHandlerOnSessionStarted() {
                assertThat(eventHandler.onSessionStartedCalled, is(true));
            }

            @Test
            @DisplayName("Should have zero completed pomodoros and cycle at zero")
            void shouldHaveZeroCompletedPomodorosAndCycle() {
                assertThat(session.getCompletedPomodoros(), is(equalTo(0)));
                assertThat(session.getCurrentCycle(), is(equalTo(0)));
            }

            @Test
            @DisplayName("Should have current session type as WORK")
            void shouldHaveWorkAsCurrentSessionType() {
                assertThat(session.getCurrentSessionType(), is(equalTo(SessionType.WORK)));
            }

            @Test
            @DisplayName("Should have timer in correct state")
            void shouldHaveTimerInCorrectState() {
                assertThat(session.isTimerRunning(), is(true));
                assertThat(session.isTimerPaused(), is(false));
                assertThat(session.isTimerStarted(), is(true));
            }

            @Test
            @DisplayName("Should not start session again if timer is already running")
            void shouldNotStartSessionIfTimerIsRunning() {
                assertThat(session.startSession(), is(false));
            }

            @Nested
            @DisplayName("After ticking just before completion")
            class AfterTickingJustBeforeCompletion {
                List<Boolean> tickResults = new ArrayList<>();

                @BeforeEach
                void setUp() {
                    int minutes = session.getCurrentSessionTypeMinutes();
                    int totalTicksBeforeCompletion = (minutes * 60) - 1;
                    for (int i = 0; i < totalTicksBeforeCompletion; i++) {
                        tickResults.add(session.tick());
                    }
                }

                @Test
                @DisplayName("Should have all ticks return false indicating not completed")
                void shouldHaveAllTicksReturnFalse() {
                    assertThat(tickResults.stream().allMatch(result -> result == false), is(true));
                }

                @Test
                @DisplayName("Should not be completed yet")
                void shouldStillBeRunning() {
                    assertThat(session.isTimerRunning(), is(true));
                    assertThat(session.isTimerPaused(), is(false));
                    assertThat(session.isTimerCompleted(), is(false));
                    assertThat(session.isTimerStarted(), is(true));
                }

                /**
                 * When session/timer is still running and not completed this should fail
                 */
                @Test
                @DisplayName("Should fail if trying to handle completion")
                void shouldFailIfTryingToHandleCompletion() {
                    assertThrows(IllegalStateException.class, () -> {
                        session.handleTimerCompletion();
                    });
                }

                @Nested
                @DisplayName("After ticking to complete the session")
                class AfterTickingToCompleteSession {
                    boolean tickResult;

                    @BeforeEach
                    void setUp() {
                        tickResult = session.tick();
                    }

                    @Test
                    @DisplayName("Should have tick return true indicating completion")
                    void shouldHaveTickReturnTrue() {
                        assertThat(tickResult, is(true));
                    }

                    @Test
                    @DisplayName("Should mark timer as completed and not running")
                    void shouldMarkTimerAsCompleted() {
                        assertThat(session.isTimerRunning(), is(false));
                        assertThat(session.isTimerPaused(), is(false));
                        assertThat(session.isTimerCompleted(), is(true));
                        assertThat(session.isTimerStarted(), is(true));
                    }

                    @Nested
                    @DisplayName("After handle the completion")
                    class AfterHandleCompletion {
                        @BeforeEach
                        void setUp() {
                            session.handleTimerCompletion();
                        }

                        @Test
                        @DisplayName("Should increment completed pomodoros and cycle")
                        void  shouldIncrementCompletedPomodorosAndCycle() {
                            assertThat(session.getCompletedPomodoros(), is(equalTo(completePomodoros + 1)));
                        }

                        @Test
                        @DisplayName("Should have current session type as SHORT_BREAK")
                        void shouldHaveCurrentSessionTypeAsShortBreak() {
                            assertThat(session.getCurrentSessionType(), is(equalTo(SessionType.SHORT_BREAK)));
                        }

                        /**
                         * Even though session was completed, timer should still be in same state
                         * Timer state changes when new session starts
                         */
                        @Test
                        @DisplayName("Should still have timer as completed and not running")
                        void shouldStillHaveTimerAsCompleted() {
                            assertThat(session.isTimerRunning(), is(false));
                            assertThat(session.isTimerPaused(), is(false));
                            assertThat(session.isTimerCompleted(), is(true));
                            assertThat(session.isTimerStarted(), is(true));
                        }
                    }
                }
            }
        }
    }

    @Nested
    @DisplayName("Timer Control Tests")
    class TimerControlTests {

        @Test
        @DisplayName("Should start session with default minutes")
        void shouldStartSessionWithDefaults() {
            Session session = new Session();
            assertThat(session.startSession(), is(true));
            assertThat(session.isTimerRunning(), is(true));
        }

        @Test
        @DisplayName("Should not start session if timer is already running")
        void shouldNotStartIfAlreadyRunning() {
            Session session = new Session();
            session.startSession();
            assertThat(session.startSession(), is(false));
        }

        @Test
        @DisplayName("Should start custom session with specified minutes")
        void shouldStartCustomSession() {
            Session session = new Session();
            assertThat(session.startCustomSession(10), is(true));
            assertThat(session.isTimerRunning(), is(true));
        }

        @Test
        @DisplayName("Should not start custom session if timer is already running")
        void shouldNotStartCustomIfAlreadyRunning() {
            Session session = new Session();
            session.startSession();
            assertThat(session.startCustomSession(10), is(false));
        }

        @Test
        @DisplayName("Should pause running timer")
        void shouldPauseRunningTimer() {
            Session session = new Session();
            session.startSession();
            assertThat(session.pauseTimer(), is(true));
            assertThat(session.isTimerPaused(), is(true));
        }

        @Test
        @DisplayName("Should not pause if timer is not running")
        void shouldNotPauseIfNotRunning() {
            Session session = new Session();
            assertThat(session.pauseTimer(), is(false));
        }

        @Test
        @DisplayName("Should resume paused timer")
        void shouldResumePausedTimer() {
            Session session = new Session();
            session.startSession();
            session.pauseTimer();
            assertThat(session.resumeTimer(), is(true));
            assertThat(session.isTimerRunning(), is(true));
        }

        @Test
        @DisplayName("Should not resume if timer is not paused")
        void shouldNotResumeIfNotPaused() {
            Session session = new Session();
            assertThat(session.resumeTimer(), is(false));
        }

        @Test
        @DisplayName("Should tick timer")
        void shouldTickTimer() {
            Session session = new Session();
            session.startSession();
            boolean result = session.tick();
            // Tick should return false when timer is not completed
            assertThat(result, is(false));
        }
    }

    @Nested
    @DisplayName("Timer Record Creation Tests")
    class TimerRecordCreationTests {

        @Test
        @DisplayName("Should create timer record with current state")
        void shouldCreateTimerRecord() {
            Session session = new Session();
            session.startSession();
            LocalDateTime finishTime = LocalDateTime.now();

            TimerRecord record = session.createTimerRecord(finishTime);

            assertThat(record.getStartedAt(), is(notNullValue()));
            assertThat(record.getFinishedAt(), is(equalTo(finishTime)));
            assertThat(record.getSessionType(), is(equalTo(SessionType.WORK)));
        }
    }

    @Nested
    @DisplayName("Event Handler Tests")
    class EventHandlerTests {

        @Test
        @DisplayName("Should invoke event handler on session start")
        void shouldInvokeEventHandlerOnStart() {
            TestEventHandler eventHandler = new TestEventHandler();
            Session sessionWithHandler = new Session(eventHandler);

            sessionWithHandler.startSession();

            assertThat(eventHandler.onSessionStartedCalled, is(true));
        }

        @Test
        @DisplayName("Should invoke event handler on custom session start")
        void shouldInvokeEventHandlerOnCustomStart() {
            TestEventHandler eventHandler = new TestEventHandler();
            Session sessionWithHandler = new Session(eventHandler);

            sessionWithHandler.startCustomSession(15);

            assertThat(eventHandler.onSessionStartedCalled, is(true));
            assertThat(eventHandler.minutes, is(equalTo(15)));
        }
    }

    // Test helper class
    private static class TestEventHandler implements SessionDomainEventHandler {
        boolean onSessionStartedCalled = false;
        SessionType sessionType;
        int minutes;

        @Override
        public void onSessionStarted(SessionType sessionType, int minutes) {
            onSessionStartedCalled = true;
            this.sessionType = sessionType;
            this.minutes = minutes;
        }
    }
}
