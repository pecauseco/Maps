package edu.brown.cs.student.sprint5.server.datasources;

import com.squareup.moshi.Moshi;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Class that makes an API call to a given URL and returns the response as an object of the given
 * class.
 */
public class WebAPICall implements APICall {

  /**
   * Makes a GET request to the given URL and returns the response as an object of the given class.
   *
   * @param url the URL to make the request to
   * @param responseClass the class of the object to return
   * @return the response as an object of the given class
   * @param <R> the type of the object to return
   * @throws InterruptedException if the request is interrupted
   * @throws IOException if there is an error reading the request or response
   */
  public <R> R makeRequest(String url, Class<R> responseClass)
      throws InterruptedException, IOException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    Moshi moshi = new Moshi.Builder().build();
    return moshi.adapter(responseClass).fromJson(response.body());
  }
}
