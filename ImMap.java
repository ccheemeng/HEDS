import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;

/**
 * From
 * @author cs2030
 * An immutable implementation of {@code LinkedHashMap}.
 */
public class ImMap<K, V> implements Iterable<Map.Entry<K, V>> {
    private final Map<K, V> map;

    public ImMap() {
        this.map = new LinkedHashMap<K, V>();
    }

    public ImMap<K, V> put(K key, V value) {
        ImMap<K, V> newMap = new ImMap<K, V>();
        newMap.map.putAll(this.map);
        newMap.map.put(key, value);
        return newMap;
    }

    public Set<K> keySet() {
        return this.map.keySet();
    }

    public Collection<V> values() {
        return this.map.values();
    }

    public Set<Map.Entry<K, V>> entrySet() {
        return this.map.entrySet();
    }

    public Iterator<Map.Entry<K, V>> iterator() {
        return this.entrySet().iterator();
    }

    public Optional<V> get(Object key) {
        return Optional.<V>ofNullable(this.map.get(key));
    }

    boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public String toString() {
        return this.map.toString();
    }
}
