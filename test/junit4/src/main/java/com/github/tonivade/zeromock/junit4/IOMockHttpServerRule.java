/*
 * Copyright (c) 2018-2019, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.junit4;

import static com.github.tonivade.zeromock.server.IOMockHttpServer.sync;

import com.github.tonivade.purefun.monad.IO;
import com.github.tonivade.zeromock.api.IORequestHandler;

public class IOMockHttpServerRule extends AbstractMockServerRule<IO.µ, IORequestHandler> {

  public IOMockHttpServerRule(int port) {
    super(sync().port(port).build());
  }
}
