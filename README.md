Maps by Luke Briody and Charlie Levy


Welcome to our map program! This program utilizes a backend server that returns information regarding redlining data and sends it to the frontend where it is then displayed on a map, giving each redlining grade a different color. 


Structure:

Backend:
Our backend portion contains a server that creates a JSONDataHandler to handle calls to create bounding boxes or execute keyword searches and ensure that the calls and data do not cause any errors before they are passed on (The JSONDataHandler implements the RequestHandler type).
The JSONDataHandler is able to generically handle requests made to query arbitrary JSON files, and specific behavior is
dictated through the injected proxy class and record class. Their respective proxies are passed in to the JSONDataHandlers, and
these decide whether the data is retrieved from the cache, or is created utilizing either the BoundingBoxFilter or the DescriptionKeywordFilter classes (Both implement the FeatureFilter interface. If a developer wanted to filter data in some other way, they could do so implementing this interface, then creating a proxy that processes some set of parameters to determine the filtering criteria). Moreover, there is a generic ServerResponse class that represents how the server formats responses. Depending on how it is constructed, it is able to
automatically differentiate between being an error or success response without a need for additional classes. Finally, to
allow for easy parsing of the GeoJSON data, there is a FeatureCollectionFormat class that handles structuring the corresponding JSON information and its corresponding fields.

Frontend:
Our frontend portion of the project contains an App class that places all of the components in their respective places. First we have the control-panel component which takes care of putting all of the input-boxes and buttons into place. Here, each input box for minimum and maximum latitudes and longitudes are set up as well as for the keyword search. Additionally, submit and reset buttons are coupled with their respective input boxes. We also have a location-panel component which takes care of displaying the city, state, and area information about a location that a user has clicked on. Of course, we also have a map-panel component that handles user interactions with the map itself, such as
clicks, drags, pans, etc. Finally, our non-tsx files are our data utils, which includes a ServerResponse type that represents a basic server response and a filter-overlays utility file that contains functions for both real and mocked backend filtering.


Accessibility:

We strive to make our program as accessible as possible, and therefore changed the color sof the different redlining portions of the map to ensure that the distinctions between each type were obvious. Furthermore, aria labels/roles/descriptions were added to every possible component to ensure that anyone utilizing our program could use this program as easily as possible. We additionally chose colors that contrast with each other, and overall ensured that our program is as accessible as possible. We used large font where feasible while maintaining an
application that did not require scrolling. All input features can be navigated using the tab key, and all input fields
can be submitted using the "Enter" key or through button clicks, whichever is most accessible or preferable to the user.

To run:

Program and Server:

First, clone this repository onto the device you would like to use to run the program. This device needs to have Java and npm installed to run our program.

../sprint-5-clevy9-lbriody/backend/src/main/java/edu/brown/cs/student/sprint5/server/main/Server.java. This will open a server, which is neccessary for the REPL program’s backend functionality.

Then, index into ../sprint-5-gclevy9-lbriody/frontend directory in the terminal. Run npm install, then run npm start. This will launch the REPL application at http://localhost:5173/. In that application, the user can input various commands into the input box, and then press the submit button or the enter key to run the command. The results will be displayed in the history box on the page. Below are the built in commands that can be run:

Tests:
Backend:
Type "mvn build" and then "mvn run" to run the tests in the backend

Frontend:
Type "npm tests" in the frontend directory to run the front end tests


12+ different tools we used to complete this project:

For this project we used many different tools to help us achieve this finished product.These included React, Mapbox, Moshi, Java Spark, GeoJSON, Maven, npm, vite, rtl, typescript, html, java, css, Guava cache, GitHub. We are thankful for these tools as they allow us to utilize pre made building blocks to create something fairly complicated in a much faster and more efficient manner. They allow us to be more creative with our solutions as achieving and creating more complex solutions becomes much more feasible on the timescale we had for this project. The labor we rely on is of the thousands of developers before us that have developed computers from machines with basic functions, to those with infinite possibilites. For example, using mapbox has been a huge help as it would be incredibly difficult to add a map to a program without this pre-made tool. Additionally, things like GitHub and the IDEs make it so much easier to work with a partner in a very efficient way. Having the react library that allows me to input things like buttons and input boxes additionally allow us to create programs with components that people have writted long ago. This kind of labor has created a long evolution of coding that now allows us to utilize past work in a simple way to create something complex. Additionally, it is important to recognize not just the developers but also every person who's labor goes into technology as this industry covers a wide variety of fields.