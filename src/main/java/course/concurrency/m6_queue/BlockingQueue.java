package course.concurrency.m6_queue;

public interface BlockingQueue<T> {

    void enqueue(T value);

    T dequeue();

    int size();

}
