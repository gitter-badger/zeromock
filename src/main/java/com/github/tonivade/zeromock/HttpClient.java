/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import static com.github.tonivade.zeromock.IOUtils.readAll;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class HttpClient {
  
  private final String baseUrl;
  
  public HttpClient(String baseUrl) {
    this.baseUrl = baseUrl;
  }
  
  public HttpResponse request(HttpRequest request) {
    try {
      URL url = new URL(baseUrl + request.toUrl());
      HttpURLConnection con = (HttpURLConnection) url.openConnection();
      con.setRequestMethod(request.method.name());
      request.headers.forEach((key, value) -> con.setRequestProperty(key, value));
      if (request.body != null) {
        con.setDoOutput(true);
        try (OutputStream output = con.getOutputStream()) {
          output.write(Serializers.plain().apply(request.body));
        }
      }

      con.connect();
      
      int responseCode = con.getResponseCode();
      Map<String, List<String>> headers = con.getHeaderFields();
      String body = null;
      if (responseCode < 400) {
        body = readAll(con.getInputStream());
      }
      return new HttpResponse(HttpStatus.fromCode(responseCode), body, new HttpHeaders(headers));
    } catch (IOException e) {
      throw new UncheckedIOException("request error: " + request, e);
    }
  }
}
