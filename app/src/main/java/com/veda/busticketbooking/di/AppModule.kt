package com.veda.busticketbooking.di

import android.app.Application
import android.content.Context
import com.veda.busticketbooking.db.AppDatabase
import com.veda.busticketbooking.db.dao.BusSeatDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun getAppDB(context: Application): AppDatabase {
        return AppDatabase.getAppDB(context)
    }

    @Singleton
    @Provides
    fun getDao(appDB: AppDatabase): BusSeatDAO {
        return appDB.getBusSeatDAO()
    }

}