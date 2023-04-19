package edu.brown.cs.student.sprint5.server.proxies;

import static edu.brown.cs.student.sprint5.Constants.*;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import edu.brown.cs.student.sprint5.server.datasources.CacheUtils;
import edu.brown.cs.student.sprint5.server.filters.BoundingBoxFilter;
import edu.brown.cs.student.sprint5.server.filters.FeatureFilter;
import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat;
import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.*;
import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;
import java.util.*;
import spark.Request;

/** Proxy class for bounding box filtering a FeatureCollection. */
public class BoundingBoxProxy implements Proxy {

  private FeatureCollection data;
  private LoadingCache<String, ServerResponse> cache;

  /**
   * Constructor for BoundingBoxProxy.
   *
   * @param data FeatureCollection to be filtered
   */
  public BoundingBoxProxy(FeatureCollection data) {
    this.data = data;
    this.cache =
        CacheUtils.generateCache(
            new CacheLoader<>() {
              @Override
              public ServerResponse load(String paramsString) throws Exception {
                return getResponse(paramsString);
              }
            },
            CACHE_MAX_SIZE,
            CACHE_EXPIRE_TIME,
            CACHE_EXPIRE_UNIT);
  }

  /**
   * Returns a ServerResponse containing the filtered FeatureCollection.
   *
   * @param paramsString string containing the parameters
   * @return ServerResponse containing the filtered FeatureCollection
   */
  private ServerResponse getResponse(String paramsString) {
    try {
      String[] params = paramsString.split("&");
      double minLat = Double.parseDouble(params[0]);
      double maxLat = Double.parseDouble(params[2]);
      boolean validLats = checkValidCoords(minLat, maxLat, true);

      double minLng = Double.parseDouble(params[1]);
      double maxLng = Double.parseDouble(params[3]);
      boolean validLngs = checkValidCoords(minLng, maxLng, false);

      if (!validLats || !validLngs) {
        return new ServerResponse(ERROR_BAD_REQUEST, COORD_OUT_OF_BOUNDS);
      }

      if (minLat > maxLat || minLng > maxLng)
        return new ServerResponse(ERROR_BAD_REQUEST, MIN_GREATER_THAN_MAX);

      if (minLat == LAT_LOWER_BOUND
          && maxLat == LAT_UPPER_BOUND
          && minLng == LNG_LOWER_BOUND
          && maxLng == LNG_UPPER_BOUND) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(FEATURES, this.data);
        return new ServerResponse(responseMap);
      } else {
        FeatureFilter filter = new BoundingBoxFilter(minLat, maxLat, minLng, maxLng);
        Set<FeatureCollectionFormat.Feature> filteredFeatures =
            this.data.filterFeatureCollection(filter);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put(FEATURES, new FeatureCollection(this.data.type(), filteredFeatures));
        return new ServerResponse(responseMap);
      }
    } catch (NumberFormatException e) {
      return new ServerResponse(ERROR_BAD_JSON, INVALID_LAT_LNG);
    }
  }

  /**
   * Returns a ServerResponse containing the filtered FeatureCollection.
   *
   * @param request Request object containing the bounding box coordinates
   * @return ServerResponse containing the filtered FeatureCollection
   * @throws Exception if there is an error in retrieval from the cache
   */
  @Override
  public ServerResponse getResponsePercolate(Request request) throws Exception {
    String paramsString =
        request.queryParams(MIN_LAT_PARAM)
            + "&"
            + request.queryParams(MIN_LNG_PARAM)
            + "&"
            + request.queryParams(MAX_LAT_PARAM)
            + "&"
            + request.queryParams(MAX_LNG_PARAM);
    return this.cache.get(paramsString);
  }

  /**
   * Returns a set of the known parameters.
   *
   * @return set of the known parameters
   */
  @Override
  public Set<String> getKnownQueryParams() {
    return BOUNDING_BOX_PARAMS;
  }

  /**
   * Checks if the response to the request corresponding to paramsString is cached.
   *
   * @param paramsString string containing the parameters (minLat, minLng, maxLat, maxLng) separated
   *     by '&' (e.g. "42.0&-71.0&43.0&-70.0")
   * @return true if the response is cached, false otherwise
   */
  @Override
  public boolean cacheContains(String paramsString) {
    return this.cache.asMap().containsKey(paramsString);
  }

  /**
   * Checks if the given coordinates are valid.
   *
   * @param min minimum coordinate
   * @param max maximum coordinate
   * @param isLat specifies if the coordinate are latitudes. If false, coordinates are assumed to be
   *     longitudes.
   * @return true if the coordinates are valid, false otherwise
   */
  private static boolean checkValidCoords(double min, double max, boolean isLat) {
    if (isLat) {
      return min >= LAT_LOWER_BOUND
          && min <= LAT_UPPER_BOUND
          && max >= LAT_LOWER_BOUND
          && max <= LAT_UPPER_BOUND;
    } else {
      return min >= LNG_LOWER_BOUND
          && min <= LNG_UPPER_BOUND
          && max >= LNG_LOWER_BOUND
          && max <= LNG_UPPER_BOUND;
    }
  }
}
