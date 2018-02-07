/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import static com.github.tonivade.zeromock.IOUtils.readAll;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class MockHttpServer {
  private static final String ROOT = "/";

  private final HttpServer server;

  private final Map<String, HttpRequest> requests = new HashMap<>();
  private final Map<String, HttpService> mappings = new HashMap<>();
  
  private MockHttpServer(int port) {
    try {
      server = HttpServer.create(new InetSocketAddress(port), 0);
      server.createContext(ROOT, this::handle);
    } catch (IOException e) {
      throw new UncheckedIOException("unable to start server at port " + port, e);
    }
  }
  
  public static MockHttpServer listenAt(int port) {
    return new MockHttpServer(port);
  }

  public MockHttpServer mount(String path, HttpService service) {
    mappings.put(path, service);
    return this;
  }

  public void start() {
    server.start();
  }

  public void stop() {
    server.stop(0);
  }

  public HttpRequest getRequest(String url) {
    return requests.get(url);
  }

  private void handle(HttpExchange exchange) throws IOException {
    HttpRequest request = createRequest(exchange);
    HttpService resource = findResource(request);
    processResponse(exchange, resource.handle(request.dropOneLevel()));
  }

  private HttpService findResource(HttpRequest request) {
    return mappings.get(ROOT + request.path.getAt(0));
  }

  private HttpRequest createRequest(HttpExchange exchange) throws IOException {
    return new HttpRequest(HttpMethod.valueOf(exchange.getRequestMethod()),
                           new Path(exchange.getRequestURI().getPath()), 
                           Deserializers.plain().apply(readAll(exchange.getRequestBody())),
                           new HttpHeaders(exchange.getRequestHeaders()),
                           new HttpParams(exchange.getRequestURI().getQuery()));
  }

  private void processResponse(HttpExchange exchange, HttpResponse response) throws IOException {
    ByteBuffer bytes = getSerializer(response).apply(response.body);
    response.headers.forEach((key, value) -> exchange.getResponseHeaders().add(key, value));
    exchange.sendResponseHeaders(response.statusCode.code, bytes.remaining());
    try (OutputStream output = exchange.getResponseBody()) {
      exchange.getResponseBody().write(bytes.array());
    }
  }

  private Function<Object, ByteBuffer> getSerializer(HttpResponse response) {
    return Serializers.plain();
  }
}
