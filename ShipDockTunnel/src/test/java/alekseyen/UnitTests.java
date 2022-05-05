package alekseyen;

import alekseyen.util.RandomChoiceAdvisor;
import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;

import java.util.HashMap;
import java.util.Map;

public class UnitTests {


    @Test
    public void randomChoiceAdvisorTest() {
        int one = 5, two = 6, three = 7;
        var ground_truth = Map.of("One", one, "Two", two, "Three", three);
        var advisor = new RandomChoiceAdvisor<>(ground_truth);

        var result = new HashMap<String, Integer>();
        for (int i = 0; i < one + two + three; ++i) {
            String key = advisor.chooseOne();
            Integer value = result.getOrDefault(key, 0);
            result.put(key, value + 1);
        }
        Assertions.assertThat(result).containsExactlyInAnyOrderEntriesOf(ground_truth);
    }
}
