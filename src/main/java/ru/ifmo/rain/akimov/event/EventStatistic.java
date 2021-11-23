package ru.ifmo.rain.akimov.event;

import java.util.List;
import java.util.Map;

public interface EventStatistic {
    void incEvent(String name);

    List<Integer> getEventStatisticByName(String name);

    Map<String, List<Integer>> getAllEventStatistic();

    void printStatistic();
}
