package com.omniflow.core.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata("javax.inject.Named")
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
public final class AppModule_Companion_ProvideBaseUrlFactory implements Factory<String> {
  @Override
  public String get() {
    return provideBaseUrl();
  }

  public static AppModule_Companion_ProvideBaseUrlFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static String provideBaseUrl() {
    return Preconditions.checkNotNullFromProvides(AppModule.Companion.provideBaseUrl());
  }

  private static final class InstanceHolder {
    private static final AppModule_Companion_ProvideBaseUrlFactory INSTANCE = new AppModule_Companion_ProvideBaseUrlFactory();
  }
}
