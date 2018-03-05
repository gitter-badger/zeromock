/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.core;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.function.Function;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class Deserializers {
  
  private Deserializers() {}
  
  public static Function<Bytes, JsonElement> json() {
    return plain().andThen(asJson());
  }
  
  public static <T> Function<Bytes, T> xml(Class<T> clazz) {
    return bytes -> Deserializers.<T>fromXml(bytes, clazz);
  }
  
  public static <T> Function<Bytes, T> json(Class<T> clazz) {
    return plain().andThen(fromJson(clazz));
  }
  
  public static <T> Function<Bytes, T> json(Type type) {
    return plain().andThen(fromJson(type));
  }
  
  public static Function<Bytes, String> plain() {
    return Bytes::asString;
  }
  
  private static Function<String, JsonElement> asJson() {
    return json -> new JsonParser().parse(json);
  }
  
  private static <T> Function<String, T> fromJson(Type type) {
    return json -> new GsonBuilder().create().fromJson(json, type);
  }
  
  @SuppressWarnings("unchecked")
  private static <T> T fromXml(Bytes bytes, Class<T> clazz) {
    try {
      JAXBContext context = JAXBContext.newInstance(clazz);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      ByteArrayInputStream input = new ByteArrayInputStream(bytes.toArray());
      return (T) unmarshaller.unmarshal(input);
    } catch (JAXBException e) {
      throw new UncheckedIOException(new IOException(e));
    }
  }
}