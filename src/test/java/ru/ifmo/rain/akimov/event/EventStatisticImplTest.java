package ru.ifmo.rain.akimov.event;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.ifmo.rain.akimov.clock.SettableClock;

import java.time.Instant;
import java.util.*;

public class EventStatisticImplTest {
    private EventStatistic eventStatistic;
    private SettableClock clock;
    private List<Integer> expected;
    private final int EVENTS_NUM = 10_000;

    @Before
    public void init() {
        clock = new SettableClock(Instant.now());
        eventStatistic = new EventStatisticImpl(clock);
        expected = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            expected.add(0);
        }
    }

    @Test
    public void noEventsTest() {
        Assert.assertEquals(Map.of(), eventStatistic.getAllEventStatistic());
        Assert.assertEquals(expected, eventStatistic.getEventStatisticByName("event"));
    }

    @Test
    public void oneEventTest() {
        eventStatistic.incEvent("event");
        expected.set(0, 1);
        Assert.assertEquals(expected, eventStatistic.getEventStatisticByName("event"));
    }

    @Test
    public void oneEventInTheLastSecondTest() {
        for (int i = 0; i < EVENTS_NUM; i++) {
            eventStatistic.incEvent("event");
        }
        expected.set(0, EVENTS_NUM);
        Assert.assertEquals(expected, eventStatistic.getEventStatisticByName("event"));
    }

    @Test
    public void oneEventInTheLastMinuteTest() {
        final Instant init = clock.now();
        final double DELTA_SECONDS = 60.0 / EVENTS_NUM;
        for (int i = 0; i < EVENTS_NUM; i++) {
            clock.setNow(init.plusSeconds((long) (DELTA_SECONDS * i)));
            eventStatistic.incEvent("event");
        }
        expected.set(0, EVENTS_NUM);
        Assert.assertEquals(expected, eventStatistic.getEventStatisticByName("event"));
    }

    @Test
    public void oneEventInTheLastHourTest() {
        final Instant init = clock.now();
        final double DELTA_SECONDS = 3_600.0 / EVENTS_NUM;
        for (int i = 0; i < EVENTS_NUM; i++) {
            clock.setNow(init.plusSeconds((long) (DELTA_SECONDS * i)));
            int currentMinute = (int) (DELTA_SECONDS * i) / 60;
            expected.set(59 - currentMinute, expected.get(59 - currentMinute) + 1);
            eventStatistic.incEvent("event");
        }
        Assert.assertEquals(expected, eventStatistic.getEventStatisticByName("event"));
    }

    @Test
    public void severalEventsInTheLastSecondTest() {
        eventStatistic.incEvent("event1");
        eventStatistic.incEvent("event2");
        eventStatistic.incEvent("event3");
        expected.set(0, 1);
        final Map<String, List<Integer>> expectedMap = new HashMap<>();
        expectedMap.put("event1", List.copyOf(expected));
        expectedMap.put("event2", List.copyOf(expected));
        expectedMap.put("event3", List.copyOf(expected));
        Assert.assertEquals(expectedMap, eventStatistic.getAllEventStatistic());
    }

    @Test
    public void severalEventsInTheLastMinuteTest() {
        final Instant init = clock.now();
        final double DELTA_SECONDS = 60.0 / EVENTS_NUM;
        for (int i = 0; i < EVENTS_NUM; i++) {
            clock.setNow(init.plusSeconds((long) (DELTA_SECONDS * i)));
            eventStatistic.incEvent("event1");
            eventStatistic.incEvent("event2");
            eventStatistic.incEvent("event3");
        }
        expected.set(0, EVENTS_NUM);
        final Map<String, List<Integer>> expectedMap = new HashMap<>();
        expectedMap.put("event1", List.copyOf(expected));
        expectedMap.put("event2", List.copyOf(expected));
        expectedMap.put("event3", List.copyOf(expected));
        Assert.assertEquals(expectedMap, eventStatistic.getAllEventStatistic());
    }

    @Test
    public void severalEventsInTheLastHourTest() {
        final Instant init = clock.now();
        final double DELTA_SECONDS = 3_600.0 / EVENTS_NUM;
        for (int i = 0; i < EVENTS_NUM; i++) {
            clock.setNow(init.plusSeconds((long) (DELTA_SECONDS * i)));
            int currentMinute = (int) (DELTA_SECONDS * i) / 60;
            expected.set(59 - currentMinute, expected.get(59 - currentMinute) + 1);
            eventStatistic.incEvent("event1");
            eventStatistic.incEvent("event2");
            eventStatistic.incEvent("event3");
        }
        final Map<String, List<Integer>> expectedMap = new HashMap<>();
        expectedMap.put("event1", List.copyOf(expected));
        expectedMap.put("event2", List.copyOf(expected));
        expectedMap.put("event3", List.copyOf(expected));
        Assert.assertEquals(expectedMap, eventStatistic.getAllEventStatistic());
    }

    @Test
    public void severalEventsInTheLastDayTest() {
        final Instant init = clock.now();
        final double DELTA_SECONDS = 86_400.0 / (EVENTS_NUM - 1);
        for (int i = 0; i < EVENTS_NUM; i++) {
            clock.setNow(init.plusSeconds((long) (DELTA_SECONDS * i)));
            int currentMinute = ((86_400 - (int) (DELTA_SECONDS * i)) / 60);
            if (currentMinute <= 59) {
                expected.set(currentMinute, expected.get(currentMinute) + 1);
            }
            eventStatistic.incEvent("event1");
            eventStatistic.incEvent("event2");
            eventStatistic.incEvent("event3");
        }
        final Map<String, List<Integer>> expectedMap = new HashMap<>();
        expectedMap.put("event1", List.copyOf(expected));
        expectedMap.put("event2", List.copyOf(expected));
        expectedMap.put("event3", List.copyOf(expected));
        Assert.assertEquals(expectedMap, eventStatistic.getAllEventStatistic());
    }

    @Test
    public void manyEventsStressTest() {
        final Instant init = clock.now();
        final Random random = new Random(init.toEpochMilli());
        final double DELTA_SECONDS = 86_400.0 * 7 * 31 / (EVENTS_NUM - 1);
        final Map<String, List<Integer>> expectedMap = new HashMap<>();
        for (int i = 0; i < 100; i++) {
            expectedMap.put("event" + i, new ArrayList<>(expected));
        }
        for (int i = 0; i < EVENTS_NUM; i++) {
            final String eventName = "event" + (random.nextInt() & Integer.MAX_VALUE) % 100;
            clock.setNow(init.plusSeconds((long) (DELTA_SECONDS * i)));
            eventStatistic.incEvent(eventName);
            int currentMinute = ((86_400 * 7 * 31 - (int) (DELTA_SECONDS * i)) / 60);
            if (currentMinute <= 59) {
                final List<Integer> curEventStatistic = expectedMap.get(eventName);
                curEventStatistic.set(currentMinute, curEventStatistic.get(currentMinute) + 1);
            }
        }
        Assert.assertEquals(expectedMap, eventStatistic.getAllEventStatistic());
    }
}
