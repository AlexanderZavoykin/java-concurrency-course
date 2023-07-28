package course.concurrency.m5_streams;

import java.util.concurrent.*;

public class ThreadPoolTask {

    // Task #1
    public ThreadPoolExecutor getLifoExecutor() {
        final BlockingQueue<Runnable> queue = new LinkedBlockingDeque<>() {
            @Override
            public boolean offer(Runnable runnable) {
                return super.offerFirst(runnable);
            }
        };
        return new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, queue);
    }

    // Task #2
    public ThreadPoolExecutor getRejectExecutor() {
        final BlockingQueue<Runnable> queue = new SynchronousQueue<>();
        final RejectedExecutionHandler handler = new ThreadPoolExecutor.DiscardPolicy();
        return new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS, queue, handler);
    }
}
