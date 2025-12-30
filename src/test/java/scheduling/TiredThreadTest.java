package scheduling;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class TiredThreadTest {

    @Test
    void testTaskExecutionWithShutdownAndJoin() throws InterruptedException {
        TiredThread worker = new TiredThread(1, 1.0);
        worker.start();

        AtomicInteger counter = new AtomicInteger(0);
        worker.newTask(() -> counter.incrementAndGet());

        worker.shutdown();
        worker.join();

        assertEquals(1, counter.get());
        assertFalse(worker.isBusy());
    }
}
