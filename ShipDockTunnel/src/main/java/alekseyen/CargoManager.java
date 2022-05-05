package alekseyen;

import java.io.IOException;
import java.util.logging.Logger;

public final class CargoManager {
    private final Tunnel tunnel;

    private final Thread shipGeneratorThread;
    private final Thread portThread;

    private final Logger logger;

    CargoManager(Config config) throws IOException {
        logger = LoggerSingleton.createInstance(config.getLogFile());
        tunnel = new Tunnel();
        shipGeneratorThread = new Thread(new ShipGenerator(config, tunnel));
        portThread = new Thread(new Port(config, tunnel));
    }

    public void startCargoShipping() throws InterruptedException {
        logger.info(String.format("[%s]: Starting cargoShipping", getClass().getSimpleName()));
        shipGeneratorThread.start();
        portThread.start();

        shipGeneratorThread.join();
        portThread.join();
        tunnel.close();
        logger.info(String.format("[%s]: finishing cargoShipping", getClass().getSimpleName()));
    }
}
