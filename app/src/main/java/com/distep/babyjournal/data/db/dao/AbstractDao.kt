package com.distep.babyjournal.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

@Dao
interface AbstractDao<T> {
    @Insert
    suspend fun insertAll(entities: List<T>): LongArray

    @Insert
    suspend fun insert(entity: T): Long

    @Delete
    suspend fun delete(entity: T)

    @Update
    suspend fun update(entity: T)

    @Update
    suspend fun updateMany(entity: List<T>)
}
