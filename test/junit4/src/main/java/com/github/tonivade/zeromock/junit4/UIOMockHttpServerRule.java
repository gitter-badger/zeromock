/*
 * Copyright (c) 2018-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.junit4;

import com.github.tonivade.purefun.effect.UIO_;

import static com.github.tonivade.zeromock.server.UIOMockHttpServer.sync;

public class UIOMockHttpServerRule extends AbstractMockServerRule<UIO_> {

  public UIOMockHttpServerRule() {
     this(0);
  }

  public UIOMockHttpServerRule(int port) {
    super(sync().port(port).buildK());
  }
}
