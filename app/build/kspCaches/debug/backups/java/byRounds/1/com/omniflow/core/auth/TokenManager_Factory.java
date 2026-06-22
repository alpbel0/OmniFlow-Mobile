package com.omniflow.core.auth;

import com.omniflow.core.data.local.datastore.PreferencesManager;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TokenManager_Factory implements Factory<TokenManager> {
  private final Provider<PreferencesManager> preferencesManagerProvider;

  private final Provider<CoroutineScope> applicationScopeProvider;

  public TokenManager_Factory(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    this.preferencesManagerProvider = preferencesManagerProvider;
    this.applicationScopeProvider = applicationScopeProvider;
  }

  @Override
  public TokenManager get() {
    return newInstance(preferencesManagerProvider.get(), applicationScopeProvider.get());
  }

  public static TokenManager_Factory create(Provider<PreferencesManager> preferencesManagerProvider,
      Provider<CoroutineScope> applicationScopeProvider) {
    return new TokenManager_Factory(preferencesManagerProvider, applicationScopeProvider);
  }

  public static TokenManager newInstance(PreferencesManager preferencesManager,
      CoroutineScope applicationScope) {
    return new TokenManager(preferencesManager, applicationScope);
  }
}
