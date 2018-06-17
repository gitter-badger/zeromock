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

public class Consumer1Test {

  @Test
  public void andThen() {
    List<String> strings = new LinkedList<String>();

    Consumer1<String> add = Consumer1.of(strings::add);
    Consumer1<String> composite = add.andThen(add);
    composite.accept("value");
    
    assertEquals(asList("value", "value"), strings);
  }
  
  @Test
  public void peek() {
    List<String> strings = new LinkedList<String>();

    Consumer1<String> add = Consumer1.of(strings::add);

    String string = add.bypass().apply("value");

    assertAll(() -> assertEquals(asList("value"), strings),
              () -> assertEquals("value", string));
  }
  
  @Test
  public void asFunction() {
    List<String> strings = new LinkedList<String>();

    Consumer1<String> add = Consumer1.of(strings::add);

    Nothing nothing = add.asFunction().apply("value");

    assertAll(() -> assertEquals(asList("value"), strings),
              () -> assertEquals(nothing(), nothing));
  }
}
