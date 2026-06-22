package com.omniflow.core.di;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata
@QualifierMetadata("com.omniflow.core.di.ApplicationScope")
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
public final class DispatcherModule_ProvideApplicationScopeFactory implements Factory<CoroutineScope> {
  @Override
  public CoroutineScope get() {
    return provideApplicationScope();
  }

  public static DispatcherModule_ProvideApplicationScopeFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static CoroutineScope provideApplicationScope() {
    return Preconditions.checkNotNullFromProvides(DispatcherModule.INSTANCE.provideApplicationScope());
  }

  private static final class InstanceHolder {
    private static final DispatcherModule_ProvideApplicationScopeFactory INSTANCE = new DispatcherModule_ProvideApplicationScopeFactory();
  }
}
