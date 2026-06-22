package com.omniflow.core.di;

import android.content.Context;
import com.omniflow.core.data.local.OmniFlowDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class DatabaseModule_ProvideOmniFlowDatabaseFactory implements Factory<OmniFlowDatabase> {
  private final Provider<Context> contextProvider;

  public DatabaseModule_ProvideOmniFlowDatabaseFactory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public OmniFlowDatabase get() {
    return provideOmniFlowDatabase(contextProvider.get());
  }

  public static DatabaseModule_ProvideOmniFlowDatabaseFactory create(
      Provider<Context> contextProvider) {
    return new DatabaseModule_ProvideOmniFlowDatabaseFactory(contextProvider);
  }

  public static OmniFlowDatabase provideOmniFlowDatabase(Context context) {
    return Preconditions.checkNotNullFromProvides(DatabaseModule.INSTANCE.provideOmniFlowDatabase(context));
  }
}
