package cz.muni.physics.pdr.app.utils;

import java.util.HashMap;

/**
 * Created by Michal on 28-Apr-16.
 */
public class DefaultHashMap<K, V> extends HashMap<K, V> {
    protected V defaultValue;
    public DefaultHashMap(V defaultValue) {
        this.defaultValue = defaultValue;
    }
    @Override
    public V get(Object k) {
        return k!=null && containsKey(k) ? super.get(k) : defaultValue;
    }

    public V getDefaultValue() {
        return defaultValue;
    }
}
