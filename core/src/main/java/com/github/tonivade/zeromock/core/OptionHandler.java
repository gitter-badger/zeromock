/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.core;

import static com.github.tonivade.zeromock.core.Producer.unit;

@FunctionalInterface
public interface OptionHandler<T, R> extends Function1<T, Option<R>> {
  
  default <V> OptionHandler<T, V> map(Function1<R, V> handler) {
    return value -> apply(value).map(handler::apply);
  }
  
  default <V> OptionHandler<T, V> flatMap(OptionHandler<R, V> handler) {
    return value -> apply(value).flatMap(handler::apply);
  }
  
  default OptionHandler<T, R> filter(Matcher<R> matcher) {
    return value -> apply(value).filter(matcher);
  }
  
  default Function1<T, R> orElse(R value) {
    return orElse(unit(value));
  }
  
  default Function1<T, R> orElse(Producer<R> handler) {
    return value -> apply(value).orElse(handler);
  }
}
