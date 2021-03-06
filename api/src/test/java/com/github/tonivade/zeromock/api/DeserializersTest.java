/*
 * Copyright (c) 2018-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.api;

import static com.github.tonivade.zeromock.api.Bytes.asBytes;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;

import org.junit.jupiter.api.Test;

import com.github.tonivade.purefun.Tuple;
import com.github.tonivade.purefun.data.ImmutableArray;
import com.github.tonivade.purefun.data.ImmutableList;
import com.github.tonivade.purefun.data.ImmutableMap;
import com.github.tonivade.purefun.data.ImmutableSet;
import com.github.tonivade.purefun.data.ImmutableTree;
import com.github.tonivade.purefun.data.ImmutableTreeMap;
import com.google.gson.reflect.TypeToken;

class DeserializersTest {
  
  private final Bytes array = asBytes("[\"a\",\"b\"]");
  private final Bytes map = asBytes("{\"a\":\"b\"}");

  @Test
  void testList() {
    Type type = new TypeToken<ImmutableList<String>>() { }.getType();
    
    ImmutableList<String> apply = Deserializers.<ImmutableList<String>>jsonTo(type).apply(array);
    
    assertEquals(ImmutableList.of("a", "b"), apply);
  }

  @Test
  void testArray() {
    Type type = new TypeToken<ImmutableArray<String>>() { }.getType();
    
    ImmutableArray<String> apply = Deserializers.<ImmutableArray<String>>jsonTo(type).apply(array);
    
    assertEquals(ImmutableArray.of("a", "b"), apply);
  }

  @Test
  void testSet() {
    Type type = new TypeToken<ImmutableSet<String>>() { }.getType();
    
    ImmutableSet<String> apply = Deserializers.<ImmutableSet<String>>jsonTo(type).apply(array);
    
    assertEquals(ImmutableSet.of("a", "b"), apply);
  }

  @Test
  void testTree() {
    Type type = new TypeToken<ImmutableTree<String>>() { }.getType();
    
    ImmutableTree<String> apply = Deserializers.<ImmutableTree<String>>jsonTo(type).apply(array);
    
    assertEquals(ImmutableTree.of("a", "b"), apply);
  }

  @Test
  void testMap() {
    Type type = new TypeToken<ImmutableMap<String, String>>() { }.getType();
    
    ImmutableMap<String, String> apply = Deserializers.<ImmutableMap<String, String>>jsonTo(type).apply(map);
    
    assertEquals(ImmutableMap.of(Tuple.of("a", "b")), apply);
  }

  @Test
  void testTreeMap() {
    Type type = new TypeToken<ImmutableTreeMap<String, String>>() { }.getType();
    
    ImmutableTreeMap<String, String> apply = Deserializers.<ImmutableTreeMap<String, String>>jsonTo(type).apply(map);
    
    assertEquals(ImmutableTreeMap.of(Tuple.of("a", "b")), apply);
  }
}
