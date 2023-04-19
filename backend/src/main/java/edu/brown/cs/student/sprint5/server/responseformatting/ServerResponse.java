package edu.brown.cs.student.sprint5.server.responseformatting;

import static edu.brown.cs.student.sprint5.Constants.RESPONSE_TYPE;
import static edu.brown.cs.student.sprint5.Constants.SUCCESS_MESSAGE;

import com.squareup.moshi.Json;
import com.squareup.moshi.Moshi;
import java.util.HashMap;
import java.util.Map;

/**
 * A record class that represents a generalized response from the server.
 *
 * @param responseCode - a string representing the type of response. potential values include: 1.
 *     "success" 2. "error_bad_request" 3. "error_bad_json" 4. "error_datasource"
 * @param errorSpecs - a string representing the specific error that occurred, if applicable.
 *     potential values include: 1. "error_bad_request" -> "missing_params" 2. "error_bad_json" ->
 *     "malformed_params" 3. "error_bad_json" -> "unknown_params" 3. "error_datasource" ->
 *     "file_not_found" 4. "error_datasource" -> "empty_file" 5. "error_datasource" -> "no_data"
 * @param data - a map of strings to objects that represents the data to be returned to the client.
 * @param url - the url of the request that generated the response.
 * @param params - a map of strings to strings that represents the parameters that were used to
 */
public record ServerResponse(
    /*
     * A string representing the type of response. potential values include: 1. "success" 2.
     * "error_bad_request" 3. "error_bad_json" 4. "error_datasource".
     */
    @Json(name = "result") String responseCode,
    /*
     * A string representing the specific error that occurred, if applicable. potential values
     * include: 1. "error_bad_request" -> "missing_params" 2. "error_bad_json" -> "malformed_params"
     */
    @Json(name = "errorReason") String errorSpecs,
    /*
     * A map of strings to objects that represents the data to be returned to the client.
     */
    @Json(name = "data") Map<String, Object> data,
    /*
     * A map of strings to strings that represents the parameters that were used to generate the
     * response.
     */
    @Json(name = "params") Map<String, String[]> params)
    implements ResponseRecord {

  /**
   * Constructor for a successful response.
   *
   * @param data - a map of strings to objects that represents the data to be returned to the
   *     client.
   */
  public ServerResponse(Map<String, Object> data) {
    this(SUCCESS_MESSAGE, null, data, null);
  }

  /**
   * Constructor for a response with no data; i.e., an error response of unspecified type.
   *
   * @param responseCode - a string representing the type of response
   * @param errorSpecs - a string representing the specific error that occurred
   * @param data - a map of strings to objects that represents the parameters responsible for the
   *     bad request
   */
  public ServerResponse(String responseCode, String errorSpecs, Map<String, Object> data) {
    this(responseCode, errorSpecs, data, null);
  }

  /**
   * Constructor for a response with no data; i.e., an error response of unspecified type.
   *
   * @param responseCode - a string representing the type of response
   * @param errorSpecs - a string representing the specific error that occurred
   */
  public ServerResponse(String responseCode, String errorSpecs) {
    this(responseCode, errorSpecs, null, null);
  }

  /**
   * Allows us to create a new ServerResponse to add its parameters (this way, there is no
   * unnecessary passing of data from handlers to proxies, back to handlers).
   *
   * @param params - the parameters to add to the response (all parameters of the Request object)
   * @return a new ServerResponse with the parameters added
   */
  public ServerResponse withParams(Map<String, String[]> params) {
    return new ServerResponse(this.responseCode, this.errorSpecs, this.data, params);
  }

  @Override
  public String serialize() {
    try {
      Map<String, Object> responseMap = new HashMap<>();
      responseMap.put("result", this.responseCode);
      if (this.params != null) {
        responseMap.put("paramsUsed", this.params);
      }
      if (this.errorSpecs != null) {
        responseMap.put("errorReason", this.errorSpecs);
      }
      if (this.data != null) {
        responseMap.put("data", this.data);
      }
      Moshi moshi = new Moshi.Builder().build();
      return moshi.adapter(RESPONSE_TYPE).toJson(responseMap);
    } catch (Exception e) {
      // print stack trace for debugging purposes (Spark normally will catch an exception to keep
      // the server
      // running, but we want to see the stack trace)
      e.printStackTrace();
      throw e;
    }
  }
}
