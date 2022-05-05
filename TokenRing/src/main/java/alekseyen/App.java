package alekseyen;

import java.io.IOException;
import java.util.logging.FileHandler;

public final class App {
    public static void main(String[] args) {
        RingProcessor processor = null;
        try {
            processor = new RingProcessor(10, 3, new FileHandler("logsRing.txt"));
            processor.startProcessing();
        } catch (IOException e) {
            e.printStackTrace();
        }


        try{
            Thread.sleep(10000);
        } catch (InterruptedException exception) {
            exception.printStackTrace();
        }

        processor.countAverageNetworkDelay();
        processor.countAverageBufferDelay();

    }
}
