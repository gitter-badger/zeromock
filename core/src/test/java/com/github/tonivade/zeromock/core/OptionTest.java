/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.core;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

public class OptionTest {
  private final Handler1<String, String> toUpperCase = string -> string.toUpperCase();

  @Test
  public void mapSome() {
    Option<String> option = Option.some("Hola mundo").map(toUpperCase);
    
    assertEquals(Option.some("HOLA MUNDO"), option);
  }

  @Test
  public void mapNone() {
    Option<String> option = Option.<String>none().map(toUpperCase);
    
    assertTrue(option.isEmpty());
  }

  @Test
  public void flatMapSome() {
    Option<String> option = Option.some("Hola mundo").flatMap(toUpperCase.liftOption());
    
    assertEquals(Option.some("HOLA MUNDO"), option);
  }

  @Test
  public void flatMapNone() {
    Option<String> option = Option.<String>none().flatMap(toUpperCase.liftOption());
    
    assertTrue(option.isEmpty());
  }

  @Test
  public void orElseSome() {
    String value = Option.some("Hola mundo").orElse(() -> "Adios!");
    
    assertEquals("Hola mundo", value);
  }

  @Test
  public void orElseNone() {
    String value = Option.<String>none().orElse(() -> "Adios!");
    
    assertEquals("Adios!", value);
  }

  @Test
  public void notFilter() {
    Option<String> option = Option.some("Hola mundo").filter(string -> string.startsWith("Hola"));
    
    assertEquals(Option.some("Hola mundo"), option);
  }

  @Test
  public void filter() {
    Option<String> option = Option.some("Hola mundo").filter(string -> string.startsWith("hola"));
    
    assertTrue(option.isEmpty());
  }

  @Test
  public void filterFailure() {
    Option<String> option = Option.<String>none().filter(string -> string.startsWith("hola"));
    
    assertTrue(option.isEmpty());
  }

  @Test
  public void some() {
    Option<String> option = Option.some("Hola mundo");
   
    assertAll(() -> assertTrue(option.isPresent()),
              () -> assertFalse(option.isEmpty()),
              () -> assertEquals("Hola mundo", option.get()),
              () -> assertEquals(Optional.of("Hola mundo"), option.toOptional()),
              () -> assertEquals(Option.some("Hola mundo"), option),
              () -> assertEquals(singletonList("Hola mundo"), option.stream().collect(toList())),
              () -> {
                AtomicReference<String> ref = new AtomicReference<>();
                option.ifPresent(ref::set);
                assertEquals("Hola mundo", ref.get());
              });
  }

  @Test
  public void failure() {
    Option<String> option = Option.none();
    
    assertAll(() -> assertFalse(option.isPresent()),
              () -> assertTrue(option.isEmpty()),
              () -> assertEquals(Option.none(), option),
              () -> assertEquals(Optional.empty(), option.toOptional()),
              () -> assertEquals(emptyList(), option.stream().collect(toList())),
              () -> assertThrows(IllegalStateException.class, () -> option.get()),
              () -> {
                AtomicReference<String> ref = new AtomicReference<>();
                option.ifPresent(ref::set);
                assertNull(ref.get());
              });
  }
  
  @Test
  public void optionOfNone() {
    Option<String> option = Option.of(this::messageNull);
    
    assertTrue(option.isEmpty());
  }
  
  @Test
  public void optionOfSome() {
    Option<String> option = Option.of(this::message);
    
    assertTrue(option.isPresent());
  }
  
  private String message() {
    return "Hola mundo";
  }
  
  private String messageNull() {
    return null;
  }
}