package edu.brown.cs.student.sprint5.server.filters;

import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.Feature;

/** Interface for a class that can specify if a Feature object meets criteria to pass a filter. */
public interface FeatureFilter {

  /**
   * Returns true if the feature meets the criteria of the filter.
   *
   * @param feature - the feature to check
   * @return - true if the feature meets the criteria of the filter
   */
  boolean featureMeetsCriteria(Feature feature);
}
