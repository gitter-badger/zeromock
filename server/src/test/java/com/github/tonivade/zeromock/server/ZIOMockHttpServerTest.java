/*
 * Copyright (c) 2018-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.server;

import static com.github.tonivade.purefun.Nothing.nothing;
import static com.github.tonivade.zeromock.api.Bytes.asString;
import static com.github.tonivade.zeromock.api.Headers.contentJson;
import static com.github.tonivade.zeromock.api.Headers.contentXml;
import static com.github.tonivade.zeromock.api.Matchers.acceptsJson;
import static com.github.tonivade.zeromock.api.Matchers.acceptsXml;
import static com.github.tonivade.zeromock.api.Matchers.get;
import static com.github.tonivade.zeromock.api.Matchers.param;
import static com.github.tonivade.zeromock.api.Matchers.path;
import static com.github.tonivade.zeromock.api.Responses.badRequest;
import static com.github.tonivade.zeromock.api.Responses.noContent;
import static com.github.tonivade.zeromock.api.Responses.ok;
import static com.github.tonivade.zeromock.api.Serializers.objectToJson;
import static com.github.tonivade.zeromock.api.Serializers.objectToXml;
import static com.github.tonivade.zeromock.client.HttpClient.connectTo;
import static com.github.tonivade.zeromock.server.ZIOMockHttpServer.listenAt;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.tonivade.purefun.Nothing;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.effect.ZIO;
import com.github.tonivade.zeromock.api.Deserializers;
import com.github.tonivade.zeromock.api.HttpRequest;
import com.github.tonivade.zeromock.api.HttpResponse;
import com.github.tonivade.zeromock.api.HttpStatus;
import com.github.tonivade.zeromock.api.HttpZIOService;
import com.github.tonivade.zeromock.api.Requests;
import com.github.tonivade.zeromock.api.Responses;
import com.github.tonivade.zeromock.api.ZIORequestHandler;

public class ZIOMockHttpServerTest {

  private static final String BASE_URL = "http://localhost:%s/path";

  private HttpZIOService<Nothing> service1 = new HttpZIOService<Nothing>("hello")
      .when(get().and(path("/hello")).and(param("name")))
        .then(request -> ZIO.<Nothing, String>task(() -> helloWorld(request)).fold(Responses::error, Responses::ok))
      .when(get().and(path("/hello")).and(param("name").negate()))
        .then(request -> ZIO.pure(badRequest("missing parameter name")));

  private HttpZIOService<Nothing> service2 = new HttpZIOService<Nothing>("test")
      .when(get().and(path("/test")).and(acceptsXml()))
        .then(request -> ZIO.<Nothing, Say>task(this::sayHello).map(objectToXml()).fold(Responses::error, Responses::ok).map(contentXml()))
      .when(get().and(path("/test")).and(acceptsJson()))
        .then(request -> ZIO.<Nothing, Say>task(this::sayHello).map(objectToJson()).fold(Responses::error, Responses::ok).map(contentJson()))
      .when(get().and(path("/empty")))
        .then(request -> ZIO.pure(noContent()));

  private HttpZIOService<Nothing> service3 = new HttpZIOService<Nothing>("other")
      .when(get("/ping")).then(request -> ZIO.pure(ok("pong")));

  private static ZIOMockHttpServer<Nothing> server = listenAt(nothing(), 0);

  @Test
  public void hello() {
    server.mount("/path", service1.combine(service2));

    HttpResponse response = connectTo(baseUrl()).request(Requests.get("/hello").withParam("name", "World"));

    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals("Hello World!", asString(response.body())));
  }

  @Test
  public void helloMissingParam() {
    server.mount("/path", service1.combine(service2));

    HttpResponse response = connectTo(baseUrl()).request(Requests.get("/hello"));

    assertEquals(HttpStatus.BAD_REQUEST, response.status());
  }

  @Test
  public void jsonTest() {
    server.mount("/path", service1.combine(service2));

    HttpResponse response = connectTo(baseUrl()).request(Requests.get("/test").withHeader("Accept", "application/json"));

    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals(sayHello(), Deserializers.jsonToObject(Say.class).apply(response.body())),
              () -> assertEquals(ImmutableSet.of("application/json"), response.headers().get("Content-type")));
  }

  @Test
  public void xmlTest() {
    server.mount("/path", service1.combine(service2));

    HttpResponse response = connectTo(baseUrl()).request(Requests.get("/test").withHeader("Accept", "text/xml"));

    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals(sayHello(), Deserializers.xmlToObject(Say.class).apply(response.body())),
              () -> assertEquals(ImmutableSet.of("text/xml"), response.headers().get("Content-type")));
  }

  @Test
  public void noContentTest() {
    server.mount("/path", service1.combine(service2));

    HttpResponse response = connectTo(baseUrl()).request(Requests.get("/empty"));

    assertAll(() -> assertEquals(HttpStatus.NO_CONTENT, response.status()),
              () -> assertEquals("", asString(response.body())));
  }

  @Test
  public void ping() {
    server.mount("/path", service3);

    HttpResponse response = connectTo(baseUrl()).request(Requests.get("/ping"));

    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals("pong", asString(response.body())));
  }

  @Test
  public void exec() {
    ZIORequestHandler<Nothing> echo = request -> ZIO.pure(ok(request.body()));
    ZIOMockHttpServer<Nothing> server = listenAt(nothing(), 0).exec(echo).start();

    HttpResponse response = connectTo("http://localhost:" + server.getPort()).request(Requests.get("/").withBody("echo"));

    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals("echo", asString(response.body())));

    server.stop();
  }

  @BeforeEach
  public void beforeEach() {
    server.reset();
  }

  @BeforeAll
  public static void beforeAll() {
    server.start();
  }

  @AfterAll
  public static void afterAll() {
    server.stop();
  }

  private String helloWorld(HttpRequest request) {
    return String.format("Hello %s!", request.param("name"));
  }

  private Say sayHello() {
    return new Say("hello");
  }

  private String baseUrl() {
    return String.format(BASE_URL, server.getPort());
  }
}
