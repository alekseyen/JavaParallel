package alekseyen;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.logging.FileHandler;

public class RingProcessorTest {
    private FileHandler logFile;

    @BeforeEach
    public void init(){
        try{
            logFile = new FileHandler("testRing.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void InitializationRingTest(){
        RingProcessor processor = new RingProcessor(6, 3, logFile);

        Assertions.assertThat(processor.getDataPackages()).hasSize(3);

        Assertions.assertThat(processor.getDataPackages().get(0).getEndTime()).isEqualTo(0L);
        Assertions.assertThat(processor.getNodeList().get(2).getBufferStack().size()).isEqualTo(0);
        Assertions.assertThat(processor.getNodeList().get(1).getAllData().size()).isEqualTo(0);
    }
}
