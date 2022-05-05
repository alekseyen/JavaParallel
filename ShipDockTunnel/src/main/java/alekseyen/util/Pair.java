package alekseyen.util;

import lombok.Data;

@Data
public final class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
