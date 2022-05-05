package alekseyen;

import lombok.Data;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


@Data
public final class Ship implements Runnable {
    private final int capacity;
    private final CargoType cargoType;

    private final Logger logger = LoggerSingleton.getLogger();

    public enum CargoType { Banana, Bread, Clothes }

    private int getSecondsToLoad() {
        return capacity / 10;
    }

    @Override
    public void run() {
        try {
            TimeUnit.SECONDS.sleep(getSecondsToLoad());
        } catch (InterruptedException e) {
            logger.warning("Ship was interrupted while running");
        }
    }
}
