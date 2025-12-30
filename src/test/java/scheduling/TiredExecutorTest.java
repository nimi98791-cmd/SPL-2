package scheduling;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class TiredExecutorTest {

    @Test
    void testSubmitAllIncrementsCounter() throws InterruptedException {
        int numThreads = 3;
        int numTasks = 10;
        TiredExecutor executor = new TiredExecutor(numThreads);
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                counter.incrementAndGet();
            });
        }

        try {
            executor.submitAll(tasks);
        } finally {
            executor.shutdown();
        }

        assertEquals(numTasks, counter.get());
    }

    @Test
    void testHighLoadStability() throws InterruptedException {
        int numThreads = 4;
        int numTasks = 1000;
        TiredExecutor executor = new TiredExecutor(numThreads);
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> {
                counter.incrementAndGet();
            });
        }

        try {
            executor.submitAll(tasks);
        } finally {
            executor.shutdown();
        }

        assertEquals(numTasks, counter.get());
    }
}