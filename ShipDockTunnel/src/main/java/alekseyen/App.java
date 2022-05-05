package alekseyen;


import java.io.IOException;

public final class App {
    private App() {
    }

    public static void main(String[] args) {
        var config = new Config(5, 5, 5, "logFile");
        runCargoShipping(config);
    }

    public static void runCargoShipping(Config config) {
        try {
            var manager = new CargoManager(config);
            manager.startCargoShipping();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

    }
}
