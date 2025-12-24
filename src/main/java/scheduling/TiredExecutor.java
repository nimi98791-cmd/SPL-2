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
            tiredThread.start();
        }
    }

    public void submit(Runnable task) {
        synchronized (this) {
            while (idleMinHeap.isEmpty()) {
                try {
                    this.wait(); // Wait for available thread.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            TiredThread worker = idleMinHeap.poll();
            worker.updateTimeIdle(); // Stops being idle.
            inFlight.incrementAndGet();
            Runnable wrappedTask = () -> {
                long startWork = System.nanoTime();
                try {
                    task.run();
                } finally {
                    worker.updateTimeUsed(startWork);
                    synchronized (this) {
                        idleMinHeap.add(worker);
                        inFlight.decrementAndGet();
                        this.notifyAll();
                    }
                }
            };
            worker.newTask(wrappedTask);
        }
    }

    public void submitAll(Iterable<Runnable> tasks) {
        System.out.println("Start submit all");
        for (Runnable task : tasks) {
            submit(task);
        }
        synchronized (this) {
            while (inFlight.get() > 0) {
                try {
                    this.wait(); // Wait for all threads to finish.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        System.out.println("Finished submit all");
    }

    public void shutdown() throws InterruptedException {
        synchronized (this) {
            for (TiredThread worker : workers) {
                worker.shutdown(); // Signals all workers to stop.
            }
            for (TiredThread worker : workers) {
                worker.join(); // Wait for all workers to finish.
            }
        }
    }

    public synchronized String getWorkerReport() {
        String ans = "";
        for (int i = 0; i < workers.length; i++) {
            ans += "ID: " + workers[i].getWorkerId() + "\n" +
                    "Name: " + workers[i].getName() + "\n" +
                    "Fatigue: " + workers[i].getFatigue() + "\n" +
                    "Time used: " + workers[i].getTimeUsed() + "\n" +
                    "Time idle: " + workers[i].getTimeIdle() + "\n\n";
        }
        return ans;
    }
}
