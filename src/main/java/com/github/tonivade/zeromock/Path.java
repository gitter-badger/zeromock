/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Path {
  private final List<PathElement> value;
  
  public Path(String path) {
    this(Stream.of(path.split("/")).skip(1).map(Path::toPathElement).collect(toList()));
  }
  
  private Path(List<PathElement> path) {
    this.value = unmodifiableList(path);
  }
  
  public Path dropOneLevel() {
    return new Path(value.stream().skip(1).collect(toList()));
  }
  
  public PathElement getAt(int pos) {
    return value.get(pos);
  }
  
  public boolean match(Path path) {
    return Pattern.matches(path.toString(), toString());
  }

  @Override
  public String toString() {
    return "/" + value.stream().map(PathElement::toString).collect(joining("/"));
  }
  
  private static PathElement toPathElement(String value) {
    if (value.startsWith("{") && value.endsWith("}")) {
      return new PathParam(value.substring(1, value.length() - 2));
    }
    return new PathValue(value);
  }
  
  private static abstract class PathElement {
    final String value;
    
    public PathElement(String value) {
      this.value = value;
    }
  }
  
  private static final class PathValue extends PathElement {
    public PathValue(String value) {
      super(value);
    }
    
    @Override
    public String toString() {
      return value;
    }
  }
  
  private static final class PathParam extends PathElement {
    public PathParam(String value) {
      super(value);
    }
    
    @Override
    public String toString() {
      return "\\w+";
    }
  }
}
