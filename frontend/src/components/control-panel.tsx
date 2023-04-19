import { useState } from "react";
import { FeatureCollection } from "geojson";
import InputBox from "./input-box";
import { 
  overlayData,
} from "../data-utils/filter-overlays";

export const controlAccessibleRoleName: string = "Control Panel Container";
export const boundBoxAccessibleRoleName: string = "Bounding Box Input Container";
export const keywordAccessibleRoleName: string = "Keyword Input Container";
export const boundSubmitAccessibleRoleName: string = "Bounding Box Filter Submit Button";
export const boundResetAccessibleRoleName: string = "Bounding Box Filter Reset Button";
export const minLatInputAccessibleRoleName: string = "Minimum Latitude Input Box";
export const maxLatInputAccessibleRoleName: string = "Maximum Latitude Input Box";
export const minLngInputAccessibleRoleName: string = "Minimum Longitude Input Box";
export const maxLngInputAccessibleRoleName: string = "Maximum Longitude Input Box";
export const keywordSubmitAccessibleRoleName: string = "Keyword Filter Submit Button";
export const keywordResetAccessibleRoleName: string = "Keyword Filter Reset Button";
export const keywordInputAccessibleRoleName: string = "Keyword Input Box";

/**
 * This interface represents the input boxes and buttons props being passed into
 * the control panel. The two props are hooks used to set the overlays of the map
 * to the bounded data and the filtered data based on the user's input.
 */
interface ControlPanelProps {
  filterBoundOverlay: (coords: BoundingBox) => Promise<FeatureCollection>;
  filterSearchOverlay: (keyword: string) => Promise<FeatureCollection>;
  setBoundOverlay: (data: FeatureCollection) => void;
  setSearchOverlay: (data: FeatureCollection) => void;
}

let numSearchResults: number = 0;
let numBoundResults: number = 0;

export interface BoundingBox {
  minLat: string;
  maxLat: string;
  minLng: string;
  maxLng: string;
}

/**
 * This is the ControlPanel function that takes in the props and handles
 * the logic for transforming user inputs to make API calls to the server
 * to get the bounded data and filtered data.
 */
