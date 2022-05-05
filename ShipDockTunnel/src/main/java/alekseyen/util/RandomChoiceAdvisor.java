package alekseyen.util;

import java.util.ArrayList;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Assume we were given a list of Pairs (element_i, cnt_of_element_i)
 * This class helps to choose random element from given list taking into account its count.
 */
public final class RandomChoiceAdvisor<T> {
    private final Random rnd = new Random();
    private final ArrayList<Pair<T, Integer>> elements;

    public RandomChoiceAdvisor(Map<T, Integer> sample) {
        elements = sample.entrySet().stream()
                .map(e -> new Pair<>(e.getKey(), e.getValue()))
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public T chooseOne() {
        int index = rnd.nextInt(elements.size());
        Pair<T, Integer> result = elements.get(index);
        if (result.getValue() == 1) {
            elements.remove(index);
        } else {
            result.setValue(result.getValue() - 1);
        }
        return result.getKey();
    }
}
