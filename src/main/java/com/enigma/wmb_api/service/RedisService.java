package com.enigma.wmb_api.service;

import java.time.Duration;

public interface RedisService {
    void save(String key, String value, Duration duration);
    String get(String key);
    void delete(String key);
    Boolean isExist(String key);
}
