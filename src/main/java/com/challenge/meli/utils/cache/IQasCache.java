package com.challenge.meli.utils.cache;

import fj.data.List;
import fj.data.Option;

import java.util.Optional;

public interface IQasCache<K, V> {

    void clean();

    void clear();

    boolean containsKey(K key);

    boolean containsAll(List<K> keys);

    Option<V> get(K key);

    void set(K key, V value);

    void delete(K key);}
