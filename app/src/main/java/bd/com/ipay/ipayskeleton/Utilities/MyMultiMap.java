package bd.com.ipay.ipayskeleton.Utilities;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MyMultiMap<K, V> {

    private final Map<K, List<V>> mInternalMap;

    public MyMultiMap() {
        mInternalMap = new HashMap<K, List<V>>();
    }

    public void clear() {
        mInternalMap.clear();
    }

    public boolean containsKey(K key) {
        return mInternalMap.containsKey(key);
    }

    public boolean containsValue(V value) {
        for (List<V> valueList : mInternalMap.values()) {
            if (valueList.contains(value)) {
                return true;
            }
        }
        return false;
    }

    public List<V> get(K key) {
        return mInternalMap.get(key);
    }

    public boolean isEmpty() {
        return mInternalMap.isEmpty();
    }

    public Set<K> keySet() {
        return mInternalMap.keySet();
    }

    public V put(K key, V value) {
        List<V> valueList = mInternalMap.get(key);
        if (valueList == null) {
            valueList = new LinkedList<V>();
            mInternalMap.put(key, valueList);
        }
        valueList.add(value);
        return value;
    }


    public List<V> remove(K key) {
        return mInternalMap.remove(key);
    }

    public int size() {
        return mInternalMap.size();
    }

    public List<V> values() {
        List<V> allValues = new LinkedList<V>();
        for (List<V> valueList : mInternalMap.values()) {
            allValues.addAll(valueList);
        }
        return allValues;
    }
}