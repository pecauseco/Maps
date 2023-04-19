import { FeatureCollection, Position } from "geojson";
import { FillLayer, LineLayer } from "react-map-gl";
import { ServerResponse } from "./server-response";
import { BoundingBox } from "../components/control-panel";
import fullDataset from "../../../backend/data/geodata/redlining.json";

/**
 * This is a type guard to check if a json object is a FeatureCollection
 * @param json the json to check if it is a FeatureCollection
 * @returns true if the json is a FeatureCollection, false otherwise
 */
function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

/**
 * This function loads the full dataset from the json file, and returns it as a FeatureCollection
 * @returns the redlining data as a FeatureCollection, or undefined if it is not a FeatureCollection
 */
export function overlayData(): GeoJSON.FeatureCollection {
  return isFeatureCollection(fullDataset) ? fullDataset : { type: "FeatureCollection", features: [] };
}

/**
 * Takes a bounding box to filter by, builds a url to call the appropriate endpoint, and obtains
 * a FeatureCollection from the server.
 * @param coords the coordinates to filter by (minLat, maxLat, minLng, maxLng)
 * @returns a Promise of the features from the response, or an empty FeatureCollection if none exist
 */
export async function extractRedliningOverlay(
  coords: BoundingBox
): Promise<FeatureCollection> {
  let url: string =
    "http://localhost:3232/boundingBox?minLat=" +
    coords.minLat +
    "&maxLat=" +
    coords.maxLat +
    "&minLng=" +
    coords.minLng +
    "&maxLng=" +
    coords.maxLng;
  return extractFeaturesFromURL(url);
}

/**
 * Takes a keyword to search, builds a url to call the appropriate endpoint, and obtains
 * a FeatureCollection from the server.
 * @param keyword the string to search for in descriptions
 * @returns aPromise of the features from the response, or an empty FeatureCollection if none exist
 */
export async function extractSearchOverlay(
  keyword: string
): Promise<FeatureCollection> {
  let url = "http://localhost:3232/describedBy?keyword=" + keyword;
  return extractFeaturesFromURL(url);
}

/**
 * Extracts the redlining data directly from the json file, without calling the server.
 * This is used for testing purposes, as it allows us to mock the server response.
 * @param coords the coordinates to filter by (minLat, maxLat, minLng, maxLng)
 * @returns Promise<FeatureCollection> that resolves to a FeatureCollection that meets the criteria of the query
 */
export async function extractMockedRedliningOverlay(
  coords: BoundingBox
): Promise<FeatureCollection> {
  let minLat: number = parseFloat(coords.minLat);
  let maxLat: number = parseFloat(coords.maxLat);
  let minLng: number = parseFloat(coords.minLng);
  let maxLng: number = parseFloat(coords.maxLng);

  let filteredFeatures: FeatureCollection = {
    type: "FeatureCollection",
    features: [],
  };
  if (isFeatureCollection(fullDataset) && minLat <= maxLat && minLng <= maxLng) {
    for (let feature of fullDataset.features) {
      if (feature.geometry === null) continue;
      if (
        feature.geometry.type === "Polygon" &&
        feature.geometry.coordinates.length > 0
      ) {
        for (let coord of feature.geometry.coordinates) {
          if (coord[0][1] >= minLng && coord[0][1] <= maxLng) {
            if (coord[1][0] >= minLat && coord[1][0] <= maxLat) {
              filteredFeatures.features.push(feature);
            }
          }
        }
      } else if (
        feature.geometry.type === "MultiPolygon" &&
        feature.geometry.coordinates.length > 0
      ) {
        for (let coord of feature.geometry.coordinates) {
          if (coord[0][0][0] >= minLng && coord[0][0][0] <= maxLng) {
            if (coord[0][1][1] >= minLat && coord[0][1][1] <= maxLat) {
              filteredFeatures.features.push(feature);
            }
          }
        }
      }
    }
  }

  return new Promise<FeatureCollection>((resolve) => {
    if (filteredFeatures.features.length > 0) {
      resolve(filteredFeatures);
    } else {
      resolve({ type: "FeatureCollection", features: [] });
    }
  });
}

/**
 * Extracts the search data directly from the json file, without calling the server.
 * This is used for testing purposes, as it allows us to mock the server response.
 * @param keyword the string to search for in descriptions
 * @returns Promise<FeatureCollection> that resolves to a FeatureCollection that meets the criteria of the query
 */
export async function extractMockedSearchOverlay(
  keyword: string
): Promise<FeatureCollection> {
  let filteredFeatures: FeatureCollection = {
    type: "FeatureCollection",
    features: [],
  };
  if (isFeatureCollection(fullDataset)) {
    for (let feature of fullDataset.features) {
      if (feature.properties) {
        if (feature.properties.area_description_data) {
          // iterate over the values of area_description_data
          let values: string[] = Object.values(feature.properties.area_description_data);
          for (let value of values) {
            if (value) {
              if (value.toLowerCase().includes(keyword.toLowerCase())) {
                filteredFeatures.features.push(feature);
              }
          }
          }
        }
      }
    }
  }

  return new Promise<FeatureCollection>((resolve) => {
    if (filteredFeatures.features.length > 0) {
      resolve(filteredFeatures);
    } else {
      resolve({ type: "FeatureCollection", features: [] });
    }
  });
}

/**
 * Calls the server to get a FeatureCollection that meets the criteria of a given filtering query
 * @param url the url to call to get the FeatureCollection
 * @returns a Promise<FeatureCollecion> that resolves to a FeatureCollection that meets the criteria of the query
 */
async function extractFeaturesFromURL(url: string): Promise<FeatureCollection> {
  let response: Response = await fetch(url);
  let serverResponse: ServerResponse = await response.json();

  return new Promise<FeatureCollection>((resolve) => {
    if (serverResponse.result === "success") {
      if (serverResponse.data) {
        if (serverResponse.data.featCollection) {
          if (
            isFeatureCollection(serverResponse.data.featCollection) &&
            serverResponse.data.featCollection.features.length > 0
          ) {
            resolve(serverResponse.data.featCollection);
          } else {
            resolve({ type: "FeatureCollection", features: [] });
          }
        }
      }
    } else {
      console.log("Error: " + serverResponse.errorReason);
      resolve({ type: "FeatureCollection", features: [] });
    }
  });
}

/**
 * The property name that will be used to determine the color of the redlining data
 */
const propertyName = "holc_grade";
/**
 * The layer that will be used to display the redlining data
 */
export const redliningLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      // we adjusted the colors to be more visible and to better
      // increase contrast for colorblind users
      "A",
      "#339EFF",
      "B",
      "#33FFDA",
      "C",
      "#EBFF33",
      "D",
      "#FFD533",
      "#ccc",
    ],
    "fill-opacity": 0.5,
  },
};

/**
 * The layer that will be used to display the highlighted redlining data,
 * which is the data that meets a user's keyword filtering criteria
 */
export const highlightLayer: LineLayer = {
  id: "geo_highlight",
  type: "line",
  paint: {
    "line-color": "#ff69b4",
    "line-width": 2,
  },
};
