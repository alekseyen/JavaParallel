package alekseyen;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.util.logging.FileHandler;

public class AppTest {
    FileHandler logFile = null;

    @BeforeEach
    public void init(){
        try{
            logFile = new FileHandler("testRing.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void IntegrationAppTest(){
        RingProcessor processor = new RingProcessor(6, 3, logFile);
        processor.startProcessing();

        try{
            Thread.sleep(5000);
        } catch (InterruptedException ex){
            ex.printStackTrace();
        }

        double networkDelay = processor.countAverageNetworkDelay();
        double bufferDelay = processor.countAverageBufferDelay();

        Assertions.assertThat(processor.countAverageNetworkDelay()).isLessThan(processor.countAverageBufferDelay());
    }

}
