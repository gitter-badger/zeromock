/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import static com.github.tonivade.zeromock.Responses.notFound;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public class HttpService {
  final String name;
  final Map<Predicate<HttpRequest>, Function<HttpRequest, HttpResponse>> mappings;
  
  public HttpService(String name) {
    this(name, new HashMap<>());
  }
  
  private HttpService(String name, Map<Predicate<HttpRequest>, Function<HttpRequest, HttpResponse>> mappings) {
    this.name = name;
    this.mappings = mappings;
  }
  
  public HttpService when(Predicate<HttpRequest> matcher, Function<HttpRequest, HttpResponse> handler) {
    mappings.put(matcher, handler);
    return this;
  }
  
  public HttpResponse handle(HttpRequest request) {
    return findHandler(request).apply(request);
  }
  
  public HttpService combine(HttpService other) {
    Map<Predicate<HttpRequest>, Function<HttpRequest, HttpResponse>> merge = new HashMap<>();
    merge.putAll(this.mappings);
    merge.putAll(other.mappings);
    return new HttpService(this.name + "+" + other.name, merge);
  }

  private Function<HttpRequest, HttpResponse> findHandler(HttpRequest request) {
    return mappings.entrySet().stream()
        .filter(entry -> entry.getKey().test(request))
        .map(Map.Entry::getValue)
        .findFirst()
        .orElse(notFound("not found"));
  }
  
  @Override
  public String toString() {
    return "HttpService(" + name + ")";
  }
}
