/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import static com.github.tonivade.zeromock.Predicates.body;
import static com.github.tonivade.zeromock.Predicates.delete;
import static com.github.tonivade.zeromock.Predicates.get;
import static com.github.tonivade.zeromock.Predicates.path;
import static com.github.tonivade.zeromock.Predicates.post;
import static com.github.tonivade.zeromock.Predicates.put;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Type;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import com.github.tonivade.zeromock.BooksService.Book;
import com.google.gson.reflect.TypeToken;

@ExtendWith(MockHttpServerExtension.class)
public class BooksServiceTest {
  
  private BooksAPI books = new BooksAPI(new BooksService());
  
  private HttpService booksService = new HttpService("books")
      .when(get("/books"), books.findAll())
      .when(get("/books/:id"), books.find())
      .when(post("/books"), books.create())
      .when(delete("/books/:id"), books.delete())
      .when(put("/books/:id"), books.update());
  
  @Test
  public void findsBooks(MockHttpServer server) {
    server.mount("/store", booksService);
    
    HttpClient client = new HttpClient("http://localhost:8080/store");
    
    HttpResponse response = client.request(Requests.get("/books"));
    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals(asList(new Book(1, "title")), asBooks(response.body())));
  }

  @Test
  public void findsBook(MockHttpServer server) {
    server.mount("/store", booksService);
    
    HttpClient client = new HttpClient("http://localhost:8080/store");
    
    HttpResponse response = client.request(Requests.get("/books/1"));
    
    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals(new Book(1, "title"), asBook(response.body())));
  }
  
  @Test
  public void createsBook(MockHttpServer server) {
    server.mount("/store", booksService);
    
    HttpClient client = new HttpClient("http://localhost:8080/store");
    
    HttpResponse response = client.request(Requests.post("/books").withBody("create"));
    
    assertAll(() -> assertEquals(HttpStatus.CREATED, response.status()),
              () -> assertEquals(new Book(1, "create"), asBook(response.body())),
              () -> server.verify(post().and(path("/store/books")).and(body("create"))));
  }
  
  @Test
  public void deletesBook(MockHttpServer server) {
    server.mount("/store", booksService);
    
    HttpClient client = new HttpClient("http://localhost:8080/store");
    
    HttpResponse response = client.request(Requests.delete("/books/1"));
    
    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals(null, asBook(response.body())));
  }
  
  @Test
  public void updatesBook(MockHttpServer server) {
    server.mount("/store", booksService);
    
    HttpClient client = new HttpClient("http://localhost:8080/store");
    
    HttpResponse response = client.request(Requests.put("/books/1").withBody("update"));
    
    assertAll(() -> assertEquals(HttpStatus.OK, response.status()),
              () -> assertEquals(new Book(1, "update"), asBook(response.body())));
  }

  private Book asBook(Bytes body) {
    return Deserializers.<Book>json(Book.class).apply(body);
  }

  private List<Book> asBooks(Bytes body) {
    return Deserializers.<List<Book>>json(listOfBooks()).apply(body);
  }
  
  private Type listOfBooks() {
    return new TypeToken<List<Book>>(){}.getType();
  }
}