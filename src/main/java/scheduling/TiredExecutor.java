package scheduling;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TiredExecutor {

    private final TiredThread[] workers;
    private final PriorityBlockingQueue<TiredThread> idleMinHeap = new PriorityBlockingQueue<>();
    private final AtomicInteger inFlight = new AtomicInteger(0);

    public TiredExecutor(int numThreads) {
        workers = new TiredThread[numThreads];
        for (int i = 0; i < numThreads; i++) {
            TiredThread tiredThread = new TiredThread(i, 0.5 + Math.random());
            workers[i] = tiredThread;
            idleMinHeap.add(tiredThread);
        }
    }

    public void submit(Runnable task) {
        inFlight.incrementAndGet();

        TiredThread t;
        try {
            t = idleMinHeap.take();
        } catch (InterruptedException e) {
            inFlight.decrementAndGet();
            Thread.currentThread().interrupt();
            return;
        }
        t.newTask(() -> {
            try {
                task.run();
            } finally {
                if (inFlight.decrementAndGet() == 0) {
                    synchronized (TiredExecutor.this) {
                        TiredExecutor.this.notifyAll();
                    }
                }
                idleMinHeap.add(t);
            }
        });

        if (!t.isAlive()) {
            t.start();
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        for (Runnable task : tasks) {
            submit(task);
        }
        synchronized (this) {
            while (inFlight.get() > 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        }
    }

    public void shutdown() throws InterruptedException {
        for (TiredThread t : workers) {
            t.interrupt();
        }
        for (TiredThread t : workers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public synchronized String getWorkerReport() {
        // TODO: return readable statistics for each worker
        return null;
    }
}
