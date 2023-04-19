package edu.brown.cs.student.sprint5.server.proxies;

import static edu.brown.cs.student.sprint5.Constants.*;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.sprint5.server.datasources.CacheUtils;
import edu.brown.cs.student.sprint5.server.filters.DescriptionKeywordFilter;
import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat;
import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.FeatureCollection;
import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;
import java.util.*;
import spark.Request;

/** Proxy class for keyword filtering the area_description_data field on a FeatureCollection. */
public class KeywordSearchProxy implements Proxy {

  private FeatureCollection data;
  private LoadingCache<String, ServerResponse> cache;

  /**
   * Constructor for KeywordSearchProxy.
   *
   * @param data FeatureCollection to be filtered
   */
  public KeywordSearchProxy(FeatureCollection data) {
    this.data = data;
    this.cache =
        CacheUtils.generateCache(
            new CacheLoader<>() {
              @Override
              public ServerResponse load(String keyword) throws Exception {
                return getResponse(keyword);
              }
            },
            CACHE_MAX_SIZE,
            CACHE_EXPIRE_TIME,
            CACHE_EXPIRE_UNIT);
  }

  /**
   * Returns a ServerResponse containing the filtered FeatureCollection.
   *
   * @param keyword Request object containing the keyword
   * @return ServerResponse containing the filtered FeatureCollection
   */
  private ServerResponse getResponse(String keyword) {
    if (keyword.equals("")) return new ServerResponse(ERROR_BAD_REQUEST, NO_KEYWORD);
    DescriptionKeywordFilter filter = new DescriptionKeywordFilter(keyword);
    Set<FeatureCollectionFormat.Feature> filteredFeatures =
        this.data.filterFeatureCollection(filter);
    Map<String, Object> responseMap = new HashMap<>();
    responseMap.put(FEATURES, new FeatureCollection(this.data.type(), filteredFeatures));
    return new ServerResponse(responseMap);
  }

  /**
   * Returns a ServerResponse containing the filtered FeatureCollection.
   *
   * @param request Request object containing the keyword
   * @return ServerResponse containing the filtered FeatureCollection
   * @throws Exception if there is an error in retrieval from the cache
   */
  @Override
  public ServerResponse getResponsePercolate(Request request) throws Exception {
    String keyword = request.queryParams(KEYWORD_PARAM);
    if (keyword == null) {
      return new ServerResponse(ERROR_BAD_REQUEST, NO_KEYWORD);
    }
    return this.cache.get(keyword.toLowerCase());
  }

  /**
   * Checks if the cache contains a response for a certain keyword search
   *
   * @param keyword the query parameters of the request to check for
   * @return
   */
  @Override
  public boolean cacheContains(String keyword) {
    return this.cache.asMap().containsKey(keyword.toLowerCase());
  }

  /**
   * Returns a set of the known parameters for this proxy.
   *
   * @return Set of known parameters
   */
  @Override
  public Set<String> getKnownQueryParams() {
    return KEYWORD_PARAMS;
  }
}
