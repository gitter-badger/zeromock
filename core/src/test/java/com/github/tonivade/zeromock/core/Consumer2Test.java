/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.core;

import static com.github.tonivade.zeromock.core.Nothing.nothing;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class Consumer2Test {

  @Test
  public void andThen() {
    List<String> strings = new LinkedList<String>();

    Consumer2<String, Integer> add = (a, b) -> strings.add(a + "=" + b);
    Consumer2<String, Integer> composite = add.andThen(add);
    composite.accept("value", 100);
    
    assertEquals(asList("value=100", "value=100"), strings);
  }

  @Test
  public void asFunction() {
    List<String> strings = new LinkedList<String>();

    Consumer2<String, Integer> add = (a, b) -> strings.add(a + "=" + b);
    Nothing nothing = add.asFunction().apply("value", 100);
    
    assertAll(() -> assertEquals(asList("value=100"), strings),
              () -> assertEquals(nothing(), nothing));
  }
}
