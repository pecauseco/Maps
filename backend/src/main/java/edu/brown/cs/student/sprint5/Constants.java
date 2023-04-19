package edu.brown.cs.student.sprint5;

import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Constants {

  /** The path to the file that contains the data for the redlining GeoJson. */
  public static final String REDLINING_PATH = "data/geodata/redlining.json";

  // parametrized type constants:
  /** Creates a parameterized type that allows Moshi to serialize a ServerResponse object. */
  public static final Type RESPONSE_TYPE =
      Types.newParameterizedType(Map.class, String.class, Object.class);

  // lat/lon bound constants:
  /** The lower bound for acceptable latitude values. */
  public static final int LAT_LOWER_BOUND = -90;
  /** The upper bound for acceptable latitude values. */
  public static final int LAT_UPPER_BOUND = 90;
  /** The lower bound for acceptable longitude values. */
  public static final int LNG_LOWER_BOUND = -180;
  /** The upper bound for acceptable longitude values. */
  public static final int LNG_UPPER_BOUND = 180;

  // cache parameters:
  /** The maximum number of entries that the cache will hold. */
  public static final int CACHE_MAX_SIZE = 1000;
  /** The number of seconds that the cache will hold entries before expiring them. */
  public static final int CACHE_EXPIRE_TIME = 10;
  /** The unit of time that the cache will use to expire entries. */
  public static final TimeUnit CACHE_EXPIRE_UNIT = TimeUnit.MINUTES;

  // bounding box handler constants:
  /** The name of the bounding box endpoint. */
  public static final String BOUNDING_BOX_ENDPOINT = "boundingBox";
  /** The maximum latitude argument that the bounding box handler will accept. */
  public static final String MAX_LAT_PARAM = "maxLat";
  /** The minimum latitude argument that the bounding box handler will accept. */
  public static final String MIN_LAT_PARAM = "minLat";
  /** The maximum longitude argument that the bounding box handler will accept. */
  public static final String MAX_LNG_PARAM = "maxLng";
  /** The minimum longitude argument that the bounding box handler will accept. */
  public static final String MIN_LNG_PARAM = "minLng";
  /** The set of arguments that the bounding box handler will accept. */
  public static final Set<String> BOUNDING_BOX_PARAMS =
      new HashSet<>(List.of(MIN_LAT_PARAM, MIN_LNG_PARAM, MAX_LAT_PARAM, MAX_LNG_PARAM));

  // description search handler constants:
  /** The name of the overlay endpoint. */
  public static final String KEYWORD_ENDPOINT = "describedBy";
  /** The minimum longitude argument that the overlay handler will accept. */
  public static final String KEYWORD_PARAM = "keyword";
  /** The set of arguments that the overlay handler will accept. */
  public static final Set<String> KEYWORD_PARAMS = new HashSet<>(List.of(KEYWORD_PARAM));

  // constants for success and error messages:
  /** The message that the server will send to the client if the request was successful. */
  public static final String SUCCESS_MESSAGE = "success";
  /**
   * The key of the response map that will contain a FeatureCollection in GeoJson format if the
   * request was successful.
   */
  public static final String FEATURES = "featCollection";
  /**
   * The general message that the server will send to the client if there is a problem with the
   * datasource.
   */
  public static final String ERROR_DATASOURCE = "error_datasource";
  /**
   * The general message that the server will send to the client if there are problems with the
   * request. i.e. there are missing parameters, or the arguments passed to the request are invalid.
   */
  public static final String ERROR_BAD_REQUEST = "error_bad_request";
  /**
   * The general message that the server will send to the client if the request is malformed. i.e.
   * there are parameters that are not recognized.
   */
  public static final String ERROR_BAD_JSON = "error_bad_json";
  /**
   * The general message that the server will send to the client if the redlining data could not be
   * loaded.
   */
  public static final String DATA_LOAD_FAILURE = "%s_overlay_data_could_not_be_loaded.";
  /**
   * A more specific message that the server will send to the client if there are missing parameters
   * in the request.
   */
  public static final String MISSING_PARAMS = "missing_params";
  /**
   * A more specific message that the server will send to the client if there are parameters that
   * are not recognized.
   */
  public static final String UNKNOWN_PARAMS = "unknown_params";
  /**
   * A more specific message that the server will send to the client if the latitude passed to the
   * request is invalid.
   */
  public static final String UNKNOWN_PARAM_MESSAGE = "unknown param %s with value";
  /**
   * A more specific message that the server will send to the client if there are no parameters
   * passed to the request.
   */
  public static final String NO_PARAMS = "no_params";

  // constants for bounding box error messages:
  /**
   * A more specific message that the server will send to the client if any of the latitude or
   * longitude arguments passed to the request are not formatted as doubles.
   */
  public static final String INVALID_LAT_LNG =
      "latitudes/longitudes_must_be_formatted_as_decimals_"
          + "with_a_period_as_the_decimal_separator";
  /**
   * A more specific message that the server will send to the client if any of the latitudes are out
   * of bounds.
   */
  public static final String COORD_OUT_OF_BOUNDS =
      "latitudes_must_be_between_-90_and_90,_and_" + "longitudes_must_be_between_-180_and_180";
  /**
   * A more specific message that the server will send to the client if the minimum
   * latitude/longitude is greater than the maximum latitude/longitude.
   */
  public static final String MIN_GREATER_THAN_MAX =
      "min_lat_must_be_less_than_max_lat,_and_" + "min_lng_must_be_less_than_max_lng";

  // constants for keyword search error messages:
  /**
   * A more specific message that the server will send to the client if no keyword argument is
   * passed
   */
  public static final String NO_KEYWORD = "you_must_pass_a_keyword_to_filter_by";
}
