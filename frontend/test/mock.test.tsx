import { render, screen } from "@testing-library/react";
import "@testing-library/jest-dom";
import "@testing-library/jest-dom/extend-expect";
import {
  extractMockedSearchOverlay,
  extractMockedRedliningOverlay,
} from "../src/data-utils/filter-overlays";
import { FeatureCollection } from "geojson";
import ControlPanel, {
  keywordInputAccessibleRoleName,
  keywordSubmitAccessibleRoleName,
  keywordResetAccessibleRoleName,
  boundSubmitAccessibleRoleName,
  boundResetAccessibleRoleName,
  minLatInputAccessibleRoleName,
  minLngInputAccessibleRoleName,
  maxLatInputAccessibleRoleName,
  maxLngInputAccessibleRoleName,
  numBoundResults,
  numSearchResults,
} from "../src/components/control-panel";
import { 
  enterCommand,
  getInputByRole,
  getButtonByRole
} from "./testutils";

let minLatInput: HTMLElement;
let minLngInput: HTMLElement;
let maxLatInput: HTMLElement;
let maxLngInput: HTMLElement;
let keywordInput: HTMLElement;
let keywordSearchButton: HTMLElement;
let keywordResetButton: HTMLElement;
let coordinateSubmitButton: HTMLElement;
let coordinateResetButton: HTMLElement;

beforeEach(async () => {
  render(
    <ControlPanel 
      filterBoundOverlay={extractMockedRedliningOverlay}
      filterSearchOverlay={extractMockedSearchOverlay}
      setBoundOverlay={() => {}}
      setSearchOverlay={() => {}}
    />
  );
  minLatInput = await getInputByRole(minLatInputAccessibleRoleName);
  minLngInput = await getInputByRole(minLngInputAccessibleRoleName);
  maxLatInput = await getInputByRole(maxLatInputAccessibleRoleName);
  maxLngInput = await getInputByRole(maxLngInputAccessibleRoleName);
  keywordInput = await getInputByRole(keywordInputAccessibleRoleName);
  keywordSearchButton = await getButtonByRole(keywordSubmitAccessibleRoleName);
  keywordResetButton = await getButtonByRole(keywordResetAccessibleRoleName);
  coordinateSubmitButton = await getButtonByRole(boundSubmitAccessibleRoleName);
  coordinateResetButton = await getButtonByRole(boundResetAccessibleRoleName);
});

describe("mocked filtering with known data", () => {
  test("filtering by search term", async () => {
    // when the search term is "red mountain", divorced from the server, the function should return 5 features (5 matches)
    await enterCommand("red mountain", keywordInput);
    expect(numSearchResults).toBe(5);
    // when the search term is "lana del rey please marry me", divorced from the server, the function should return 0 features (no matches)
    await enterCommand("lana del rey please marry me", keywordInput);
    expect(numSearchResults).toBe(0);
  });

  test("filtering by redlining", async () => {
    // since the upper and lower bounds are equal, we should see 0 features returned from our mock data
    let data: FeatureCollection = await extractMockedRedliningOverlay(
      {
        minLat: "40.0",
        maxLat: "40.0",
        minLng: "-105.0",
        maxLng: "-105.0",
      }
    );
    expect(data.features.length).toBe(0);
    });
});