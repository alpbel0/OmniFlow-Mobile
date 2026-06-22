package com.omniflow.core.network;

import com.omniflow.core.auth.TokenStore;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideOkHttpClientFactory implements Factory<OkHttpClient> {
  private final Provider<TokenStore> tokenStoreProvider;

  private final Provider<RefreshTokenApi> refreshTokenApiProvider;

  public NetworkModule_ProvideOkHttpClientFactory(Provider<TokenStore> tokenStoreProvider,
      Provider<RefreshTokenApi> refreshTokenApiProvider) {
    this.tokenStoreProvider = tokenStoreProvider;
    this.refreshTokenApiProvider = refreshTokenApiProvider;
  }

  @Override
  public OkHttpClient get() {
    return provideOkHttpClient(tokenStoreProvider.get(), refreshTokenApiProvider.get());
  }

  public static NetworkModule_ProvideOkHttpClientFactory create(
      Provider<TokenStore> tokenStoreProvider, Provider<RefreshTokenApi> refreshTokenApiProvider) {
    return new NetworkModule_ProvideOkHttpClientFactory(tokenStoreProvider, refreshTokenApiProvider);
  }

  public static OkHttpClient provideOkHttpClient(TokenStore tokenStore,
      RefreshTokenApi refreshTokenApi) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideOkHttpClient(tokenStore, refreshTokenApi));
  }
}
