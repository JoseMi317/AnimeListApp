package com.josemi.animediary.core.model

import org.junit.Assert.assertEquals
import org.junit.Test

class RatingQualifierTest {
    @Test
    fun automaticQualifierReturnsExpectedLabelsForRanges() {
        assertEquals("QUE ACABO DE VER?", automaticRatingQualifier(1.9))
        assertEquals("BODRIO", automaticRatingQualifier(2.0))
        assertEquals("REALLY?", automaticRatingQualifier(3.5))
        assertEquals("MEEEH", automaticRatingQualifier(4.9))
        assertEquals("MID", automaticRatingQualifier(5.0))
        assertEquals("DECENTE", automaticRatingQualifier(6.9))
        assertEquals("ESTA BUENO", automaticRatingQualifier(7.2))
        assertEquals("JOYITA", automaticRatingQualifier(8.8))
        assertEquals("ABSOLUT CINEMA", automaticRatingQualifier(9.7))
        assertEquals("OBRA MAESTRA", automaticRatingQualifier(9.8))
    }

    @Test
    fun automaticQualifierHandlesNullAndOutOfRangeScores() {
        assertEquals("SIN NOTA", automaticRatingQualifier(null))
        assertEquals("QUE ACABO DE VER?", automaticRatingQualifier(-5.0))
        assertEquals("OBRA MAESTRA", automaticRatingQualifier(12.0))
    }

    @Test
    fun resolvedQualifierUsesCustomOnlyWhenCustomModeHasText() {
        assertEquals(
            "MI TEXTO",
            resolvedRatingQualifier(8.0, " MI TEXTO ", RatingLabelMode.Custom)
        )
        assertEquals("JOYITA", resolvedRatingQualifier(8.0, "", RatingLabelMode.Custom))
        assertEquals("JOYITA", resolvedRatingQualifier(8.0, "Otro", RatingLabelMode.Auto))
    }
}
