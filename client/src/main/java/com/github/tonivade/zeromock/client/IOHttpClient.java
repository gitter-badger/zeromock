/*
 * Copyright (c) 2018-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.client;

import static java.util.Objects.requireNonNull;
import com.github.tonivade.purefun.instances.IOInstances;
import com.github.tonivade.purefun.monad.IO;
import com.github.tonivade.purefun.monad.IOOf;
import com.github.tonivade.purefun.monad.IO_;
import com.github.tonivade.zeromock.api.HttpRequest;
import com.github.tonivade.zeromock.api.HttpResponse;

public class IOHttpClient {

  private final HttpClientK<IO_> client;

  public IOHttpClient(HttpClientK<IO_> client) {
    this.client = requireNonNull(client);
  }

  public static IOHttpClient connectTo(String baseUrl) {
    return new IOHttpClient(new HttpClientK<>(baseUrl, IOInstances.monadDefer()));
  }

  public IO<HttpResponse> request(HttpRequest request) {
    return client.request(request).fix(IOOf::narrowK);
  }
}
