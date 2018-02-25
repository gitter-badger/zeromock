/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class HttpParamsTest {
  @Test
  public void isEmpty() {
    HttpParams params = HttpParams.empty();
    
    assertAll("should be empty and should not contains any key",
              () -> assertTrue(params.isEmpty()),
              () -> assertFalse(params.contains("key")),
              () -> assertEquals(Optional.empty(), params.get("key")),
              () -> assertEquals("", params.toQueryString()));
  }

  @Test
  public void notEmpty() {
    HttpParams params = HttpParams.empty().withParam("key", "value");
    
    assertAll("should not be empty and should contains a key",
              () -> assertFalse(params.isEmpty()),
              () -> assertTrue(params.contains("key")),
              () -> assertEquals(Optional.of("value"), params.get("key")),
              () -> assertEquals("?key=value", params.toQueryString()));
  }

  @Test
  public void queryString() {
    HttpParams params = HttpParams.empty().withParam("key1", "value1").withParam("key2", "value2");
    
    assertEquals("?key1=value1&key2=value2", params.toQueryString());
  }
  
  @Test
  public void equalsVerifier() {
    EqualsVerifier.forClass(HttpParams.class).verify();
  }
}
