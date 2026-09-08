package com.focustimer.model;

import java.time.Duration;
import java.time.Instant;

/** A single completed focus or break interval. */
public record PomodoroSession(SessionType type, String label, Duration duration, Instant completedAt) {
}
