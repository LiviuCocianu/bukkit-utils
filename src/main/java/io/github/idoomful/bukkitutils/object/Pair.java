package io.github.idoomful.bukkitutils.object;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.Map;

public final class Pair<K, V> implements Map.Entry<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
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

    public String toJSON() {
        return new Gson().toJson(this, new TypeToken<Pair<K, V>>(){}.getType());
    }

    public static <K, V> Pair<K, V> fromJSON(String json) {
        return new Gson().fromJson(json, new TypeToken<Pair<K, V>>(){}.getType());
    }
}
