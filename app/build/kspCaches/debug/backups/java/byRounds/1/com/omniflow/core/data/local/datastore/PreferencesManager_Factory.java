package com.omniflow.core.data.local.datastore;

import android.content.Context;
import com.omniflow.core.auth.TokenCipher;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class PreferencesManager_Factory implements Factory<PreferencesManager> {
  private final Provider<Context> contextProvider;

  private final Provider<TokenCipher> tokenCipherProvider;

  public PreferencesManager_Factory(Provider<Context> contextProvider,
      Provider<TokenCipher> tokenCipherProvider) {
    this.contextProvider = contextProvider;
    this.tokenCipherProvider = tokenCipherProvider;
  }

  @Override
  public PreferencesManager get() {
    return newInstance(contextProvider.get(), tokenCipherProvider.get());
  }

  public static PreferencesManager_Factory create(Provider<Context> contextProvider,
      Provider<TokenCipher> tokenCipherProvider) {
    return new PreferencesManager_Factory(contextProvider, tokenCipherProvider);
  }

  public static PreferencesManager newInstance(Context context, TokenCipher tokenCipher) {
    return new PreferencesManager(context, tokenCipher);
  }
}
