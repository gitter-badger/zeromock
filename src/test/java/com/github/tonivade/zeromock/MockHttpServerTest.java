/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import static com.github.tonivade.zeromock.Bytes.asString;
import static com.github.tonivade.zeromock.Handlers.badRequest;
import static com.github.tonivade.zeromock.Handlers.contentJson;
import static com.github.tonivade.zeromock.Handlers.contentXml;
import static com.github.tonivade.zeromock.Handlers.force;
import static com.github.tonivade.zeromock.Handlers.noContent;
import static com.github.tonivade.zeromock.Handlers.ok;
import static com.github.tonivade.zeromock.Predicates.acceptsJson;
import static com.github.tonivade.zeromock.Predicates.acceptsXml;
import static com.github.tonivade.zeromock.Predicates.get;
import static com.github.tonivade.zeromock.Predicates.param;
import static com.github.tonivade.zeromock.Predicates.path;
import static com.github.tonivade.zeromock.Serializers.json;
import static com.github.tonivade.zeromock.Serializers.plain;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.google.gson.JsonObject;

@ExtendWith(MockHttpServerExtension.class)
public class MockHttpServerTest {

  private HttpService service1 = new HttpService("hello")
      .when(get().and(path("/hello")).and(param("name")), ok(plain().compose(this::helloWorld)))
      .when(get().and(path("/hello")).and(param("name").negate()), badRequest("missing parameter name"));

  private HttpService service2 = new HttpService("test")
      .when(get().and(path("/test")).and(acceptsXml()), ok("<body/>").andThen(contentXml()))
      .when(get().and(path("/test")).and(acceptsJson()), ok(force(JsonObject::new).andThen(json())).andThen(contentJson()))
      .when(get().and(path("/empty")), noContent());
  
  @Test
  public void hello(MockHttpServer server) {
    server.mount("/path", service1.combine(service2));
    
    HttpClient client = new HttpClient("http://localhost:8080/path");
    
    HttpResponse response = client.request(Requests.get("/hello").withParam("name", "World"));

    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals("Hello World!", asString(response.body())));
  }
  
  @Test
  public void helloMissingParam(MockHttpServer server) {
    server.mount("/path", service1.combine(service2));
    
    HttpClient client = new HttpClient("http://localhost:8080/path");
    
    HttpResponse response = client.request(Requests.get("/hello"));

    assertEquals(HttpStatus.BAD_REQUEST, response.status());
  }

  @Test
  public void jsonTest(MockHttpServer server) {
    server.mount("/path", service1.combine(service2));
    
    HttpClient client = new HttpClient("http://localhost:8080/path");
    
    HttpResponse response = client.request(Requests.get("/test").withHeader("Accept", "application/json"));

    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals(new JsonObject(), Deserializers.json().apply(response.body())),
              () -> assertEquals(asList("application/json"), response.headers().get("Content-type")));
  }

  @Test
  public void xmlTest(MockHttpServer server) {
    server.mount("/path", service1.combine(service2));
    
    HttpClient client = new HttpClient("http://localhost:8080/path");
    
    HttpResponse response = client.request(Requests.get("/test").withHeader("Accept", "text/xml"));

    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals("<body/>", asString(response.body())),
              () -> assertEquals(asList("text/xml"), response.headers().get("Content-type")));
  }

  @Test
  public void noContentTest(MockHttpServer server) {
    server.mount("/path", service1.combine(service2));
    
    HttpClient client = new HttpClient("http://localhost:8080/path");
    
    HttpResponse response = client.request(Requests.get("/empty"));

    assertAll(() -> assertEquals(HttpStatus.NO_CONTENT, response.status()),
              () -> assertEquals(null, response.body()));
  }
  
  private String helloWorld(HttpRequest request) {
    return String.format("Hello %s!", request.param("name"));
  }
}
