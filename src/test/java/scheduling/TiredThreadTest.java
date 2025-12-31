package scheduling;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
    void testRunThrowsException() throws InterruptedException {
        TiredThread worker = new TiredThread(1, 1.0);

        AtomicReference<Throwable> exceptionCatcher = new AtomicReference<>();

        worker.setUncaughtExceptionHandler((t, e) -> exceptionCatcher.set(e));

        worker.start();
        worker.newTask(() -> {
            throw new RuntimeException("ABBA");
        });
        worker.join();
        assertNotNull(exceptionCatcher.get());
        assertEquals("ABBA", exceptionCatcher.get().getMessage());
        assertTrue(exceptionCatcher.get() instanceof RuntimeException);
    }
}
