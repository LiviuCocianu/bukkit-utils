package io.github.idoomful.bukkitutils.object;

import java.util.Map;

final class ConfigPair<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    public ConfigPair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    @Override
    public V getValue() {
        return value;
    }

    @Override
    public V setValue(V value) {
        this.value = value;
        return value;
    }
}
