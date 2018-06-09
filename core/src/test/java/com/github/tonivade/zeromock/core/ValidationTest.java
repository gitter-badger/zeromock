/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.core;

import static com.github.tonivade.zeromock.core.Handler1.identity;
import static com.github.tonivade.zeromock.core.Option.none;
import static com.github.tonivade.zeromock.core.Option.some;
import static com.github.tonivade.zeromock.core.Validation.invalid;
import static com.github.tonivade.zeromock.core.Validation.map2;
import static com.github.tonivade.zeromock.core.Validation.map3;
import static com.github.tonivade.zeromock.core.Validation.map4;
import static com.github.tonivade.zeromock.core.Validation.map5;
import static com.github.tonivade.zeromock.core.Validation.valid;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

public class ValidationTest {

  private final Handler2<Integer, Integer, Integer> sum2 = (a, b) -> a + b;
  private final Handler1<Integer, Handler1<Integer, Handler1<Integer, Integer>>> sum3 = 
      a -> b -> c -> a + b + c;
  private final Handler1<Integer, Handler1<Integer, Handler1<Integer, Handler1<Integer, Integer>>>> sum4 = 
      a -> b -> c -> d -> a + b + c + d;
  private final Handler1<Integer, Handler1<Integer, Handler1<Integer, Handler1<Integer, Handler1<Integer, Integer>>>>> sum5 = 
      a -> b -> c -> d -> e -> a + b + c + d + e;

  @Test
  public void validTest() {
    Validation<String, Integer> valid =  valid(1);
    
    assertAll(() -> assertTrue(valid.isValid()),
              () -> assertFalse(valid.isInvalid()),
              () -> assertEquals(Integer.valueOf(1), valid.get()),
              () -> assertThrows(NoSuchElementException.class, () -> valid.getError()),
              () -> assertEquals(valid(3), valid.map(i -> i + 2)),
              () -> assertEquals(valid(1), valid.mapError(String::toUpperCase)),
              () -> assertEquals(valid("1"), valid.flatMap(i -> valid(valueOf(i)))),
              () -> assertEquals("1", valid.fold(i -> valueOf(i), identity())),
              () -> assertEquals(some(valid(1)), valid.filter(i -> i > 0)),
              () -> assertEquals(none(), valid.filter(i -> i > 1)),
              () -> assertEquals(Either.right(1), valid.toEither()),
              () -> assertEquals("Valid(1)", valid.toString())
        );
  }

  @Test
  public void invalidTest() {
    Validation<String, Integer> invalid =  invalid("error");
    
    assertAll(() -> assertFalse(invalid.isValid()),
              () -> assertTrue(invalid.isInvalid()),
              () -> assertEquals("error", invalid.getError()),
              () -> assertThrows(NoSuchElementException.class, () -> invalid.get()),
              () -> assertEquals(invalid("error"), invalid.map(i -> i + 2)),
              () -> assertEquals(invalid("ERROR"), invalid.mapError(String::toUpperCase)),
              () -> assertEquals(invalid("error"), invalid.flatMap(i -> valid(valueOf(i)))),
              () -> assertEquals("error", invalid.fold(i -> valueOf(i), identity())),
              () -> assertEquals(some(invalid("error")), invalid.filter(i -> i > 0)),
              () -> assertEquals(some(invalid("error")), invalid.filter(i -> i > 1)),
              () -> assertEquals(Either.left("error"), invalid.toEither()),
              () -> assertEquals("Invalid(error)", invalid.toString())
        );
  }
  
  @Test
  public void map2Test() {
    assertAll(() -> assertEquals(valid(3), map2(valid(1), valid(2), sum2)),
              () -> assertEquals(invalid(InmutableList.of("error")), map2(valid(1), invalid("error"), sum2)),
              () -> assertEquals(invalid(InmutableList.of("error")), map2(invalid("error"), valid(1), sum2)),
              () -> assertEquals(invalid(InmutableList.of("error1", "error2")), 
                  map2(invalid("error1"), invalid("error2"), sum2))
        );
  }
  
  @Test
  public void map3Test() {
    assertAll(() -> assertEquals(valid(6), map3(valid(1), valid(2), valid(3), sum3)),
              () -> assertEquals(invalid(InmutableList.of("error1", "error2", "error3")), 
                  map3(invalid("error1"), invalid("error2"), invalid("error3"), sum3))
        );
  }
  
  @Test
  public void map4Test() {
    assertAll(() -> assertEquals(valid(10), map4(valid(1), valid(2), valid(3), valid(4), sum4)),
              () -> assertEquals(invalid(InmutableList.of("error1", "error2", "error3", "error4")), 
                  map4(invalid("error1"), invalid("error2"), invalid("error3"), invalid("error4"), sum4))
        );
  }
  
  @Test
  public void map5Test() {
    assertAll(() -> assertEquals(valid(15), map5(valid(1), valid(2), valid(3), valid(4), valid(5), sum5)),
              () -> assertEquals(invalid(InmutableList.of("error1", "error2", "error3", "error4", "error5")), 
                  map5(invalid("error1"), invalid("error2"), invalid("error3"), invalid("error4"), invalid("error5"), sum5))
        );
  }
}
