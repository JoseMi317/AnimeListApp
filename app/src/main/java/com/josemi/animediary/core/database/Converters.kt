package com.josemi.animediary.core.database

import androidx.room.TypeConverter
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.RatingLabelMode
import com.josemi.animediary.core.model.ReleaseStatus

class Converters {
    @TypeConverter
    fun personalStatusToString(value: PersonalStatus): String = value.name

    @TypeConverter
    fun stringToPersonalStatus(value: String): PersonalStatus = PersonalStatus.valueOf(value)

    @TypeConverter
    fun releaseStatusToString(value: ReleaseStatus): String = value.name

    @TypeConverter
    fun stringToReleaseStatus(value: String): ReleaseStatus = ReleaseStatus.valueOf(value)

    @TypeConverter
    fun ratingLabelModeToString(value: RatingLabelMode): String = value.name

    @TypeConverter
    fun stringToRatingLabelMode(value: String): RatingLabelMode = RatingLabelMode.valueOf(value)
}
