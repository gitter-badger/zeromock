/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.junit4;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Predicate;

import org.junit.rules.ExternalResource;

import com.github.tonivade.zeromock.core.HttpRequest;
import com.github.tonivade.zeromock.core.HttpResponse;
import com.github.tonivade.zeromock.core.HttpService;
import com.github.tonivade.zeromock.server.MockHttpServer;

public class MockHttpServerRule extends ExternalResource {
  private final MockHttpServer server;

  public MockHttpServerRule(int port) {
    this.server = MockHttpServer.listenAt(port);
  }

  @Override
  protected void before() throws Throwable {
    server.start();
  }

  @Override
  protected void after() {
    server.stop();
  }

  public MockHttpServerRule verify(Predicate<HttpRequest> matcher) {
    server.verify(matcher);
    return this;
  }

  public MockHttpServerRule when(Predicate<HttpRequest> matcher, 
                                 Function<HttpRequest, HttpResponse> handler) {
    server.when(matcher, handler);
    return this;
  }

  public MappingBuilder when(Predicate<HttpRequest> matcher) {
    return new MappingBuilder(this).when(matcher);
  }

  public MockHttpServerRule mount(String path, HttpService service) {
    server.mount(path, service);
    return this;
  }

  public static final class MappingBuilder {
    private final MockHttpServerRule server;
    private Predicate<HttpRequest> matcher;
    
    public MappingBuilder(MockHttpServerRule server) {
      this.server = requireNonNull(server);
    }

    public MappingBuilder when(Predicate<HttpRequest> matcher) {
      this.matcher = matcher;
      return this;
    }

    public MockHttpServerRule then(Function<HttpRequest, HttpResponse> handler) {
      return server.when(matcher, handler);
    }
  }
}
