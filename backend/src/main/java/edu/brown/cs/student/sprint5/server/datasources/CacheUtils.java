package edu.brown.cs.student.sprint5.server.datasources;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.sprint5.server.responseformatting.ResponseRecord;
import java.util.concurrent.TimeUnit;

/** Utility class for generating caches. */
public class CacheUtils {

  /**
   * Static method for generating a cache.
   *
   * @param loader the CacheLoader to use
   * @param maxCacheSize the maximum size of the cache
   * @param expireAfterAccess the time after which a cache entry expires
   * @param timeUnit the time unit of the expireAfterAccess parameter
   * @return the generated cache
   * @param <K> the type of the cache's keys
   * @param <V> the type of the cache's values
   */
  public static <K, V extends ResponseRecord> LoadingCache<K, V> generateCache(
      CacheLoader<K, V> loader, long maxCacheSize, long expireAfterAccess, TimeUnit timeUnit) {
    return CacheBuilder.newBuilder()
        .maximumSize(maxCacheSize)
        .expireAfterAccess(expireAfterAccess, timeUnit)
        .recordStats()
        .build(loader);
  }
}
