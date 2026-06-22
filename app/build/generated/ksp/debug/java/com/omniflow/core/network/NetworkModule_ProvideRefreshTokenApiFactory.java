package com.omniflow.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.omniflow.core.network.RefreshClient")
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
public final class NetworkModule_ProvideRefreshTokenApiFactory implements Factory<RefreshTokenApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideRefreshTokenApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public RefreshTokenApi get() {
    return provideRefreshTokenApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideRefreshTokenApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideRefreshTokenApiFactory(retrofitProvider);
  }

  public static RefreshTokenApi provideRefreshTokenApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideRefreshTokenApi(retrofit));
  }
}
