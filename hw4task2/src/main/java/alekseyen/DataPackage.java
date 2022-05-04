package alekseyen;

import lombok.Getter;
import lombok.Setter;

public class DataPackage {
    @Getter
    private final int destinationNode;

    @Getter
    private final String data;

    @Getter
    private final long startTime;

    @Getter
    @Setter
    private long endTime;

    @Getter
    private long totalBufferTime;

    @Setter
    @Getter
    private long bufferStart;

    public DataPackage(int destinationNode, String data) {
        this.destinationNode = destinationNode;
        this.data = data;
        this.totalBufferTime = 0;

        startTime = System.nanoTime();
    }

    public void addTotalBufferTime(long added) {
        this.totalBufferTime += added;
    }


}
