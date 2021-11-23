package ru.ifmo.rain.akimov.clock;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.Random;

public class SettableClockTest {
    private Instant now;
    private SettableClock clock;
    private final Random random = new Random(Instant.now().toEpochMilli());

    @Before
    public void init() {
        now = Instant.now();
        clock = new SettableClock(now);
    }

    @Test
    public void createAndGetTest() {
        Assert.assertEquals(now, clock.now());
    }

    @Test
    public void setAndGetTest() {
        final Instant inMinute = now.plusSeconds(60L);
        clock.setNow(inMinute);
        Assert.assertEquals(inMinute, clock.now());
    }

    @Test
    public void severalSetsTest() {
        Instant curInstant = now;
        for (int i = 0; i < 100_000; i++) {
            curInstant = curInstant.plusSeconds((random.nextInt() & Integer.MAX_VALUE) % 100);
            clock.setNow(curInstant);
        }
        Assert.assertEquals(curInstant, clock.now());
    }
}
