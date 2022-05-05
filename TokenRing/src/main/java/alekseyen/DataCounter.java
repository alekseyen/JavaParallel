package alekseyen;

import java.util.concurrent.atomic.AtomicInteger;

public class DataCounter {
    private final AtomicInteger counter;
    private final int amountData;

    public DataCounter(int amountData) {
        counter = new AtomicInteger(0);
        this.amountData = amountData;
    }

    public void incrementCounter() {
        this.counter.getAndIncrement();
    }

    public boolean isAllDataReceived() {
        return counter.get() == amountData;
    }
}
