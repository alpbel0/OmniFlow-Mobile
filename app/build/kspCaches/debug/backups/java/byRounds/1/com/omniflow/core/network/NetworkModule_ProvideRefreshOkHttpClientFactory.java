package com.omniflow.core.network;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;

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
public final class NetworkModule_ProvideRefreshOkHttpClientFactory implements Factory<OkHttpClient> {
  @Override
  public OkHttpClient get() {
    return provideRefreshOkHttpClient();
  }

  public static NetworkModule_ProvideRefreshOkHttpClientFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OkHttpClient provideRefreshOkHttpClient() {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideRefreshOkHttpClient());
  }

  private static final class InstanceHolder {
    private static final NetworkModule_ProvideRefreshOkHttpClientFactory INSTANCE = new NetworkModule_ProvideRefreshOkHttpClientFactory();
  }
}
