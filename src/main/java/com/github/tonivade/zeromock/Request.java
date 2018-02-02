package com.github.tonivade.zeromock;

import static java.util.Collections.unmodifiableMap;

import java.util.List;
import java.util.Map;

public final class Request {
  final String method;
  final String url;
  final String body;
  final Map<String, List<String>> headers;
  final Map<String, String> params;

  public Request(String method, String url, String body, 
                 Map<String, List<String>> headers, Map<String, String> params) {
    this.method = method;
    this.url = url;
    this.body = body;
    this.headers = unmodifiableMap(headers);
    this.params = unmodifiableMap(params);
  }
}
