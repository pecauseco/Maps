/**
 * This is the accessible role name of the location panel.
 */
export const locationAccessibleRoleName = "Location Data Display Panel";

/**
 * The LocationPanel function takes in three props,
 * stateData, cityData, and nameData, and displays
 * them in a panel on the screen
 */
interface LocationPanelProps {
  stateData: string;
  cityData: string;
  nameData: string;
}

/**
 * This function represents the LocationPanel component that is used to render the location panel.
 * @param props the props being passed into the location panel
 */
export default function LocationPanel(props: LocationPanelProps) {
  /**
   * The tsx/HTML of the LocationPanel component
   */
  return (
    <div className="location-container"
      role={locationAccessibleRoleName}
      aria-label="Location Data Display Panel"
      aria-description="This is the location data display panel. It displays the state, city, and name of the location clicked."
    >
      <h2>Location</h2>
      <p>State: {props.stateData}</p>
      <p>City: {props.cityData}</p>
      <p>Name: {props.nameData}</p>
    </div>
  );

} 