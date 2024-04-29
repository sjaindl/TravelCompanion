package com.sjaindl.travelcompanion.di

import com.sjaindl.travelcompanion.api.firestore.FireStoreClient
import com.sjaindl.travelcompanion.api.firestore.FireStoreClientImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ApplicationBindingModule {
    @Binds
    abstract fun bindFireStoreClient(fireStoreClient: FireStoreClientImpl): FireStoreClient
}
