/*
 * Copyright (c) 2018-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.junit5;

import java.util.Objects;

import com.github.tonivade.purefun.Equal;

public final class Say {
  private String message;

  public Say(String message) {
    this.message = message;
  }

  @Override
  public int hashCode() {
    return Objects.hash(message);
  }

  @Override
  public boolean equals(Object obj) {
    return Equal.<Say>of()
        .comparing(s -> s.message)
        .applyTo(this, obj);
  }

  @Override
  public String toString() {
    return "Say(message=" + message + ")";
  }
}
