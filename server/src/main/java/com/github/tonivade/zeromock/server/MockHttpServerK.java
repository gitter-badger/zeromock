/*
 * Copyright (c) 2018-2019, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.server;

import static com.github.tonivade.zeromock.api.Bytes.asBytes;
import static com.github.tonivade.zeromock.api.Responses.error;
import static com.github.tonivade.zeromock.api.Responses.notFound;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.github.tonivade.purefun.Function1;
import com.github.tonivade.purefun.Higher1;
import com.github.tonivade.purefun.Kind;
import com.github.tonivade.purefun.Matcher1;
import com.github.tonivade.purefun.concurrent.Promise;
import com.github.tonivade.purefun.type.Option;
import com.github.tonivade.zeromock.api.Bytes;
import com.github.tonivade.zeromock.api.HttpHeaders;
import com.github.tonivade.zeromock.api.HttpMethod;
import com.github.tonivade.zeromock.api.HttpParams;
import com.github.tonivade.zeromock.api.HttpPath;
import com.github.tonivade.zeromock.api.HttpRequest;
import com.github.tonivade.zeromock.api.HttpResponse;
import com.github.tonivade.zeromock.api.HttpServiceK;
import com.github.tonivade.zeromock.api.HttpServiceK.MappingBuilderK;
import com.github.tonivade.zeromock.api.RequestHandlerK;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public abstract class MockHttpServerK<F extends Kind> implements com.github.tonivade.zeromock.server.HttpServer {

  private static final Logger LOG = Logger.getLogger(MockHttpServerK.class.getName());

  private static final String ROOT = "/";

  private final HttpServer server;

  private final List<HttpRequest> matched = new LinkedList<>();
  private final List<HttpRequest> unmatched = new LinkedList<>();

  private HttpServiceK<F> service;

  private MockHttpServerK(String host, int port, int threads, int backlog) {
    try {
      service = new HttpServiceK<>("root");
      server = HttpServer.create(new InetSocketAddress(host, port), backlog);
      server.setExecutor(Executors.newFixedThreadPool(threads));
      server.createContext(ROOT, this::handle);
    } catch (IOException e) {
      throw new UncheckedIOException("unable to start server at " + host + ":" + port, e);
    }
  }

  public MockHttpServerK<F> mount(String path, HttpServiceK<F> other) {
    service = service.mount(path, other);
    return this;
  }

  public MockHttpServerK<F> exec(RequestHandlerK<F> handler) {
    service = service.exec(handler);
    return this;
  }

  public MockHttpServerK<F> add(Matcher1<HttpRequest> matcher, RequestHandlerK<F> handler) {
    service = service.add(matcher, handler);
    return this;
  }

  public MappingBuilderK<F, RequestHandlerK<F>, MockHttpServerK<F>> when(Matcher1<HttpRequest> matcher) {
    return new MappingBuilderK<>(this::add).when(matcher);
  }

  @Override
  public MockHttpServerK<F> start() {
    server.start();
    LOG.info(() -> "server listening at " + server.getAddress());
    return this;
  }

  @Override
  public void stop() {
    server.stop(0);
    LOG.info(() -> "server stopped");
  }

  @Override
  public MockHttpServerK<F> verify(Matcher1<HttpRequest> matcher) {
    if (!matches(matcher)) {
      throw new AssertionError("request not found");
    }
    return this;
  }

  @Override
  public List<HttpRequest> getUnmatched() {
    return unmodifiableList(unmatched);
  }

  @Override
  public void reset() {
    service = new HttpServiceK<>("root");
    matched.clear();
    unmatched.clear();
  }

  protected abstract Promise<HttpResponse> run(Higher1<F, HttpResponse> response);

  private void handle(HttpExchange exchange) throws IOException {
    HttpRequest request = createRequest(exchange);
    execute(request)
      .ifPresent(responseK -> matched(exchange, request, responseK))
      .ifEmpty(() -> unmatched(exchange, request));
  }

  private void unmatched(HttpExchange exchange, HttpRequest request) {
    LOG.fine(() -> "unmatched request " + request);
    processResponse(exchange, notFound());
    unmatched.add(request);
  }

  private void matched(HttpExchange exchange, HttpRequest request, Higher1<F, HttpResponse> responseK) {
    matched.add(request);
    run(responseK)
      .onSuccess(response -> processResponse(exchange, response))
      .onFailure(error -> processResponse(exchange, error(error)));
  }

  private boolean matches(Matcher1<HttpRequest> matcher) {
    return matched.stream().anyMatch(matcher::match);
  }

  private Option<Higher1<F, HttpResponse>> execute(HttpRequest request) {
    return service.execute(request);
  }

  private HttpRequest createRequest(HttpExchange exchange) throws IOException {
    HttpMethod method = HttpMethod.valueOf(exchange.getRequestMethod());
    HttpHeaders headers = HttpHeaders.from(exchange.getRequestHeaders());
    HttpParams params = new HttpParams(exchange.getRequestURI().getQuery());
    HttpPath path = HttpPath.from(exchange.getRequestURI().getPath());
    Bytes body = asBytes(exchange.getRequestBody());
    return new HttpRequest(method, path, body, headers, params);
  }

  private void processResponse(HttpExchange exchange, HttpResponse response) {
    try {
      Bytes bytes = response.body();
      response.headers().forEach((key, value) -> exchange.getResponseHeaders().add(key, value));
      exchange.sendResponseHeaders(response.status().code(), bytes.size());
      try (OutputStream output = exchange.getResponseBody()) {
        exchange.getResponseBody().write(bytes.toArray());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      exchange.close();
    }
  }

  public static final class Builder<F extends Kind> {
    private String host = "localhost";
    private int port = 8080;
    private int threads = Runtime.getRuntime().availableProcessors();
    private int backlog = 0;
    private final Function1<Higher1<F, HttpResponse>, Promise<HttpResponse>> run;

    public Builder(Function1<Higher1<F, HttpResponse>, Promise<HttpResponse>> run) {
      this.run = requireNonNull(run);
    }

    public Builder<F> host(String host) {
      this.host = requireNonNull(host);
      return this;
    }

    public Builder<F> port(int port) {
      this.port = port;
      return this;
    }

    public Builder<F> threads(int threads) {
      this.threads = threads;
      return this;
    }

    public Builder<F> backlog(int backlog) {
      this.backlog = backlog;
      return this;
    }

    public MockHttpServerK<F> build() {
      return new MockHttpServerK<F>(host, port, threads, backlog) {

        @Override
        protected Promise<HttpResponse> run(Higher1<F, HttpResponse> response) {
          return run.apply(response);
        }
      };
    }
  }
}

