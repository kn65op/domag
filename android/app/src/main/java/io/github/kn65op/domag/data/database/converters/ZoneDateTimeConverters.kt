package io.github.kn65op.domag.data.database.converters

import androidx.room.TypeConverter
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ZoneDateTimeConverters {
    @TypeConverter
    fun fromText(value: String?): ZonedDateTime? =
        value?.let {
            ZonedDateTime.parse(
                it,
                timeFormatter
            )
        }

    @TypeConverter
    fun dateToText(date: ZonedDateTime?): String? =
        date?.format(timeFormatter)

    companion object {
        private val timeFormatter = DateTimeFormatter.ISO_ZONED_DATE_TIME
    }
}