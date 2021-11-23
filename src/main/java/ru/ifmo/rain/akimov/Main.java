package ru.ifmo.rain.akimov;

import ru.ifmo.rain.akimov.clock.SettableClock;
import ru.ifmo.rain.akimov.event.EventStatistic;
import ru.ifmo.rain.akimov.event.EventStatisticImpl;

import java.time.Instant;

public class Main {
    public static void main(String[] args) {
        final SettableClock clock = new SettableClock(Instant.now());
        final EventStatistic eventStatistic = new EventStatisticImpl(clock);
        for (int i = 0; i < 60; i++) {
            clock.setNow(clock.now().plusSeconds(60));
            eventStatistic.incEvent("Every minute event");
            if (i % 2 == 0) {
                eventStatistic.incEvent("Every two minutes event");
                eventStatistic.incEvent("Every two minutes event");
            }
            if (i % 5 == 0) {
                eventStatistic.incEvent("Every five minutes event");
            }
            if (i % 10 == 0) {
                eventStatistic.incEvent("Every ten minutes event");
            }
            if (i % 30 == 0) {
                eventStatistic.incEvent("Every thirty minutes event");
                eventStatistic.incEvent("Every thirty minutes event");
                eventStatistic.incEvent("Every thirty minutes event");
            }
            if (i % 60 == 0) {
                eventStatistic.incEvent("Every hour event");
            }
        }
        eventStatistic.printStatistic();
    }
}
