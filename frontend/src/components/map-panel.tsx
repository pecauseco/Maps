import "mapbox-gl/dist/mapbox-gl.css";
import Map, {
  Layer,
  MapLayerMouseEvent,
  Source,
  ViewStateChangeEvent,
  MapboxGeoJSONFeature,
  PointLike,
  MapRef,
} from "react-map-gl";
import { redliningLayer, highlightLayer } from "../data-utils/filter-overlays";
import { RefObject, createRef, useState } from "react";
import ACCESS_TOKEN from "../../private/access-token";

/**
 * These are the constants that represent the accessible role names of the
 * map container, map, redlining data source, redlining data layer, highlight
 * data source, and highlight data layer.
 */
export const mapContainerAccessibleRoleName: string =
  "Interactive Map Container";

/**
 * This interface represents the props of the MapPanel component.
 */
export interface MapPanelProps {
  boundOverlay: GeoJSON.FeatureCollection;
  searchOverlay: GeoJSON.FeatureCollection;
  setLocationData: (data: {
    stateData: string;
    cityData: string;
    nameData: string;
  }) => void;
}

/**
 * This interface represents the LatLong object that is used to represent
 * the latitude and longitude of a location
 */
interface LatLong {
  lat: number;
  long: number;
}

/**
 * This function represents the MapPanel component that is used to render the map.
 * It handles the state of the map and related events.
 * @param props the props of the MapPanel component
 * @returns the HTML of the MapPanel component
 */
export default function MapPanel(props: MapPanelProps) {
  /**
   * This constant represents the latitude and longitude of Providence, RI,
   * which is the initial location of the map.
   */
  const ProvidenceLatLong: LatLong = { lat: 41.824, long: -71.4128 };
  /**
   * This constant represents the initial zoom of the map.
   */
  const initialZoom = 10;
  /**
   * This state represents the view state of the map, which includes the
   * latitude, longitude, and zoom of the map.
   */
  const [viewState, setViewState] = useState({
    longitude: ProvidenceLatLong.long,
    latitude: ProvidenceLatLong.lat,
    zoom: initialZoom,
  });
  /**
   * This RefObject is used to reference the map. Particularly, it is used
   * to get the feature that was clicked on the map. This is important for
   * getting the state, city, and name of a location that the user clicks
   * on the map.
   */
  const mapRef: RefObject<MapRef> = createRef();

  /**
   * This is the mapClick method that is invoked on click of the map. When clicked, it sets the state,
   * city, and name of the location clicked if applicable
   * @param e - the event
   * @param mapRef - a reference to the map
   */
  function onMapClick(e: MapLayerMouseEvent) {
    //default set to n/a
    let stateResponse: string = "n/a";
    let cityResponse: string = "n/a";
    let nameResponse: string = "n/a";

    //bounding box
    const bbox: [PointLike, PointLike] = [
      [e.point.x, e.point.y],
      [e.point.x, e.point.y],
    ];

    // perform null checks
    if (mapRef.current !== null && mapRef.current !== undefined) {
      // gets the feature that was clicked
      let feature: MapboxGeoJSONFeature =
        mapRef.current.queryRenderedFeatures(bbox)[0];

      // null checks the feature
      if (feature !== null && feature !== undefined) {
        //null checks the feature
        if (feature.properties !== null && feature.properties !== undefined) {
          // null checks the properties and updates states accordingly
          if (feature.properties.hasOwnProperty("state")) {
            stateResponse = feature.properties.state;
          }
          if (feature.properties.hasOwnProperty("city")) {
            cityResponse = feature.properties.city;
          }
          if (feature.properties.hasOwnProperty("name")) {
            nameResponse = feature.properties.name;
          }
        }
      }
    }
    // perform state update
    props.setLocationData({
      stateData: stateResponse,
      cityData: cityResponse,
      nameData: nameResponse,
    });
  }

  /**
   * This is the tsx/HTML of the MapPanel component.
   */
  return (
    <div
      className="map-container"
      role={mapContainerAccessibleRoleName}
      aria-label="Interactive Map Container"
      aria-description="This is the map container. It contains the interactive map with data overlays."
    >
      <Map
        {...viewState}
        ref={mapRef}
        mapboxAccessToken={ACCESS_TOKEN}
        mapStyle={"mapbox://styles/mapbox/light-v10"}
        onMove={(ev: ViewStateChangeEvent) => setViewState(ev.viewState)}
        onClick={(ev: MapLayerMouseEvent) => onMapClick(ev)}
      >
        <Source id="geo_data" type="geojson" data={props.boundOverlay}>
          <Layer {...redliningLayer} />
        </Source>
        <Source id="geo_highlight" type="geojson" data={props.searchOverlay}>
          <Layer {...highlightLayer} />
        </Source>
      </Map>
    </div>
  );
}
