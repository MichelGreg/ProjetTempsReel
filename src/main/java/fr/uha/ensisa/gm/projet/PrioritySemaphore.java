package fr.uha.ensisa.gm.projet;

import java.util.concurrent.PriorityBlockingQueue;

public class PrioritySemaphore {

    private PriorityBlockingQueue<Thread> queue;
    private int permits;

    public PrioritySemaphore(int permits) {
        this.queue = new PriorityBlockingQueue<>(10, (t1, t2) -> t2.getPriority() - t1.getPriority());
        this.permits = permits;
    }

    public synchronized void acquire() throws InterruptedException {
        if (permits > 0) {
            --permits;
        } else {
            Thread currentThread = Thread.currentThread();
            queue.put(currentThread);
            wait();
        }
    }

    public synchronized void release() {
        if (!queue.isEmpty()) {
            Thread nextThread = queue.poll();
            nextThread.notify();
        } else {
            ++permits;
        }
    }

    public boolean tryAcquire() {
        if (permits > 0) {
            --permits;
            return true;
        } else {
            return false;
        }
    }
}

