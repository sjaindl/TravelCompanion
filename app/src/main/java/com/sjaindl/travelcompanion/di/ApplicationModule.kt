package com.sjaindl.travelcompanion.di

import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sjaindl.travelcompanion.api.google.GooglePlacesClient
import com.sjaindl.travelcompanion.api.google.GooglePlacesClientImpl
import com.sjaindl.travelcompanion.com.sjaindl.travelcompanion.di.AndroidPersistenceInjector
import com.sjaindl.travelcompanion.repository.DataRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {
    @Provides
    fun provideAuth(): FirebaseAuth {
        return Firebase.auth
    }

    @Provides
    fun provideDataRepository(
        @ApplicationContext context: Context,
    ): DataRepository {
        return AndroidPersistenceInjector(context).shared.dataRepository
    }

    @Provides
    fun provideGooglePlacesClient(
        @ApplicationContext context: Context,
    ): GooglePlacesClient {
        return GooglePlacesClientImpl(context)
    }
}
