package com.distep.babyjournal.data.db.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Query
import com.distep.babyjournal.data.entity.Record
import java.time.LocalDateTime
import java.time.ZoneOffset

@Dao
interface RecordDao: AbstractDao<Record> {
    @Query("SELECT * FROM record WHERE uid=:id")
    suspend fun getOneById(id: Long): Record?

    @Query("SELECT * FROM record")
    suspend fun getAll(): List<Record>

    @Query("SELECT * FROM record r WHERE type='EVENT' AND date_time >= :from AND date_time < :to ORDER BY date_time")
    fun getItems(
        from: Long,
        to: Long
    ): DataSource.Factory<Int, Record>

    @Query("SELECT * FROM record r WHERE type='WEIGHT' ORDER BY date_time")
    fun getWeightItems(): DataSource.Factory<Int, Record>
}