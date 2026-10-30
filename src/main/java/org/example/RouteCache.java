package org.example;

import java.util.LinkedHashMap;
import java.util.Map;

public class RouteCache {

    private static final int MAX_CACHE_SIZE = 1000;

    private static final long EXPIRY_TIME =
            30 * 60 * 1000; // 30 minutes

    private Map<String, CacheEntry> cache =
            new LinkedHashMap<>();

    private static class CacheEntry {

        double distance;
        long createdAt;

        CacheEntry(double distance) {
            this.distance = distance;
            this.createdAt = System.currentTimeMillis();
        }
    }

    // Generate a key for each route
    private String createKey(GpsPoint start, GpsPoint end) {

        return Math.round(start.getLatitude() * 1000) + "_"
                + Math.round(start.getLongitude() * 1000) + "_"
                + Math.round(end.getLatitude() * 1000) + "_"
                + Math.round(end.getLongitude() * 1000);
    }

    // Check whether a route exists
    public synchronized boolean containsRoute(
            GpsPoint start, GpsPoint end) {

        return getRoute(start, end) != null;
    }

    // Save route distance
    public synchronized void saveRoute(
            GpsPoint start,
            GpsPoint end,
            double distance) {

        removeExpiredRoutes();

        String key = createKey(start, end);

        // Remove oldest entry if cache is full
        if (!cache.containsKey(key)
                && cache.size() >= MAX_CACHE_SIZE) {

            String oldestKey =
                    cache.keySet().iterator().next();

            cache.remove(oldestKey);
        }

        cache.put(key, new CacheEntry(distance));
    }

    // Retrieve route distance
    public synchronized Double getRoute(
            GpsPoint start,
            GpsPoint end) {

        String key = createKey(start, end);

        CacheEntry entry = cache.get(key);

        if (entry == null) {
            return null;
        }

        // Check expiry
        if (System.currentTimeMillis()
                - entry.createdAt > EXPIRY_TIME) {

            cache.remove(key);
            return null;
        }

        return entry.distance;
    }

    // Remove expired routes
    private void removeExpiredRoutes() {

        long currentTime = System.currentTimeMillis();

        cache.entrySet().removeIf(entry ->
                currentTime - entry.getValue().createdAt
                        > EXPIRY_TIME);
    }

    // Get cache size
    public synchronized int getCacheSize() {
        removeExpiredRoutes();
        return cache.size();
    }
}