package com.focustimer;

import com.focustimer.timer.CountdownTimer;
import com.focustimer.tracker.PomodoroTracker;

import java.time.Duration;
import java.util.Scanner;

/**
 * Interactive command-line Pomodoro timer.
 *
 * Runs a focus timer followed by a break timer for a chosen label, keeps
 * a running total of time spent on breaks, and counts completed
 * pomodoros separately per label.
 */
public class Main {

    private static final Duration DEFAULT_FOCUS_DURATION = Duration.ofMinutes(25);
    private static final Duration DEFAULT_BREAK_DURATION = Duration.ofMinutes(5);

    public static void main(String[] args) {
        PomodoroTracker tracker = new PomodoroTracker();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Focus Timer (Pomodoro) ===");

        boolean keepGoing = true;
        while (keepGoing) {
            System.out.print("\nEtichetta per questo pomodoro (es. 'Progetto X'): ");
            String label = scanner.nextLine().trim();
            if (label.isEmpty()) {
                label = "Senza etichetta";
            }

            Duration focusDuration = askDuration(scanner, "Durata concentrazione in minuti", DEFAULT_FOCUS_DURATION);
            Duration breakDuration = askDuration(scanner, "Durata pausa in minuti", DEFAULT_BREAK_DURATION);

            try {
                runFocusTimer(label, focusDuration, tracker);
                runBreakTimer(breakDuration, tracker);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Timer interrotto.");
                break;
            }

            printSummary(tracker);

            System.out.print("\nVuoi iniziare un altro pomodoro? (s/n): ");
            keepGoing = scanner.nextLine().trim().equalsIgnoreCase("s");
        }

        System.out.println("\n=== Riepilogo finale ===");
        printSummary(tracker);
    }

    private static Duration askDuration(Scanner scanner, String prompt, Duration defaultValue) {
        System.out.printf("%s [default %d]: ", prompt, defaultValue.toMinutes());
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) {
            return defaultValue;
        }
        try {
            return Duration.ofMinutes(Long.parseLong(input));
        } catch (NumberFormatException e) {
            System.out.println("Valore non valido, uso il default.");
            return defaultValue;
        }
    }

    private static void runFocusTimer(String label, Duration duration, PomodoroTracker tracker)
            throws InterruptedException {
        System.out.printf("%n▶ Concentrazione su \"%s\" per %d minuti...%n", label, duration.toMinutes());
        new CountdownTimer().start(duration, remaining -> printCountdown("Focus", remaining));
        tracker.recordFocusCompleted(label, duration);
        System.out.println("\n✔ Pomodoro completato!");
    }

    private static void runBreakTimer(Duration duration, PomodoroTracker tracker) throws InterruptedException {
        System.out.printf("%n▶ Pausa di %d minuti...%n", duration.toMinutes());
        new CountdownTimer().start(duration, remaining -> printCountdown("Pausa", remaining));
        tracker.recordBreakCompleted(duration);
        System.out.println("\n✔ Pausa terminata!");
    }

    private static void printCountdown(String label, Duration remaining) {
        long minutes = remaining.toMinutes();
        long seconds = remaining.minusMinutes(minutes).getSeconds();
        System.out.printf("\r%s: %02d:%02d", label, minutes, seconds);
    }

    private static void printSummary(PomodoroTracker tracker) {
        System.out.println("Pomodori per etichetta:");
        if (tracker.getPomodoroCountsByLabel().isEmpty()) {
            System.out.println("  (nessuno ancora)");
        } else {
            tracker.getPomodoroCountsByLabel()
                    .forEach((label, count) -> System.out.printf("  - %s: %d%n", label, count));
        }
        System.out.printf("Totale pomodori: %d%n", tracker.getTotalPomodoroCount());
        System.out.printf("Tempo totale in pausa: %s%n", formatDuration(tracker.getTotalBreakTime()));
    }

    private static String formatDuration(Duration duration) {
        long minutes = duration.toMinutes();
        long seconds = duration.minusMinutes(minutes).getSeconds();
        return String.format("%d min %02d sec", minutes, seconds);
    }
}
