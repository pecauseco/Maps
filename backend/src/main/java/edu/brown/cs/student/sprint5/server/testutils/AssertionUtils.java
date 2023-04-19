package edu.brown.cs.student.sprint5.server.testutils;

import static edu.brown.cs.student.sprint5.Constants.*;
import static edu.brown.cs.student.sprint5.server.datasources.JSONReader.fromJsonString;
import static edu.brown.cs.student.sprint5.server.datasources.JSONReader.toJson;

import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.FeatureCollection;
import edu.brown.cs.student.sprint5.server.responseformatting.ServerResponse;

public class AssertionUtils {

  /**
   * This method is used to check if the data contained in a successful response from the server is
   * a FeatureCollection. It does this by converting the data to JSON String and then converting it
   * back to a FeatureCollection object. If the conversion is successful, then the data meets
   * GeoJSON standards for a FeatureCollection.
   *
   * @param response the response from the server
   * @return true if the response is a FeatureCollection, false otherwise
   */
  public static boolean checkDataHasFeatureCollection(ServerResponse response) {
    try {
      String json = toJson(response.data().get(FEATURES), RESPONSE_TYPE);
      fromJsonString(json, FeatureCollection.class);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * This method is used to convert the data contained in a successful response from the server to a
   * FeatureCollection. It does this by converting the data to JSON String and then converting it
   * back to a FeatureCollection object.
   *
   * @param response the response from the server
   * @return FeatureCollection object containing the data from the response
   */
  public static FeatureCollection getFeatureCollection(ServerResponse response) {
    try {
      String json = toJson(response.data().get(FEATURES), RESPONSE_TYPE);
      return fromJsonString(json, FeatureCollection.class);
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * This method is used to check if a FeatureCollection contains any features.
   *
   * @param collection the FeatureCollection to check
   * @return true if the FeatureCollection contains features, false otherwise
   */
  public static boolean checkHasFeatures(FeatureCollection collection) {
    return collection.features().size() > 0;
  }
}
