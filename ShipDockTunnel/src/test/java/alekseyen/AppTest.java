package alekseyen;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class AppTest {
    private final int bananaShipsCnt = 5;
    private final int clothesShipsCnt = 5;
    private final int breadShipsCnt = 5;
    private final Config config = new Config(bananaShipsCnt, clothesShipsCnt, breadShipsCnt, "logFileTest");

    @Test
    void cargoShippingTest() throws IOException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        assertDoesNotThrow(() -> App.runCargoShipping(config));

        String content = Files.readString(Path.of(config.getLogFile().getAbsolutePath()));

        for (var type : Ship.CargoType.values()) {
            Pattern p = Pattern.compile(String.format("\\[Port]: New ship \\[with %s], \\[number=\\d+] is being served",
                    type.name()));
            Matcher m = p.matcher(content);


            int cnt = (int) config.getClass()
                    .getDeclaredMethod(String.format("get%sShipsToGenerate", type.name()))
                    .invoke(config);
            Assertions.assertThat(m.results().count()).isEqualTo(cnt);
        }

        Assertions.assertThat(config.getLogFile().delete()).isTrue();

    }
}
