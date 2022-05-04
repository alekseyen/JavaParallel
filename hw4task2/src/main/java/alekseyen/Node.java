package alekseyen;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Node implements Runnable {
    private final int MAX_PACKETS_PER_NODE;
    private static final int NANO_SECONDS_TO_MS = 1_000_000;

    @Getter
    private final int nodeId;

    @Setter
    @Getter
    private Node next;

    @Getter
    private final ConcurrentLinkedQueue<DataPackage> bufferStack;

    @Getter
    private final List<DataPackage> allData;
    private final AtomicInteger numPackages;
    private final DataCounter dataCounter;
    private final Logger logger;

    @Setter
    private Node coordinator;

    Node(int nodeId, DataCounter dataCounter, Logger logger) {
        this.nodeId = nodeId;
        this.dataCounter = dataCounter;
        this.logger = logger;
        allData = new ArrayList<>();

        bufferStack = new ConcurrentLinkedQueue<>();
        numPackages = new AtomicInteger(0);

        MAX_PACKETS_PER_NODE = 3; // Exactly like in task conditions
    }

    public final void writeDate(DataPackage data) {
        logger.info(data.getData() + " written to node " + nodeId);
        data.setEndTime(System.nanoTime());

        allData.add(data);
    }

    /**
     * Get data from Node::bufferStack and send to process. After that get results to the next Node.
     */
    @Override
    public void run() {
        while (!dataCounter.isAllDataReceived()) {

            int packages = numPackages.get();
            if (!bufferStack.isEmpty() && packages < MAX_PACKETS_PER_NODE) {
                if (numPackages.compareAndSet(packages, packages + 1)) {
                    processData();
                    numPackages.decrementAndGet();
                }
            }
        }
    }

    /**
     * For current node :
     * 1. get data from buffer
     * 2. increment total buffer time
     * 3. send data to process
     * 3. send result to next Node
     */
    private void processData() {
        DataPackage data = bufferStack.poll();

        data.addTotalBufferTime(System.nanoTime() - data.getTotalBufferTime());
        try {
            Thread.sleep(1);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        if (data.getDestinationNode() == this.nodeId) {
            long received = System.nanoTime();
            logger.log(Level.INFO, "Data received on node " + nodeId
                    + " for time: " + (received - data.getStartTime()) / NANO_SECONDS_TO_MS + " ms");
            coordinator.writeDate(data);
            dataCounter.incrementCounter();
        } else {
            next.addData(data);
            logger.log(Level.INFO, data.getData() + " sent from" + nodeId + " to " + next.getNodeId());
        }
    }

    public final void addData(DataPackage data) {
        bufferStack.add(data);

        data.setBufferStart(System.nanoTime());
    }

}
