package edu.brown.cs.student.sprint5.server.responseformatting;

import com.squareup.moshi.Json;
import edu.brown.cs.student.sprint5.server.filters.FeatureFilter;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** Class for the format of the feature collection. */
public class FeatureCollectionFormat {

  /**
   * Class for the feature collection.
   *
   * @param type The type of the feature collection.
   * @param features The features in the feature collection.
   */
  public record FeatureCollection(
      @Json(name = "type") String type, @Json(name = "features") Set<Feature> features) {
    public Set<Feature> filterFeatureCollection(FeatureFilter filterStrategy) {
      Set<Feature> feats =
          this.features.parallelStream()
              .filter(filterStrategy::featureMeetsCriteria)
              .collect(Collectors.toSet());
      return feats;
    }
  }

  /**
   * Class for a feature.
   *
   * @param type The type of the feature.
   * @param geometry The geometry of the feature.
   * @param properties The properties of the feature.
   */
  public record Feature(
      @Json(name = "type") String type,
      @Json(name = "geometry") Geometry geometry,
      @Json(name = "properties") Properties properties) {}

  /**
   * Class for the geometry of a feature.
   *
   * @param coordinates The coordinates of the geometry.
   * @param type The type of the geometry.
   */
  public record Geometry(
      @Json(name = "coordinates") double[][][][] coordinates, @Json(name = "type") String type) {
    public boolean checkBounds(double minLat, double maxLat, double minLng, double maxLng) {
      double[] coord = this.coordinates[0][0][0];
      double lat = coord[1];
      double lng = coord[0];
      return maxLat >= lat && minLat <= lat && maxLng >= lng && minLng <= lng;
    }
  }

  /**
   * Class for the properties of a feature.
   *
   * @param city The city of the feature.
   * @param state The state of the feature.
   * @param name The name of the feature.
   * @param holc_id The holc id of the feature.
   * @param holc_grade The holc grade of the feature.
   * @param neighborhood_id The neighborhood id of the feature.
   * @param area_description_data The area description data of the feature.
   */
  public record Properties(
      @Json(name = "city") String city,
      @Json(name = "state") String state,
      @Json(name = "name") String name,
      @Json(name = "holc_id") String holc_id,
      @Json(name = "holc_grade") String holc_grade,
      @Json(name = "neighborhood_id") int neighborhood_id,
      @Json(name = "area_description_data") Map<String, String> area_description_data) {}
}
