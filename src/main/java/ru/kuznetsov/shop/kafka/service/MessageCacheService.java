package ru.kuznetsov.shop.kafka.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageCacheService<E> {

    @Value("${message.ttl.seconds:60}")
    private Integer objectTtl;

    private final Map<E, LocalDateTime> messageCache = new ConcurrentHashMap<>();

    public void put(E id) {
        messageCache.put(id, LocalDateTime.now());
    }

    public boolean exists(E id) {
        return messageCache.containsKey(id);
    }

    public void removeBefore(LocalDateTime date) {
        messageCache.entrySet().removeIf(entry -> entry.getValue().isBefore(date));
    }

    @Scheduled(fixedRateString = "${message.ttl.seconds:60}")
    public void clearCache() {
        removeBefore(LocalDateTime.now().minusSeconds(objectTtl));
    }
}
