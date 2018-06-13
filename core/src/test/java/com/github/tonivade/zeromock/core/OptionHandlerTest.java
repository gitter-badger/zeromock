/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class OptionHandlerTest {
  
  @Test
  public void mapTest() {
    OptionHandler<String, Integer> str2int = str -> Option.some(str.length());
    
    assertEquals(Option.some(10), str2int.map(a -> a * 2).handle("asdfg"));
  }
  
  @Test
  public void mapEmptyTest() {
    OptionHandler<String, Integer> str2int = str -> Option.none();
    
    assertEquals(Option.none(), str2int.map(a -> a * 2).handle("asdfg"));
  }
  
  @Test
  public void orElseTest() {
    OptionHandler<String, Integer> str2int = str -> Option.none();
    
    assertEquals(Integer.valueOf(0), str2int.orElse(0).handle("asdfg"));
  }
  
  @Test
  public void filterTest() {
    OptionHandler<String, Integer> str2int = str -> Option.some(str.length());
    
    assertEquals(Option.some(5), str2int.filter(x -> x > 0).handle("asdfg"));
  }
  
  @Test
  public void filterEmptyTest() {
    OptionHandler<String, Integer> str2int = str -> Option.some(str.length());
    
    assertEquals(Option.none(), str2int.filter(x -> x > 10).handle("asdfg"));
  }
  
  @Test
  public void flatMapTest() {
    OptionHandler<String, Integer> str2int = str -> Option.some(str.length());
    
    assertEquals(Option.some(10), str2int.flatMap(a -> Option.some(a * 2)).handle("asdfg"));
  }
  
  @Test
  public void flatMapEmptyTest() {
    OptionHandler<String, Integer> str2int = str -> Option.some(str.length());
    
    assertEquals(Option.none(), str2int.flatMap(a -> Option.none()).handle("asdfg"));
  }
}
