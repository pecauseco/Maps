import "@testing-library/jest-dom";
import "@testing-library/jest-dom/extend-expect";
import userEvent from "@testing-library/user-event";
import { screen } from "@testing-library/react";

/**
 * This function is used to get the most recently added input element with a given role.
 * @param role: string the role of the input element to get.
 * @returns: Promise<HTMLElement> the most recently added input element with the given role.
 */
export async function getInputByRole(role: string): Promise<HTMLElement> {
  let el: HTMLElement = screen.getByRole("textbox", {
    name: role,
  });
  return new Promise<HTMLElement>((resolve) => {
    resolve(el);
  });
}

/**
 * This function is used to get the most recently added button element with a given role.
 * @param role: string the role of the input element to get.
 * @returns: Promise<HTMLElement> the most recently added button element with the given role.
 */
export async function getButtonByRole(role: string): Promise<HTMLElement> {
  let el: HTMLElement = screen.getByRole("button", {
    name: role,
  });
  return new Promise<HTMLElement>((resolve) => {
    resolve(el);
  });
}

/**
 * This function is used to enter commands into the REPL input, simulating a user typing and pressing the "Enter" key.
 * @param command: string - the command to enter into the REPL input.
 * @param box: HTMLElement - the REPL input box.
 */
export async function enterCommand(command: string, box: HTMLElement) {
  await userEvent.type(box, command);
  await userEvent.type(box, "{enter}");
}

/**
 * This function is used to enter commands into the REPL input, simulating a user typing and pressing the Button.
 * @param command: string - the command to enter into the REPL input.
 * @param box: HTMLElement - the REPL input box.
 */
export async function enterCommandButton(
  command: string,
  box: HTMLElement,
  button: HTMLElement
) {
  await userEvent.type(box, command);
  await userEvent.click(button);
}

/**
 * This function is used to get the most recent commands from the REPL history, from most recent to least recent.
 * @param role: string - the role of the command to get.
 * @returns: Promise<HTMLElement> - the most recently output HTMLElement object with the given role.
 */
export async function getMostRecentWithRole(
  role: string
): Promise<HTMLElement> {
  let all: HTMLElement[] = await getAllWithRole(role);
  return new Promise<HTMLElement>((resolve) => {
    resolve(all[0]);
  });
}

/**
 * This function is used to get all commands from the REPL history, from most recent to least recent.
 * @param role: string - the role of the command to get.
 * @returns: Promise<HTMLElement[]> - an array of HTMLParagraphElement objects with the given role.
 */
export async function getAllWithRole(role: string): Promise<HTMLElement[]> {
  let all: HTMLElement[] = await screen.findAllByRole(role);
  all = all.reverse();
  return new Promise<HTMLElement[]>((resolve) => {
    resolve(all);
  });
}