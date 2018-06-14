/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.junit5;

import java.util.Objects;

import com.github.tonivade.zeromock.core.Equal;
import com.github.tonivade.zeromock.core.ImmutableList;
import com.github.tonivade.zeromock.core.Option;

public class BooksService {

  public ImmutableList<Book> findAll() {
    return ImmutableList.of(new Book(1, "title"));
  }

  public Option<Book> find(Integer id) {
    return Option.some(new Book(id, "title"));
  }

  public Book create(String title) {
    return new Book(1, title);
  }

  public Book update(Integer id, String title) {
    return new Book(id, title);
  }

  public void delete(Integer id) {
    // nothing to do
  }
  
  public static class Book {
    private final Integer id;
    private final String title;

    public Book(Integer id, String title) {
      this.id = id;
      this.title = title;
    }
    
    @Override
    public String toString() {
      return "Book(id:" + id + ",title:" + title + ")";
    }

    @Override
    public int hashCode() {
      return Objects.hash(id, title);
    }

    @Override
    public boolean equals(Object obj) {
      return Equal.equal(this)
          .append((a, b) -> Objects.equals(a.id, b.id))
          .append((a, b) -> Objects.equals(a.title, b.title))
          .applyTo(obj);
    }
  }
}
