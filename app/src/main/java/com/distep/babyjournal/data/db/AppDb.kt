package com.distep.babyjournal.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.distep.babyjournal.data.db.dao.RecordDao
import com.distep.babyjournal.data.entity.Record

@Database(
    entities = [Record::class],
    version = 2
)
@TypeConverters(Converters::class)
abstract class AppDb : RoomDatabase() {
    abstract fun recordDao(): RecordDao

    companion object {
        @JvmField
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE record ADD COLUMN type TEXT NOT NULL DEFAULT 'EVENT'")
            }
        }
    }
}