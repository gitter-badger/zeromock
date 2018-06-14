/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.api;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;

import javax.xml.bind.DataBindingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.github.tonivade.zeromock.core.Function1;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public final class Deserializers {
  
  private static ThreadLocal<GsonBuilder> gsonImplicit = ThreadLocal.withInitial(GsonBuilder::new);
  
  private Deserializers() {}
  
  public static void withGsonBuilder(GsonBuilder builder) {
    gsonImplicit.set(builder);
  }

  public static GsonBuilder gsonBuilder() {
    return gsonImplicit.get();
  }
  
  public static Function1<Bytes, JsonElement> json() {
    return plain().andThen(asJson());
  }
  
  public static <T> Function1<Bytes, T> xml(Class<T> clazz) {
    return bytes -> Deserializers.<T>fromXml(bytes, clazz);
  }
  
  public static <T> Function1<Bytes, T> json(Class<T> clazz) {
    return plain().andThen(fromJson(clazz));
  }
  
  public static <T> Function1<Bytes, T> json(Type type) {
    return plain().andThen(fromJson(type));
  }
  
  public static Function1<Bytes, String> plain() {
    return Bytes::asString;
  }
  
  private static Function1<String, JsonElement> asJson() {
    return new JsonParser()::parse;
  }
  
  private static <T> Function1<String, T> fromJson(Type type) {
    return json -> gsonBuilder().create().fromJson(json, type);
  }
  
  @SuppressWarnings("unchecked")
  private static <T> T fromXml(Bytes bytes, Class<T> clazz) {
    try (InputStream input = new ByteArrayInputStream(bytes.toArray())) {
      JAXBContext context = JAXBContext.newInstance(clazz);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      return (T) unmarshaller.unmarshal(input);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (JAXBException e) {
      throw new DataBindingException(e);
    }
  }
}
