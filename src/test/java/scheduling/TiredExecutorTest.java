package scheduling;

import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

class TiredExecutorTest {

    @Test
    void testSubmitAll() throws InterruptedException {
        int numThreads = 2;
        int numTasks = 10;
        TiredExecutor executor = new TiredExecutor(numThreads);
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> { counter.incrementAndGet(); });
        }
        try {
            executor.submitAll(tasks);
        } finally {
            executor.shutdown();
        }
        TiredThread[] workers = getWorkers(executor);
        for (TiredThread worker : workers) {
            assertTrue(worker.getTimeUsed() >= 0);
            assertTrue(worker.getTimeIdle() >= 0);
        }
        assertEquals(numTasks, counter.get());
    }

    @Test
    void testSubmitAllHighLoad() throws InterruptedException {
        int numThreads = 4;
        int numTasks = 1000;
        TiredExecutor executor = new TiredExecutor(numThreads);
        AtomicInteger counter = new AtomicInteger(0);

        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < numTasks; i++) {
            tasks.add(() -> { counter.incrementAndGet(); });
        }
        try {
            executor.submitAll(tasks);
        } finally {
            executor.shutdown();
        }
        TiredThread[] workers = getWorkers(executor);
        for (TiredThread worker : workers) {
            assertTrue(worker.getTimeUsed() >= 0);
            assertTrue(worker.getTimeIdle() >= 0);
        }
        assertEquals(numTasks, counter.get());
    }

    private TiredThread[] getWorkers(TiredExecutor executor) {
        try {
            Field field = TiredExecutor.class.getDeclaredField("workers");
            field.setAccessible(true);
            return (TiredThread[]) field.get(executor);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to access workers field via reflection: " + e.getMessage());
            return new TiredThread[0];
        }
    }
}
