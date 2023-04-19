import { FeatureCollection } from "geojson";

/**
 * Representation of a response from our server, which requires a result,
 * optional errorReason, data if relevant (i.e. if overlay loading was successful) and params.
 */
export interface ServerResponse {
  result: string;
  errorReason: string | undefined;
  data: DataMap | undefined;
  params: Map<string, string[]>;
}

/**
 * A DataMap is a map of data that is returned from the backend server.
 * Currently, the only data that is returned is a FeatureCollection.
 */
export interface DataMap {
  featCollection: FeatureCollection;
}
