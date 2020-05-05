/*
 * Copyright (c) 2018-2020, Antonio Gabriel Muñoz Conejo <antoniogmc at gmail dot com>
 * Distributed under the terms of the MIT License
 */
package com.github.tonivade.zeromock.api;

import com.github.tonivade.purefun.effect.UIO;
import com.github.tonivade.purefun.instances.UIOInstances;

public interface UIORequestHandler extends RequestHandlerK<UIO.µ> {

  @Override
  default UIO<HttpResponse> apply(HttpRequest value) {
    return RequestHandlerK.super.apply(value).fix1(UIO::narrowK);
  }

  default UIORequestHandler preHandle(PreFilter before) {
    return RequestHandlerK.super.preHandle(UIOInstances.monad(), before)::apply;
  }

  default UIORequestHandler postHandle(PostFilter after) {
    return postHandle(UIOInstances.functor(), after).andThen(UIO::narrowK)::apply;
  }
}