export default function ControlPanel(props: ControlPanelProps) {
  /**
   * These states represent the values of the input boxes used to make
   * bounding box queries to the server.
   */
  const [minLatVal, setMinLat] = useState("");
  const [maxLatVal, setMaxLat] = useState("");
  const [minLngVal, setMinLng] = useState("");
  const [maxLngVal, setMaxLng] = useState("");
  const [searchResults, setSearchResults] = useState(numSearchResults);
  numSearchResults = searchResults;
  const [boundResults, setBoundResults] = useState(numBoundResults);
  numBoundResults = boundResults;
  /**
   * This constant represents the list of coordinates that is passed into the
   * extractRedliningOverlay function to get the bounded data from the server
   */
  const coordsList: BoundingBox = {
    minLat: minLatVal,
    maxLat: maxLatVal,
    minLng: minLngVal,
    maxLng: maxLngVal,
  };
  /**
   * This state represents the value of the keyword input box
   */
  const [keyword, setKeyword] = useState("");

  /**
   * Handles the bound submit button being clicked or the enter key being pressed!
   * It calls the extractRedliningOverlay function to get the bounded data and then
   * sets the overlay to that bounded data
   */
  async function handleBoundSubmit() {
    if (
      minLatVal === "" ||
      maxLatVal === "" ||
      minLngVal === "" ||
      maxLngVal === ""
    ) {
      return;
    }
    let data: FeatureCollection = await props.filterBoundOverlay(coordsList);
    props.setBoundOverlay(data);
    setBoundResults(data.features.length);
    setMinLat("");
    setMinLng("");
    setMaxLat("");
    setMaxLng("");
  }

  /**
   * Handles the submit button being clicked or the enter key being pressed for
   * the keyword search. It calls the extractSearchOverlay function to get the
   * filtered data and then sets the search overlay to that filtered data
   * allowing that area to be highlighted.
   *
   */
  async function handleKeywordSubmit() {
    if (keyword === "") {
      return;
    }
    let dataToHighlight: FeatureCollection = await props.filterSearchOverlay(
      keyword
    );
    props.setSearchOverlay(dataToHighlight);
    setSearchResults(dataToHighlight.features.length);
    setKeyword("");
  }

  /**
   * Handles the reset button being clicked for the bounded box.
   */
  async function handleBoundReset() {
    props.setBoundOverlay(overlayData());
    setBoundResults(overlayData().features.length);
  }

  /**
   * Handles the reset button being clicked for the keyword search. It calls the
   * setSearchOverlay function to set the search overlay to an empty FeatureCollection.
   */
  async function handleKeywordReset() {
    props.setSearchOverlay({
      type: "FeatureCollection",
      features: [],
    });
    setSearchResults(0);
  }

  /**
   * Returns the tsx Element for the control panel. It contains the input boxes and buttons
   * for the bounded box and keyword search.
   */
  return (
    <div className="controls-container"
      role={controlAccessibleRoleName}
      aria-label="Control Panel Container"
      aria-description="This is the control panel container. It contains the controls for filtering and searching data."
    >
      <div className="sub-panel"
        aria-label={keywordAccessibleRoleName}
        aria-description="This is the keyword search container. It contains the controls for searching data by keyword."
      >
        <h2>Search by keyword</h2>
        <InputBox
          defaultText="Enter keyword"
          ariaDescription="This is the keyword input box. Enter a keyword to filter for the keyword you entered."
          ariaLabel={keywordInputAccessibleRoleName}
          handleSubmit={handleKeywordSubmit}
          textbox={keyword}
          setTextbox={setKeyword}
        />
        <button
          className="input-button"
          aria-label={keywordSubmitAccessibleRoleName}
          aria-description="This is the keyword submit button. Enter a keyword above and press to filter for the keyword you entered."
          onClick={handleKeywordSubmit}
        >
          search
        </button>
        <button
          className="reset-button"
          aria-label={keywordResetAccessibleRoleName}
          onClick={handleKeywordReset}
        >
          clear search
        </button>
      </div>
      <div className="sub-panel"
        role={boundBoxAccessibleRoleName}
        aria-label="Bounding Box Input Container"
        aria-description="This is the bounding box input container. It contains the controls for filtering data by bounding box."
      >
        <h2>Bound by coordinates</h2>
        <InputBox
          defaultText="Enter min latitude"
          ariaLabel={minLatInputAccessibleRoleName}
          ariaDescription="This is the minimum latitude input box. Enter a minimum latitude for the bounding box."
          handleSubmit={handleBoundSubmit}
          textbox={minLatVal}
          setTextbox={setMinLat}
        />
        <InputBox
          defaultText="Enter min longitude"
          ariaLabel={minLngInputAccessibleRoleName}
          ariaDescription="This is the minimum longitude input box. Enter a minimum longitude for the bounding box."
          handleSubmit={handleBoundSubmit}
          textbox={minLngVal}
          setTextbox={setMinLng}
        />
        <InputBox
          defaultText="Enter max latitude"
          ariaLabel={maxLatInputAccessibleRoleName}
          ariaDescription="This is the maximum latitude input box. Enter a maximum latitude for the bounding box."
          handleSubmit={handleBoundSubmit}
          textbox={maxLatVal}
          setTextbox={setMaxLat}
        />
        <InputBox
          defaultText="Enter max longitude"
          ariaLabel={maxLngInputAccessibleRoleName}
          ariaDescription="This is the maximum longitude input box. Enter a maximum longitude for the bounding box."
          handleSubmit={handleBoundSubmit}
          textbox={maxLngVal}
          setTextbox={setMaxLng}
        />
        <button
          className="input-button"
          aria-label={boundSubmitAccessibleRoleName}
          aria-description="This is the bounding box submit button. Enter minimum/maximum coordinates and press to filter based on the coordinates you entered."
          onClick={handleBoundSubmit}
        >
          submit
        </button>
        <button
          className="reset-button"
          aria-label={boundResetAccessibleRoleName}
          aria-description="This is the reset button. Press to reset the bounding box to the full dataset."
          onClick={handleBoundReset}
        >
          reset bounding box
        </button>
      </div>
    </div>
  );
}

export {
  numBoundResults,
  numSearchResults
};