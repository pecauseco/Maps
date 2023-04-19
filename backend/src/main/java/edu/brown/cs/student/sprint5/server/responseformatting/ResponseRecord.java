package edu.brown.cs.student.sprint5.server.responseformatting;

import java.util.Map;

/** Interface for a record that can encode information about a response. */
public interface ResponseRecord {

  /**
   * Serializes the record into a string, which can then be encoded into a JSON object to be sent
   * out as a response.
   *
   * @return - the serialized record
   */
  String serialize();

  /**
   * Returns a new ResponseRecord with the parameters supplied in the request that generated it.
   *
   * @param paramsMap - the parameters supplied in the request that generated the ResponseRecord
   * @return - a new ResponseRecord with the parameters supplied in the request that generated it
   */
  ResponseRecord withParams(Map<String, String[]> paramsMap);
}
