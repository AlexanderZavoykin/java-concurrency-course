package course.concurrency.m6_queue;

import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MyBlockingQueue<T> implements BlockingQueue<T> {

    private final LinkedList<T> list;
    private final Semaphore putSemaphore;
    private final Semaphore pollSemaphore;
    private final Lock lock = new ReentrantLock();

    public MyBlockingQueue(int maxSize) {
        list = new LinkedList<>();
        putSemaphore = new Semaphore(maxSize);
        pollSemaphore = new Semaphore(0);
    }

    @Override
    public void enqueue(T value) {
        try {
            putSemaphore.acquire();
            lock.lock();
            try {
                list.add(value);
            } finally {
                lock.unlock();
            }

            pollSemaphore.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T dequeue() {
        try {
            pollSemaphore.acquire();
            lock.lock();
            T value;

            try {
                value = list.getLast();
            } finally {
                lock.unlock();
            }

            putSemaphore.release();
            return value;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
