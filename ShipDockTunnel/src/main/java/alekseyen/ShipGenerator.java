package alekseyen;

import alekseyen.util.RandomChoiceAdvisor;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class ShipGenerator implements Runnable {
    private static final List<Integer> SHIP_CAPACITY = List.of(10, 50, 100);
    private static final long DELAY = 500;

    private final RandomChoiceAdvisor<Ship.CargoType> advisor;
    private final Random rnd = new Random();

    private final Config cfg;
    private final Logger logger = LoggerSingleton.getLogger();

    private final Tunnel tunnel;

    ShipGenerator(Config config, Tunnel tunnel) {
        cfg = config;
        this.tunnel = tunnel;

        var cargoTypesCount = List.of(cfg.getBananaShipsToGenerate(), cfg.getBreadShipsToGenerate(),
                cfg.getClothesShipsToGenerate());
        var cargoTypes = List.of(Ship.CargoType.values());

        advisor = new RandomChoiceAdvisor<>(
                IntStream.range(0, cargoTypes.size())
                        .boxed()
                        .collect(Collectors.toMap(cargoTypes::get, cargoTypesCount::get)));
    }

    private Ship.CargoType getRandomType() {
        return advisor.chooseOne();
    }

    private int getRandomCapacity() {
        return SHIP_CAPACITY.get(rnd.nextInt(SHIP_CAPACITY.size()));
    }

    private Ship getRandomShip() {
        return new Ship(getRandomCapacity(), getRandomType());
    }

    @Override
    public void run() {
        for (int i = 0; i < cfg.getOverallShipsToGenerate(); ++i) {
            Ship newShip = getRandomShip();
            logger.info(String.format("[%s]: New ship with %s generated, sending it to tunnel",
                    getClass().getSimpleName(), newShip.getCargoType().name()));
            try {
                tunnel.send(newShip);
                TimeUnit.MILLISECONDS.sleep(DELAY);
            } catch (InterruptedException e) {
                logger.warning(String.format("[%s]: Interrupted while recharging", getClass().getSimpleName()));
            }
        }
        logger.info(String.format("[%s]: Finishing generating ships", getClass().getSimpleName()));
    }
}
