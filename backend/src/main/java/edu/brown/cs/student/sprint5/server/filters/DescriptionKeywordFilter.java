package edu.brown.cs.student.sprint5.server.filters;

import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.Feature;

/** Class that filters a FeatureCollection by keyword. */
public class DescriptionKeywordFilter implements FeatureFilter {

  private String keyword;

  /**
   * Constructor for DescriptionKeywordFilter.
   *
   * @param keyword the keyword to filter by
   */
  public DescriptionKeywordFilter(String keyword) {
    this.keyword = keyword;
  }

  /**
   * Returns true if the feature meets the criteria of the filter.
   *
   * @param feature the feature to check
   * @return true if the feature meets the criteria of the filter
   */
  @Override
  public boolean featureMeetsCriteria(Feature feature) {
    if (feature.properties() != null)
      return feature.properties().area_description_data().values().stream()
          .anyMatch(s -> s.toLowerCase().contains(this.keyword.toLowerCase()));
    else return false;
  }
}
