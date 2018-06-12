/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.core;

import static com.github.tonivade.zeromock.core.Equal.comparing;
import static com.github.tonivade.zeromock.core.Equal.equal;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

public final class Tuple5<A, B, C, D, E> {

  private final A value1;
  private final B value2;
  private final C value3;
  private final D value4;
  private final E value5;

  private Tuple5(A value1, B value2, C value3, D value4, E value5) {
    this.value1 = requireNonNull(value1);
    this.value2 = requireNonNull(value2);
    this.value3 = requireNonNull(value3);
    this.value4 = requireNonNull(value4);
    this.value5 = requireNonNull(value5);
  }

  public A get1() {
    return value1;
  }

  public B get2() {
    return value2;
  }
  
  public C get3() {
    return value3;
  }
  
  public D get4() {
    return value4;
  }
  
  public E get5() {
    return value5;
  }
  
  public <R> Tuple5<R, B, C, D, E> map1(Handler1<A, R> mapper) {
    return Tuple5.of(mapper.handle(value1), value2, value3, value4, value5);
  }
  
  public <R> Tuple5<A, R, C, D, E> map2(Handler1<B, R> mapper) {
    return Tuple5.of(value1, mapper.handle(value2), value3, value4, value5);
  }
  
  public <R> Tuple5<A, B, R, D, E> map3(Handler1<C, R> mapper) {
    return Tuple5.of(value1, value2, mapper.handle(value3), value4, value5);
  }
  
  public <R> Tuple5<A, B, C, R, E> map4(Handler1<D, R> mapper) {
    return Tuple5.of(value1, value2, value3, mapper.handle(value4), value5);
  }
  
  public <R> Tuple5<A, B, C, D, R> map5(Handler1<E, R> mapper) {
    return Tuple5.of(value1, value2, value3, value4, mapper.handle(value5));
  }
  
  public <F, G, H, I, J> Tuple5<F, G, H, I, J> map(Handler1<A, F> map1, 
                                                   Handler1<B, G> map2, 
                                                   Handler1<C, H> map3, 
                                                   Handler1<D, I> map4, 
                                                   Handler1<E, J> map5) {
    return Tuple5.of(map1.handle(value1), map2.handle(value2), map3.handle(value3), map4.handle(value4), map5.handle(value5));
  }

  public static <A, B, C, D, E> Tuple5<A, B, C, D, E> of(A value1, B value2, C value3, D value4, E value5) {
    return new Tuple5<>(value1, value2, value3, value4, value5);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value1, value2, value3, value4, value5);
  }

  @Override
  public boolean equals(Object obj) {
    return equal(this)
        .append(comparing(Tuple5::get1))
        .append(comparing(Tuple5::get2))
        .append(comparing(Tuple5::get3))
        .append(comparing(Tuple5::get4))
        .append(comparing(Tuple5::get5))
        .applyTo(obj);
  }

  @Override
  public String toString() {
    return "Tuple5(" + value1 + ", " + value2 + ", " + value3 + ", " + value4 + ", " +  value5 + ")";
  }
}
