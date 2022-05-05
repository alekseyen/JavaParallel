package alekseyen;

import lombok.Getter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RingProcessor {
    private static final int NANO_SECONDS_TO_MS = 1_000_000;
    private final int nodesAmount;
    private final int dataAmount;

    @Getter
    private final List<Node> nodeList;

    private final Logger logger;
    @Getter
    private final List<DataPackage> dataPackages;

    RingProcessor(int nodesAmount, int dataAmount, FileHandler logs) {
        this.nodesAmount = nodesAmount;
        this.dataAmount = dataAmount;
        this.nodeList = new ArrayList<>();
        this.dataPackages = new ArrayList<>();
        logger = Logger.getLogger("ringLogger");

        logger.addHandler(logs);

        init();
    }

    /**
     * Initialize ring procedure. Create nodes, Setting up destinations and coordinator
     */
    private void init() {
        Random random = new Random();

        DataCounter dataCounter = new DataCounter(dataAmount);
        for (int i = 0; i < nodesAmount; i++) {
            Node node = new Node(i, dataCounter, logger);
            nodeList.add(node);
            if (i > 0) {
                nodeList.get(i - 1).setNext(node);
            }
        }
        nodeList.get(nodesAmount - 1).setNext(nodeList.get(0));

        int coordinatorId = random.nextInt(nodesAmount);
        nodeList.get((nodesAmount + coordinatorId - 1) % nodesAmount)
                .setNext(nodeList.get((coordinatorId + 1) % nodesAmount));

        for (int i = 0; i < nodesAmount; i++) {
            nodeList.get(i).setCoordinator(nodeList.get(coordinatorId));
        }

        for (int i = 0; i < dataAmount; i++) {
            String data = "Data_" + i;

            int dest = random.nextInt(nodesAmount);

            while (dest == coordinatorId) {
                dest = random.nextInt(nodesAmount);
            }

            DataPackage dataPackage = new DataPackage(dest, data);

            int distFromCoordinator = (coordinatorId + i) % (nodesAmount - 1) + 1;
            nodeList.get((coordinatorId + distFromCoordinator) % nodesAmount).addData(dataPackage);

            dataPackages.add(dataPackage);
        }

        logger.log(Level.INFO, "Number of nodes: " + nodesAmount);
        logger.info("Coordinator node is number " + coordinatorId);

        for (int i = 0; i < nodesAmount; i++) {
            logger.log(Level.INFO, "Node " + i + " contains "
                    + nodeList.get(i).getBufferStack().size() + " data packages");
        }
    }

    public void startProcessing() {
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < nodesAmount; i++) {
            service.execute(nodeList.get(i));
        }
        service.shutdown();
    }


    public double countAverageNetworkDelay() {
        DecimalFormat df = new DecimalFormat("#.###");

        Optional<Long> delay = dataPackages.stream()
                .map(it -> it.getEndTime() - it.getStartTime())
                .reduce(Long::sum);

        double res = (double) delay.get() / (double) (dataAmount + NANO_SECONDS_TO_MS);
        logger.info("Average network delay is " + df.format(res) + " ms");

        return res;
    }

    public double countAverageBufferDelay() {
        DecimalFormat df = new DecimalFormat("#.###");

        Optional<Long> delay = dataPackages.stream()
                .map(DataPackage::getTotalBufferTime)
                .reduce(Long::sum);


        double res = (double) delay.get() / (double) (dataAmount * NANO_SECONDS_TO_MS);
        logger.info("Average buffer delay is " + df.format(res) + " ms");

        return res;
    }
}
