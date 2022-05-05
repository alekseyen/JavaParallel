package alekseyen;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

public final class LoggerSingleton {
    private static final Logger LOGGER = Logger.getLogger("CargoShippingLogger");
    private static boolean initialised = false;

    private LoggerSingleton() {
    }

    public static Logger createInstance(File file) throws IOException {
        if (!initialised) {
            LOGGER.setUseParentHandlers(false); // in order not to write to console
            LOGGER.addHandler(new FileHandler(file.getAbsolutePath()));
            initialised = true;
        }
        return LOGGER;
    }

    public static Logger getLogger() {
        assert initialised;
        return LOGGER;
    }
}
