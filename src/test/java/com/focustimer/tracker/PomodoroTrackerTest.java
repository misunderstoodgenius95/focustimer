package com.focustimer.tracker;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PomodoroTrackerTest {

    @Test
    void countsPomodorosSeparatelyPerLabel() {
        PomodoroTracker tracker = new PomodoroTracker();

        tracker.recordFocusCompleted("Studio", Duration.ofMinutes(25));
        tracker.recordFocusCompleted("Studio", Duration.ofMinutes(25));
        tracker.recordFocusCompleted("Lavoro", Duration.ofMinutes(25));

        assertEquals(2, tracker.getPomodoroCount("Studio"));
        assertEquals(1, tracker.getPomodoroCount("Lavoro"));
        assertEquals(0, tracker.getPomodoroCount("Etichetta inesistente"));
        assertEquals(3, tracker.getTotalPomodoroCount());
    }

    @Test
    void accumulatesTotalBreakTimeAcrossLabels() {
        PomodoroTracker tracker = new PomodoroTracker();

        tracker.recordFocusCompleted("Studio", Duration.ofMinutes(25));
        tracker.recordBreakCompleted(Duration.ofMinutes(5));
        tracker.recordFocusCompleted("Lavoro", Duration.ofMinutes(25));
        tracker.recordBreakCompleted(Duration.ofMinutes(10));

        assertEquals(Duration.ofMinutes(15), tracker.getTotalBreakTime());
    }
}
