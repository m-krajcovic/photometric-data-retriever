package cz.muni.physics.pdr.app.model;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 23/04/16
 */
public abstract class EntryModel<K , V> {

    protected K key;
    protected V value;

    protected EntryModel() {
    }

    protected EntryModel(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public abstract void key(String keyString);
    public abstract void value(String valueString);
    public abstract String key();
    public abstract String value();
}
