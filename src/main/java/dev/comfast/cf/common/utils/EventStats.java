package dev.comfast.cf.common.utils;
import dev.comfast.experimental.events.EventListener;
import dev.comfast.experimental.events.model.AfterEvent;
import dev.comfast.util.TerminalGenerator;
import dev.comfast.util.time.TimeFormatter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;

import static java.lang.String.valueOf;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.toList;

/**
 * Measure all Events and able to print out statistics. Low cpu/memory overhead.
 */
@RequiredArgsConstructor
public class EventStats<T> implements EventListener<T> {
    private final String name;
    private final HashMap<String, LongSummaryStatistics> data = new HashMap<>();

    /**
     * Collect times for statistics
     */
    @Override public void after(AfterEvent<T> event) {
        countTime(event.actionName, event.time.nanos);
        countTime("TOTAL", event.time.nanos);
    }

    private void countTime(String key, long nanos) {
        data.computeIfAbsent(key, k -> new LongSummaryStatistics()).accept(nanos);
    }

    public void printStats() {
        List<List<String>> stats = data.entrySet().stream()
            .sorted(comparingLong(a -> a.getValue().getSum()))
            .map(entry -> formatStats(entry.getKey(), entry.getValue()))
            .collect(toList());

        System.out.printf("%s:%n%s%n%s%n%n", name,
            "=".repeat(65),
            new TerminalGenerator(" | ").table(
                List.of("actionName", "count", "min", "max", "avg", "total"), stats));
    }

    /**
     * Format statistics for printing
     */
    private List<String> formatStats(String name, LongSummaryStatistics stats) {
        var formatter = new TimeFormatter();
        return List.of(
            name,
            valueOf(stats.getCount()),
            formatter.formatNanoseconds(stats.getMin()),
            formatter.formatNanoseconds(stats.getMax()),
            formatter.formatNanoseconds(((Double) stats.getAverage()).longValue()),
            formatter.formatNanoseconds(stats.getSum())
        );
    }
}
