package com.github.huifer.sc.gateway.filter;

import com.github.huifer.sc.http.HttpStaticValue;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Order(-1)
@Component
public class RequestGlobalFilter implements GlobalFilter {

  private String uuid() {
    return UUID.randomUUID().toString();
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    request.getHeaders().add(HttpStaticValue.TRACE_ID, uuid());
    String rawPath = request.getURI().getRawPath();
    String newPath = "/" + Arrays.stream(StringUtils.tokenizeToStringArray(rawPath, "/")).skip(1L)
        .collect(Collectors.joining("/"));
    ServerHttpRequest newRequest = request.mutate().path(newPath).build();
    return chain.filter(exchange.mutate().request(newRequest.mutate().build()).build());
  }


}