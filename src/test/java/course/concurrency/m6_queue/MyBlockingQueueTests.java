package course.concurrency.m6_queue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyBlockingQueueTests {

    @Test
    @DisplayName("Add element to empty queue, then poll it")
    public void enqueueThenDequeue() {
        int maxSize = 5;
        BlockingQueue<String> queue = new MyBlockingQueue<>(maxSize);

        String element = "some";
        queue.enqueue(element);

        Assertions.assertEquals(element, queue.dequeue());
    }

    @Test
    @DisplayName("Add element to full queue")
    public void enqueueToFullQueue() throws InterruptedException {
        int maxSize = 5;
        BlockingQueue<String> queue = new MyBlockingQueue<>(maxSize);

        for (int i = 0; i < maxSize; i++) {
            queue.enqueue(String.valueOf(i));
        }

        final String element = "some";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(() -> queue.enqueue(element));

        Thread.sleep(500); // wait a little before poll element
        Assertions.assertEquals("4", queue.dequeue());

        Thread.sleep(500); // wait a little to be sure that executor`s task put new element to queue
        Assertions.assertEquals(element, queue.dequeue());
    }

    @Test
    @DisplayName("Poll element from empty queue")
    public void dequeueFromEmptyQueue() throws InterruptedException {
        int maxSize = 5;
        BlockingQueue<String> queue = new MyBlockingQueue<>(maxSize);

        final String element = "some";

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(
            () -> Assertions.assertEquals(element, queue.dequeue()) // assertion will be accomplished after queue is unblocked
        );

        Thread.sleep(2000); // wait a little before put something to queue
        queue.enqueue(element);
    }

    @Test
    @DisplayName("Put & poll many elements")
    public void enqueueAndDequeue() throws InterruptedException {
        int maxSize = 10;
        BlockingQueue<String> queue = new MyBlockingQueue<>(maxSize);

        int operationNum = 10_000_000;

        ExecutorService executor = Executors.newFixedThreadPool(24);

        CountDownLatch latch = new CountDownLatch(operationNum * 2 + 1);

        for (int i = 0; i< operationNum * 2 ; i++) {
            executor.submit(
                () -> {
                    latch.countDown();
                    queue.enqueue("some-element");
                }
            );
            executor.submit(
                () -> {
                    latch.countDown();
                    queue.dequeue();
                }
            );
        }

        executor.submit(() -> {
            latch.countDown();
            queue.enqueue("additional one element");
        });

        latch.await();

        Assertions.assertNotNull(queue.dequeue());

    }


}
