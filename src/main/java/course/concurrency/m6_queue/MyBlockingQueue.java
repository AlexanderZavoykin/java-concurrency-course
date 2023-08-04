package course.concurrency.m6_queue;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class MyBlockingQueue<T> implements BlockingQueue<T> {

    private final LinkedList<T> list;
    private final Semaphore putSemaphore;
    private final Semaphore pollSemaphore;
    private final AtomicInteger size = new AtomicInteger(0);

    public MyBlockingQueue(int maxSize) {
        list = new LinkedList<>();
        putSemaphore = new Semaphore(maxSize);
        pollSemaphore = new Semaphore(0);
    }

    @Override
    public void enqueue(T value) {
        try {
            putSemaphore.acquire();
            list.add(value);
            size.incrementAndGet();
            pollSemaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T dequeue() {
        try {
            pollSemaphore.acquire();
            T value = list.removeLast();
            size.decrementAndGet();
            putSemaphore.release();
            return value;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int size() {
        return size.get();
    }

}
