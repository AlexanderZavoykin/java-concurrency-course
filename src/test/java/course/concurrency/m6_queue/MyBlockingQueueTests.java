package course.concurrency.m6_queue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBlockingQueueTests {

    @Test
    @DisplayName("Add element to empty queue, then poll it")
    public void enqueueThenDequeue() {
        int maxSize = 5;
        BlockingQueue<String> queue = new MyBlockingQueue<>(maxSize);

        Assertions.assertEquals(0, queue.size());

        String element = "some";
        queue.enqueue(element);

        Assertions.assertEquals(1, queue.size());
        Assertions.assertEquals(element, queue.dequeue());
        Assertions.assertEquals(0, queue.size());
    }

    @Test
    @DisplayName("Add elements, then check that polled element is the last added one")
    public void checkFIFO() {
        int maxSize = 5;
        BlockingQueue<String> queue = new MyBlockingQueue<>(maxSize);
        List<String> elements = new ArrayList<>(List.of("one", "two", "three", "four"));

        elements.forEach(queue::enqueue);

        Collections.reverse(elements);
        Collections.reverse(elements);
        elements.forEach(e -> Assertions.assertEquals(e, queue.dequeue()));
    }

    @Test
    @DisplayName("Add element to full queue")
    public void enqueueToFullQueue() {
        int maxSize = 5;
        BlockingQueue<String> queue = new MyBlockingQueue<>(maxSize);

        for (int i = 0; i < maxSize; i++) {
            queue.enqueue(String.valueOf(i));
        }

        final String element = "some";

        Assertions.assertEquals(5, queue.size());

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> queue.enqueue(element));

        Assertions.assertEquals(5, queue.size());
        Assertions.assertEquals("4", queue.dequeue());
        Assertions.assertEquals(element, queue.dequeue());
    }

    @Test
    @DisplayName("Poll element from empty queue")
    public void dequeueFromEmptyQueue() {
        int maxSize = 5;
        BlockingQueue<String> queue = new MyBlockingQueue<>(maxSize);

        final String element = "some";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(
            () -> Assertions.assertEquals(element, queue.dequeue()) // assertion will be accomplished after queue is unblocked
        );

        Assertions.assertEquals(0, queue.size());

        queue.enqueue(element);
        Assertions.assertEquals(1, queue.size());
    }

}
