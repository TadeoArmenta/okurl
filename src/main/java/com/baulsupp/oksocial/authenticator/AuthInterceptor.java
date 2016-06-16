package com.baulsupp.oksocial.authenticator;

import com.baulsupp.oksocial.credentials.ServiceDefinition;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public interface AuthInterceptor<T> {
  String name();

  boolean supportsUrl(HttpUrl url);

  Response intercept(Interceptor.Chain chain, Optional<T> credentials) throws IOException;

  T authorize(OkHttpClient client) throws IOException;

  ServiceDefinition<T> serviceDefinition();

  default Future<Optional<ValidatedCredentials>> validate(OkHttpClient client,
      Request.Builder requestBuilder, T credentials) throws IOException {
    return CompletableFuture.completedFuture(Optional.empty());
  }
}
