# Focus Timer (Pomodoro)

Timer da riga di comando per la tecnica del Pomodoro, con due timer
indipendenti (concentrazione e pausa).

## Funzionalità

- Timer di concentrazione (focus) e timer di pausa (break), eseguiti in
  sequenza per ogni ciclo.
- Ogni pomodoro viene associato a un'etichetta (es. il nome di un progetto
  o di un'attività) e i pomodori completati vengono conteggiati
  separatamente per etichetta.
- Il tempo totale trascorso in pausa viene accumulato e mostrato, insieme
  al totale complessivo dei pomodori, dopo ogni ciclo e nel riepilogo
  finale.

## Struttura del progetto

- `com.focustimer.model` — `SessionType` (FOCUS/BREAK) e `PomodoroSession`.
- `com.focustimer.timer.CountdownTimer` — countdown generico a un secondo
  di risoluzione.
- `com.focustimer.tracker.PomodoroTracker` — conteggio pomodori per
  etichetta e tempo totale di pausa.
- `com.focustimer.Main` — applicazione a riga di comando.

## Requisiti

- Java 17+
- Maven

## Build ed esecuzione

```bash
mvn compile
mvn exec:java -Dexec.mainClass=com.focustimer.Main
```

oppure, dopo `mvn package`:

```bash
java -jar target/focustimer.jar
```

## Test

```bash
mvn test
```
