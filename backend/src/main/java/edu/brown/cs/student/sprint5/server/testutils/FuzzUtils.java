package edu.brown.cs.student.sprint5.server.testutils;

import static edu.brown.cs.student.sprint5.Constants.*;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class FuzzUtils {

  private static final String ALPHA_NUMERIC_STRING =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  /**
   * Generates a random bounding box as a string. Used to fuzz test the bounding box endpoint.
   *
   * @return a random bounding box as a string in the order minLat, minLng, maxLat, maxLng
   */
  public static Map<String, String> generateRandomBoundingBox() {
    // multiply bounds by two to get coordinates that are in and outside of bounds
    double lat1 = ThreadLocalRandom.current().nextDouble(LAT_LOWER_BOUND * 2, LAT_UPPER_BOUND * 2);
    double lon1 = ThreadLocalRandom.current().nextDouble(LNG_LOWER_BOUND * 2, LNG_UPPER_BOUND * 2);
    double lat2 = ThreadLocalRandom.current().nextDouble(LAT_LOWER_BOUND * 2, LAT_UPPER_BOUND * 2);
    double lon2 = ThreadLocalRandom.current().nextDouble(LNG_LOWER_BOUND * 2, LNG_UPPER_BOUND * 2);
    Map<String, String> params =
        new HashMap<>(
            Map.of(
                "minLat", String.valueOf(lat1),
                "minLng", String.valueOf(lon1),
                "maxLat", String.valueOf(lat2),
                "maxLng", String.valueOf(lon2)));

    // possibly replace random coordinates with random strings
    for (String param : params.keySet()) {
      if (ThreadLocalRandom.current().nextBoolean()) {
        params.put(param, getRandomURLStringBounded(2));
      }
    }

    // select random coordinates to remove
    for (int i = 0; i < params.keySet().stream().toList().size(); i++) {
      if (ThreadLocalRandom.current().nextBoolean()) {
        params.remove(params.keySet().stream().toList().get(i));
        i++;
      }
    }

    // possibly add a coordinate, which may or may not be a double
    if (ThreadLocalRandom.current().nextBoolean()) {
      params.put(getRandomURLStringBounded(10), getRandomURLStringBounded(2));
    }

    return params;
  }

  /**
   * Generates a random String that is URL-safe.
   *
   * @return a random keyword as a string
   */
  public static String getRandomURLStringBounded(int length) {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    StringBuilder sb = new StringBuilder();
    for (int iCount = 0; iCount < length; iCount++) {
      // upper-bound is exclusive
      // append random character from alphanumeric string
      int code = r.nextInt(1, ALPHA_NUMERIC_STRING.length());
      sb.append(ALPHA_NUMERIC_STRING.charAt(code));
    }
    return sb.toString();
  }
}
