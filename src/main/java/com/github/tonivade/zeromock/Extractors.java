/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import java.nio.ByteBuffer;
import java.util.function.Function;

public final class Extractors {
  
  private Extractors() {}

  public static Function<HttpRequest, ByteBuffer> body() {
    return request -> request.body();
  }

  public static Function<HttpRequest, String> queryParam(String name) {
    return request -> request.param(name);
  }

  public static Function<HttpRequest, String> pathParam(int position) {
    return request -> request.pathParam(position);
  }
  
  public static Function<ByteBuffer, String> asString() {
    return Bytes::asString;
  }
  
  public static Function<String, Integer> asInteger() {
    return string -> Integer.parseInt(string);
  }
}
