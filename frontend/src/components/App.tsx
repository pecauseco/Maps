import { FeatureCollection } from "geojson";
import { useState } from "react";
import "../styles/App.css";
import ControlPanel, { BoundingBox } from "./control-panel";
import LocationPanel from "./location-panel";
import MapPanel from "./map-panel";
import {
  extractSearchOverlay,
  extractRedliningOverlay,
  overlayData 
} from "../data-utils/filter-overlays";

/**
 * This is the accessible role name of the app container.
 * It is used to set the role of the app container.
 */
export const appAccessibleRoleName: string =
  "Redlining Map Application Container";

/**
 * This function represents the App component that is used to render the map,
 * control panel, and location panel. It handles the state of the map and
 * related events.
 * @returns the HTML of the App component
 */
export default function App() {
  /**
   * This state represents the bound overlay, which is the redlining data
   * that is currently being displayed on the map baswed on the user's
   * specified bounding box.
   */
  const [boundOverlay, setBoundOverlay] = useState<FeatureCollection>(
    overlayData()
  );
  /**
   * This state represents the search overlay, which is the redlining data
   * that is currently being displayed on the map based on the user's
   * specified area description data keyword filter query.
   */
  const [searchOverlay, setSearchOverlay] = useState<FeatureCollection>({
    type: "FeatureCollection",
    features: [],
  });
  /**
   * This state represents the location data that is currently being displayed
   * on the location panel. It is an object that contains the state, city, and
   * name of a location that the user clicks on the map, if available.
   */
  const [locationData, setLocationData] = useState({
    stateData: "n/a",
    cityData: "n/a",
    nameData: "n/a",
  });

  /**
   * tsx of the App component
   */
  return (
    <div
      className="App"
      aria-role={appAccessibleRoleName}
      aria-description="This is the main container for the redlining map application. From left to right,
      the first column is where you can filter and search data, the center pane is the interactive map with
      data overlays, and the third column displays the state, city, and name of a location that you click on the map,
      if those properties are available."
    >
      <ControlPanel
        filterBoundOverlay={extractRedliningOverlay}
        filterSearchOverlay={extractSearchOverlay}
        setBoundOverlay={setBoundOverlay}
        setSearchOverlay={setSearchOverlay}
      />
      <MapPanel
        boundOverlay={boundOverlay}
        searchOverlay={searchOverlay}
        setLocationData={setLocationData}
      />
      <LocationPanel {...locationData} />
    </div>
  );
}
