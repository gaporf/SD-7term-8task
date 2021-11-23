package ru.ifmo.rain.akimov.event;

import ru.ifmo.rain.akimov.clock.Clock;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventStatisticImpl implements EventStatistic {
    private final Clock clock;
    private final Map<String, List<Instant>> statistic = new HashMap<>();

    private static final long SECONDS_IN_MINUTE = 60;
    private static final long SECONDS_IN_HOUR = 3600;

    public EventStatisticImpl(final Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(final String name) {
        final Instant now = clock.now();
        statistic.putIfAbsent(name, new ArrayList<>());
        statistic.get(name).add(now);
    }

    @Override
    public List<Integer> getEventStatisticByName(final String name) {
        final List<Integer> result = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            result.add(0);
        }
        final List<Instant> events = statistic.getOrDefault(name, new ArrayList<>());
        final long nowSeconds = clock.now().getEpochSecond();
        for (int i = events.size() - 1; i >= 0; i--) {
            final long eventSeconds = events.get(i).getEpochSecond();
            final long difSeconds = nowSeconds - eventSeconds;
            if (difSeconds >= 0 && difSeconds < SECONDS_IN_HOUR) {
                final int minute = (int) (difSeconds / SECONDS_IN_MINUTE);
                result.set(minute, result.get(minute) + 1);
            } else if (difSeconds > SECONDS_IN_HOUR) {
                break;
            }
        }
        return result;
    }

    @Override
    public Map<String, List<Integer>> getAllEventStatistic() {
        final Map<String, List<Integer>> result = new HashMap<>();
        for (String key : statistic.keySet()) {
            result.put(key, getEventStatisticByName(key));
        }
        return result;
    }

    @Override
    public void printStatistic() {
        final Map<String, List<Integer>> currentStatistic = getAllEventStatistic();
        final Instant now = clock.now();
        System.out.println("Now: " + now.toString());
        for (String name : currentStatistic.keySet()) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
            System.out.println("Statistic for event " + name);
            final List<Integer> rpm = currentStatistic.get(name);
            for (int i = 0; i < 60; i++) {
                if (rpm.get(i) != 0) {
                    System.out.println(i + " minute(s) ago was/were " + rpm.get(i) + " event(s)");
                }
            }
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        }
    }
}
