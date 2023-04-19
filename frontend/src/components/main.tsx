import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import "../styles/index.css";
import {
  extractRedliningOverlay,
  extractSearchOverlay,
} from "../data-utils/filter-overlays";

/**
 * This function represents the main function of the application. It renders
 * the App component to the root element of the HTML and enables strict mode.
 */
ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <App/>
  </React.StrictMode>
);
