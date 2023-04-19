/**
 * This interface represents the Input box props being passed into
 * the input box.
 */
interface InputBoxProps {
  ariaLabel: string;
  ariaDescription: string;
  defaultText: string;
  handleSubmit: Function;
  textbox: string;
  setTextbox: (data: string) => void;
}

/**
 * This function is the InputBox function that takes in the props and handles
 * the logic for commands
 */
export default function InputBox(props: InputBoxProps) {
  /**
   * The HTML and Javascript that are the input box
   */
  return (
    <input
      aria-label={props.ariaLabel}
      aria-description={props.ariaDescription}
      type="text"
      className="input-box"
      onChange={(e) => props.setTextbox(e.target.value)}
      placeholder={props.defaultText}
      value={props.textbox}
      onKeyUp={(e) => {
        if (e.key == "Enter") {
          props.handleSubmit();
        }
      }}
    />
  );
}
