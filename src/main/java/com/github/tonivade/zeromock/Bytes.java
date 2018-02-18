/*
 * Copyright (c) 2018, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.requireNonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Bytes {

  private static final int BUFFER_SIZE = 1024;
  
  private final ByteBuffer buffer;

  private Bytes(byte[] buffer) {
    this.buffer = wrap(requireNonNull(buffer));
  }

  public byte[] toArray() {
    return buffer.duplicate().array();
  }

  public ByteBuffer getBuffer() {
    return buffer.duplicate();
  }

  public int size() {
    return buffer.remaining();
  }
  
  public boolean isEmpty() {
    return !buffer.hasRemaining();
  }

  public static Bytes empty() {
    return new Bytes(new byte[]{});
  }

  public static Bytes asByteBuffer(InputStream input) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    byte[] buffer = new byte[BUFFER_SIZE];
    while (true) {
      int read = input.read(buffer);
      if (read > 0) {
        out.write(buffer, 0, read);
      } else break;
    }
    return new Bytes(out.toByteArray());
  }
  
  public static Bytes asByteBuffer(String string) {
    return new Bytes(string.getBytes(UTF_8));
  }
  
  public static String asString(Bytes buffer) {
    return new String(buffer.toArray(), UTF_8);
  }
}
