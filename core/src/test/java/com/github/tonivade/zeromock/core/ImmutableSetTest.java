package com.github.tonivade.zeromock.core;

import static com.github.tonivade.zeromock.core.Function1.identity;
import static com.github.tonivade.zeromock.core.Sequence.setOf;
import static java.util.Collections.emptySet;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

public class ImmutableSetTest {
  
  private final Function1<String, String> toUpperCase = String::toUpperCase;
  
  @Test
  public void notEmptySet() {
    ImmutableSet<String> set = setOf("a", "b", "c");
    
    assertAll(() -> assertEquals(3, set.size()),
              () -> assertFalse(set.isEmpty()),
              () -> assertTrue(set.contains("a")),
              () -> assertFalse(set.contains("z")),
              () -> assertEquals("abc", set.fold("", (a, b) -> a + b)),
              () -> assertEquals("cba", set.foldRight("", (a, b) -> a + b)),
              () -> assertEquals("abc", set.foldLeft("", (a, b) -> a + b)),
              () -> assertEquals(Option.some("abc"), set.reduce((a, b) -> a + b)),
              () -> assertEquals(setOf("a", "b", "c"), set),
              () -> assertEquals(ImmutableSet.from(Arrays.asList("a", "b", "c")), set),
              () -> assertEquals(new HashSet<>(Arrays.asList("a", "b", "c")), set.toSet()),
              () -> assertEquals(setOf("a", "b", "c"), set.append("c")),
              () -> assertEquals(setOf("a", "b", "c", "z"), set.append("z")),
              () -> assertEquals(setOf("a", "b"), set.remove("c")),
              () -> assertEquals(setOf("a", "b", "c"), set.remove("z")),
              () -> assertEquals(setOf("a", "b", "c", "z"), set.union(setOf("z"))),
              () -> assertEquals(ImmutableSet.empty(), set.intersection(setOf("z"))),
              () -> assertEquals(setOf("a", "b"), set.intersection(setOf("a", "b"))),
              () -> assertEquals(setOf("c"), set.difference(setOf("a", "b"))),
              () -> assertEquals(setOf("a", "b", "c"), set.map(identity())),
              () -> assertEquals(setOf("A", "B", "C"), set.map(toUpperCase)),
              () -> assertEquals(setOf("A", "B", "C"), set.flatMap(toUpperCase.liftSequence())),
              () -> assertEquals(setOf("a", "b", "c"), setOf(set).flatten()),
              () -> assertThrows(UnsupportedOperationException.class, () -> set.flatten()),
              () -> assertEquals(setOf("a", "b", "c"), set.filter(e -> e.length() > 0)),
              () -> assertEquals(ImmutableSet.empty(), set.filter(e -> e.length() > 1)));
  }
  
  @Test
  public void emptyList() {
    ImmutableSet<String> set = ImmutableSet.empty();
    
    assertAll(() -> assertEquals(0, set.size()),
              () -> assertTrue(set.isEmpty()),
              () -> assertFalse(set.contains("z")),
              () -> assertEquals("", set.fold("", (a, b) -> a + b)),
              () -> assertEquals(Option.none(), set.reduce((a, b) -> a + b)),
              () -> assertEquals(ImmutableSet.empty(), set),
              () -> assertEquals(ImmutableSet.from(Collections.emptyList()), set),
              () -> assertEquals(emptySet(), set.toSet()),
              () -> assertEquals(setOf("z"), set.append("z")),
              () -> assertEquals(setOf("z"), set.union(setOf("z"))),
              () -> assertEquals(ImmutableSet.empty(), set.remove("c")),
              () -> assertEquals(ImmutableSet.empty(), set.map(identity())),
              () -> assertEquals(ImmutableSet.empty(), set.map(toUpperCase)),
              () -> assertEquals(ImmutableSet.empty(), set.flatMap(toUpperCase.liftSequence())),
              () -> assertEquals(ImmutableSet.empty(), setOf(set).flatten()),
              () -> assertEquals(ImmutableSet.empty(), set.flatten()),
              () -> assertEquals(ImmutableSet.empty(), set.filter(e -> e.length() > 1)));
  }
  
  @Test
  public void listLaws() {
    FunctorLaws.verifyLaws(setOf("a", "b", "c"));
  }
}
