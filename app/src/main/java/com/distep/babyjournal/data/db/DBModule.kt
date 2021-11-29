package com.distep.babyjournal.data.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.RoomDatabase.Builder
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ): AppDb {

        val rdc: RoomDatabase.Callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
            }
        }

        val builder: Builder<AppDb> =
            Room.databaseBuilder(
                appContext,
                AppDb::class.java,
                "baby-journal-database"
            )

        return builder
            .allowMainThreadQueries()
            .addMigrations(AppDb.MIGRATION_1_2)
            .fallbackToDestructiveMigration()
            .addCallback(rdc)
            .build()
    }
}