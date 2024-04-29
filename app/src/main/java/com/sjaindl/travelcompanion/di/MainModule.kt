package com.sjaindl.travelcompanion.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.facebook.CallbackManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext

@Module
@InstallIn(ActivityComponent::class)
class MainModule {
    @Provides
    fun provideCallbackManager(): CallbackManager {
        return CallbackManager.Factory.create()
    }

    @Provides
    fun provideCredentialManager(
        @ActivityContext context: Context,
    ): CredentialManager {
        return CredentialManager.create(context)
    }
}
