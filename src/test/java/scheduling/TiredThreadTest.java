package scheduling;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

class TiredThreadTest {

    @Test
    void testTaskExecution() throws InterruptedException {
        TiredThread worker = new TiredThread(1, 1.0);
        worker.start();

        AtomicInteger counter = new AtomicInteger(0);
        worker.newTask(() -> counter.incrementAndGet());

        worker.shutdown();
        worker.join();

        assertEquals(1, counter.get());
        assertFalse(worker.isBusy());
    }

    @Test
    void testCompareTo() throws Exception {
        TiredThread thread1 = new TiredThread(1, 1.0);
        TiredThread thread2 = new TiredThread(2, 2.0);

        setTimeUsed(thread1, 100); // Fatigue = 100 * 1.0 = 100
        setTimeUsed(thread2, 100); // Fatigue = 100 * 2.0 = 200
        assertTrue(thread1.compareTo(thread2) < 0);
        assertTrue(thread2.compareTo(thread1) > 0);

        setTimeUsed(thread1, 200);
        assertEquals(0, thread1.compareTo(thread2));

        setTimeUsed(thread1, 500);
        assertTrue(thread1.compareTo(thread2) > 0);
    }

    /**
     * Helper method to inject precise timeUsed values for deterministic testing.
     */
    private void setTimeUsed(TiredThread thread, long value) throws Exception {
        Field field = TiredThread.class.getDeclaredField("timeUsed");
        field.setAccessible(true);
        AtomicLong timeUsed = (AtomicLong) field.get(thread);
        timeUsed.set(value);
    }
}
