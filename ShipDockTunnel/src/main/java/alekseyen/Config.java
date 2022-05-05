package alekseyen;

import lombok.Getter;

import java.io.File;

public final class Config {
    @Getter
    private final int bananaShipsToGenerate;
    @Getter
    private final int clothesShipsToGenerate;
    @Getter
    private final int breadShipsToGenerate;

    @Getter
    private final int overallShipsToGenerate;

    @Getter
    private final File logFile;

    Config(int bananaShipsCnt, int clothesShipsCnt, int breadShipsCnt, String filename) {
        overallShipsToGenerate = bananaShipsCnt + clothesShipsCnt + breadShipsCnt;
        bananaShipsToGenerate = bananaShipsCnt;
        clothesShipsToGenerate = clothesShipsCnt;
        breadShipsToGenerate = breadShipsCnt;
        logFile = new File(filename);
    }
}
