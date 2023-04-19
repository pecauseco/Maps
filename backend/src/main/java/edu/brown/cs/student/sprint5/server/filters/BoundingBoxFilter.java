package edu.brown.cs.student.sprint5.server.filters;

import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.*;

/** Class that filters a FeatureCollection by bounding box (lat/long) */
public class BoundingBoxFilter implements FeatureFilter {

  private double minLat;
  private double maxLat;
  private double minLng;
  private double maxLng;

  /**
   * Constructor for BoundingBoxFilter.
   *
   * @param minLat the minimum latitude of the box
   * @param maxLat the maximum latitude of the box
   * @param minLng the minimum longitude of the box
   * @param maxLng the maximum longitude of the box
   */
  public BoundingBoxFilter(double minLat, double maxLat, double minLng, double maxLng) {
    this.minLat = minLat;
    this.maxLat = maxLat;
    this.minLng = minLng;
    this.maxLng = maxLng;
  }

  /**
   * Returns true if the feature meets the criteria of the filter.
   *
   * @param feature the feature to check
   * @return true if the feature meets the criteria of the filter
   */
  @Override
  public boolean featureMeetsCriteria(Feature feature) {
    if (feature.geometry() != null)
      return feature.geometry().checkBounds(this.minLat, this.maxLat, this.minLng, this.maxLng);
    else return false;
  }
}
