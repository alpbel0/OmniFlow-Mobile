package com.omniflow.core.auth;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class TokenCipher_Factory implements Factory<TokenCipher> {
  @Override
  public TokenCipher get() {
    return newInstance();
  }

  public static TokenCipher_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TokenCipher newInstance() {
    return new TokenCipher();
  }

  private static final class InstanceHolder {
    private static final TokenCipher_Factory INSTANCE = new TokenCipher_Factory();
  }
}
