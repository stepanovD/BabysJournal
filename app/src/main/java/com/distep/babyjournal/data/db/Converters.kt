package com.distep.babyjournal.data.db

import androidx.room.TypeConverter
import com.distep.babyjournal.data.entity.RecordType
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun recordTypeFromCode(name: String?): RecordType? {
        return name?.let { RecordType.valueOf(it) }
    }

    @TypeConverter
    fun recordTypeToCode(type: RecordType?): String? {
        return type?.name
    }
}