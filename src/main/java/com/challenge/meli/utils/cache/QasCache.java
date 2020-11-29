package com.challenge.meli.utils.cache;

import fj.data.HashMap;
import fj.data.List;
import fj.data.Option;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class QasCache<K, V> implements IQasCache<K, V> {

    public static final Long DEFAULT_CACHE_TIMEOUT = 80000L;

    protected HashMap<K, CacheValue<V>> cacheMap;
    protected Long cacheTimeout;

    public QasCache() {
        this(DEFAULT_CACHE_TIMEOUT);
    }

    public QasCache(Long cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
        this.cacheMap = HashMap.hashMap();
    }

    @Override
    public void clean() {
        for(K key: this.getExpiredKeys()) {
            this.delete(key);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return this.cacheMap.contains(key);
    }

    protected List<K> getExpiredKeys() {
        return this.cacheMap.keys()
                            .filter(this::isExpired);
    }

    protected boolean isExpired(K key) {
        LocalDateTime expirationDateTime = this.cacheMap.get(key)
                                                        .some().getCreatedAt().plus(this.cacheTimeout, ChronoUnit.MILLIS);
        return LocalDateTime.now().isAfter(expirationDateTime);
    }

    @Override
    public void clear() {
        this.cacheMap.clear();
    }

    @Override
    public boolean containsAll(List<K> keys) {
        return keys.foldLeft((exist, key) -> exist? this.containsKey(key): false,Boolean.TRUE);
    }

    @Override
    public Option<V> get(K key) {
        this.clean();
        Option<V> map = this.cacheMap.get(key).map(CacheValue::getValue);
        return map;
    }

    @Override
    public void set(K key, V value) {
        this.cacheMap.set(key, this.createCacheValue(value));
    }

    protected CacheValue<V> createCacheValue(V value) {
        LocalDateTime now = LocalDateTime.now();
        return new CacheValue<V>() {
            @Override
            public V getValue() {
                return value;
            }

            @Override
            public LocalDateTime getCreatedAt() {
                return now;
            }
        };
    }

    @Override
    public void delete(K key) {
        this.cacheMap.delete(key);
    }

    protected interface CacheValue<V> {
        V getValue();
        LocalDateTime getCreatedAt();
    }
}
