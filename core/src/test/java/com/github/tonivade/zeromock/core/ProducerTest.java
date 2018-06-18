/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.core;

import static com.github.tonivade.zeromock.core.Nothing.nothing;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ProducerTest {

  @Test
  public void andThen() {
    Producer<String> producer = Producer.of(() -> "hello world");
    
    Producer<String> andThen = producer.andThen(String::toUpperCase);
    
    assertEquals("HELLO WORLD", andThen.get());
  }
  
  @Test
  public void unit() {
    assertEquals("hello world", Producer.unit("hello world").get());
  }

  @Test
  public void asFunction() {
    Producer<String> producer = Producer.unit("hello world");

    assertEquals("hello world", producer.asFunction().apply(nothing()));
  }
}
