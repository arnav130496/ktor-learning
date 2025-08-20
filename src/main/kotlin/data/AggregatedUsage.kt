package com.arnav.data

import java.time.OffsetDateTime
import com.google.gson.JsonElement
import java.math.BigDecimal

data class AggregatedUsage(
    val time: OffsetDateTime,
    val projectId: String,
    val cdfService: String,
    val metricKey: String,
    val groupByDims: JsonElement?, // Use JsonElement for JSONB
    val valueSum: BigDecimal,
    val valueCount: Long
)