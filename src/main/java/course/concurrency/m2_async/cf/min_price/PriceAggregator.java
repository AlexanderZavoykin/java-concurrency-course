package course.concurrency.m2_async.cf.min_price;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class PriceAggregator {

    private static final Long TIMEOUT_MILLIS = 2900L;

    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10l, 45l, 66l, 345l, 234l, 333l, 67l, 123l, 768l);

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        final List<CompletableFuture<Double>> futures = shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId), executor)
                        .exceptionally(ex -> null) // при выбросе исключения берем значение null
                ).collect(Collectors.toList());

        final CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[shopIds.size()]));

        allFutures
                .completeOnTimeout(null, TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .join();

        return futures.stream()
                .filter(CompletableFuture::isDone)
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .min(Double::compareTo)
                .orElse(Double.NaN);
    }

}
