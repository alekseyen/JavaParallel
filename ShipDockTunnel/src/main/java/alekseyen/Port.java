package alekseyen;

import java.util.List;
import java.util.logging.Logger;

public final class Port implements Runnable {
    private final List<Thread> docks;
    private final Tunnel tunnel;
    private final Config cfg;
    private final Logger logger = LoggerSingleton.getLogger();

    public Port(Config config, Tunnel tunnel) {
        this.tunnel = tunnel;
        cfg = config;
        docks = getDocks();
    }

    // Creating Docks
    private interface CargoSupplier<T> {
        T get() throws InterruptedException;
    }

    private List<Thread> getDocks() {
        return List.of(
                getDock(tunnel::receiveBananaShip, cfg.getBananaShipsToGenerate(), Ship.CargoType.Banana.name()),
                getDock(tunnel::receiveBreadShip, cfg.getBreadShipsToGenerate(), Ship.CargoType.Bread.name()),
                getDock(tunnel::receiveClothesShip, cfg.getClothesShipsToGenerate(), Ship.CargoType.Clothes.name()));
    }

    private Thread getDock(CargoSupplier<Ship> supplier, int threshold, String label) {
        return new Thread(() -> {
            for (int shipsServed = 0; shipsServed < threshold; ++shipsServed) {
                try {
                    Ship newShip = supplier.get();
                    logger.info(String.format("[%s]: New ship [with %s], [number=%d] is being served",
                            getClass().getSimpleName(), newShip.getCargoType().name(), shipsServed));
                    newShip.run();
                } catch (InterruptedException e) {
                    logger.info(String.format("[%s]: Dock with %s is finishing loading ships",
                            getClass().getSimpleName(), label));
                    break;
                }

            }
        });
    }


    // Processing
    @Override
    public void run() {
        try {
            startDocks();
            awaitFinish();
        } catch (InterruptedException e) {
            logger.warning("Port got interrupted while waiting for Docks to finish");
        }
        logger.info(String.format("[%s]: closing", getClass().getSimpleName()));
    }

    private void startDocks() {
        for (Thread thread : docks) {
            thread.start();
        }
    }

    private void awaitFinish() throws InterruptedException {
        for (Thread thread : docks) {
            thread.join();
        }
    }
}
