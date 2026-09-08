# Focus Timer (Pomodoro)

Applicazione desktop JavaFX per la tecnica del Pomodoro, con due timer
indipendenti (concentrazione e pausa).

## Funzionalità

- Timer di concentrazione (focus) e timer di pausa (break), eseguiti in
  sequenza per ogni ciclo, con pulsanti per avviare, mettere in pausa/
  riprendere e saltare la fase corrente.
- Ogni pomodoro viene associato a un'etichetta (es. il nome di un progetto
  o di un'attività) e i pomodori completati vengono conteggiati
  separatamente per etichetta, mostrati in una lista aggiornata in tempo
  reale.
- Il tempo totale trascorso in pausa viene accumulato e mostrato, insieme
  al totale complessivo dei pomodori, dopo ogni ciclo.

## Struttura del progetto

- `com.focustimer.model` — `SessionType` (FOCUS/BREAK) e `PomodoroSession`.
- `com.focustimer.tracker.PomodoroTracker` — conteggio pomodori per
  etichetta e tempo totale di pausa.
- `com.focustimer.Main` — applicazione JavaFX (`Application`), interfaccia
  grafica e gestione dei timer tramite `javafx.animation.Timeline`.

## Requisiti

- Java 17+
- Nessuna installazione di Gradle richiesta: usare il wrapper incluso
  (`./gradlew`, o `gradlew.bat` su Windows).

## Build ed esecuzione

```bash
./gradlew run
```

## Test

```bash
./gradlew test
```
