package edu.brown.cs.student.sprint5.server.main;

import static edu.brown.cs.student.sprint5.Constants.*;
import static spark.Spark.after;

import edu.brown.cs.student.sprint5.server.handlers.JSONDataHandler;
import edu.brown.cs.student.sprint5.server.proxies.BoundingBoxProxy;
import edu.brown.cs.student.sprint5.server.proxies.KeywordSearchProxy;
import edu.brown.cs.student.sprint5.server.responseformatting.FeatureCollectionFormat.FeatureCollection;
import spark.Spark;

/**
 * Top-level class for our Server. It serves as a composer class to inject dependencies into
 * different components of our server, particularly proxy types and data types into generic JSON
 * data handlers.
 */
public class Server {
  public static void main(String[] args) {
    Spark.port(3232);
    /* Sets up CORS headers for responses

       More info on CORS:
           - https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS
           - https://portswigger.net/web-security/cors
    */
    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    try {
      Spark.get(
          "/" + BOUNDING_BOX_ENDPOINT,
          new JSONDataHandler(REDLINING_PATH, FeatureCollection.class, BoundingBoxProxy.class));
      Spark.get(
          "/" + KEYWORD_ENDPOINT,
          new JSONDataHandler(REDLINING_PATH, FeatureCollection.class, KeywordSearchProxy.class));
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Failed to initialize server");
    }

    // initiate server
    Spark.init();
    Spark.awaitInitialization();
  }
}
