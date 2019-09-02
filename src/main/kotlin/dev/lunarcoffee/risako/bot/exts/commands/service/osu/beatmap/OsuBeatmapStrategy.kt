package dev.lunarcoffee.risako.bot.exts.commands.service.osu.beatmap

import com.google.gson.FieldNamingStrategy
import java.lang.reflect.Field

class OsuBeatmapStrategy : FieldNamingStrategy {
    override fun translateName(field: Field): String {
        return when (field.name) {
            "name" -> "title"
            "beatmapId" -> "beatmap_id"
            "starRatingRaw" -> "difficultyrating"
            "lengthSeconds" -> "total_length"
            "cs" -> "diff_size"
            "ar" -> "diff_approach"
            "hp" -> "diff_drain"
            "od" -> "diff_overall"
            "statusRaw" -> "approved"
            "maxCombo" -> "max_combo"
            else -> field.name
        }
    }
}
