package com.distep.babyjournal.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.ForeignKey.SET_NULL
import androidx.room.PrimaryKey
import java.time.LocalDateTime
import java.util.*

@Entity(
    tableName = "record"
)
data class Record(
    @ColumnInfo(name = "date_time")
    var dateTime: Date = Calendar.getInstance().time,
    val event: String,
    @ColumnInfo(defaultValue = "EVENT")
    val type: RecordType = RecordType.EVENT,
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Long = 0
}