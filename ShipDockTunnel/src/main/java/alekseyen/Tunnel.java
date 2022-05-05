package alekseyen;


import java.util.Objects;
import java.util.concurrent.*;
import java.util.logging.Logger;

public final class Tunnel {
    private static final int TUNNEL_CAPACITY = 5;
    private final ArrayBlockingQueue<Ship> tunnel = new ArrayBlockingQueue<>(TUNNEL_CAPACITY);

    private final LinkedBlockingQueue<Ship> awaitingBananaShips = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Ship> awaitingBreadShips = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Ship> awaitingClothesShips = new LinkedBlockingQueue<>();

    private final ScheduledExecutorService threadPool = new ScheduledThreadPoolExecutor(3);

    private final Logger logger = LoggerSingleton.getLogger();



    private void putToAwaiting(Ship ship) {
        switch (ship.getCargoType()) {
            case Banana:
                awaitingBananaShips.offer(ship);
                break;
            case Bread:
                awaitingBreadShips.offer(ship);
                break;
            case Clothes:
                awaitingClothesShips.offer(ship);
                break;
            default:
                logger.warning("Unknown ship cargo type");
                break;
        }
    }


    public void send(Ship ship) throws InterruptedException {
        tunnel.put(ship);
        threadPool.schedule(() -> {
            Ship shipCopy = tunnel.poll();
            Objects.requireNonNull(shipCopy); // there must be at least one ship because we have put one
            logger.info(String.format("[%s]: Another ship with %s has passed, sending it to await queue",
                    getClass().getSimpleName(), shipCopy.getCargoType().name()));
            putToAwaiting(shipCopy);
        }, 1, TimeUnit.SECONDS);
    }

    // receive methods
    public Ship receiveBananaShip() throws InterruptedException {
        return awaitingBananaShips.take();
    }

    public Ship receiveBreadShip() throws InterruptedException {
        return awaitingBreadShips.take();
    }

    public Ship receiveClothesShip() throws InterruptedException {
        return awaitingClothesShips.take();
    }

    public void close() {
        logger.info(String.format("[%s]: Closing", getClass().getSimpleName()));
        threadPool.shutdown();
    }
}
