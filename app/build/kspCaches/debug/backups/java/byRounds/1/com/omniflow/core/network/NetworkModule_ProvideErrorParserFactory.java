package com.omniflow.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.serialization.json.Json;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class NetworkModule_ProvideErrorParserFactory implements Factory<ErrorParser> {
  private final Provider<Json> jsonProvider;

  public NetworkModule_ProvideErrorParserFactory(Provider<Json> jsonProvider) {
    this.jsonProvider = jsonProvider;
  }

  @Override
  public ErrorParser get() {
    return provideErrorParser(jsonProvider.get());
  }

  public static NetworkModule_ProvideErrorParserFactory create(Provider<Json> jsonProvider) {
    return new NetworkModule_ProvideErrorParserFactory(jsonProvider);
  }

  public static ErrorParser provideErrorParser(Json json) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideErrorParser(json));
  }
}
