package com.focustimer.tracker;

import com.focustimer.model.PomodoroSession;
import com.focustimer.model.SessionType;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Keeps track of completed pomodoros per label and the running total of
 * time spent on breaks, across all labels.
 */
public class PomodoroTracker {

    private final Map<String, Integer> pomodorosByLabel = new LinkedHashMap<>();
    private Duration totalBreakTime = Duration.ZERO;

    /** Records one completed focus interval for the given label. */
    public PomodoroSession recordFocusCompleted(String label, Duration duration) {
        pomodorosByLabel.merge(label, 1, Integer::sum);
        return new PomodoroSession(SessionType.FOCUS, label, duration, Instant.now());
    }

    /** Records one completed break interval and adds it to the running total. */
    public PomodoroSession recordBreakCompleted(Duration duration) {
        totalBreakTime = totalBreakTime.plus(duration);
        return new PomodoroSession(SessionType.BREAK, null, duration, Instant.now());
    }

    /** Number of pomodoros completed for a specific label. */
    public int getPomodoroCount(String label) {
        return pomodorosByLabel.getOrDefault(label, 0);
    }

    /** Number of pomodoros completed across all labels. */
    public int getTotalPomodoroCount() {
        return pomodorosByLabel.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** Read-only view of pomodoro counts per label, in the order labels were first used. */
    public Map<String, Integer> getPomodoroCountsByLabel() {
        return Collections.unmodifiableMap(pomodorosByLabel);
    }

    /** Total time spent on breaks so far, across all labels. */
    public Duration getTotalBreakTime() {
        return totalBreakTime;
    }
}
