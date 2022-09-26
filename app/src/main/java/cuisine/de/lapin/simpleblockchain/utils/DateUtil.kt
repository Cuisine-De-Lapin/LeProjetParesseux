package cuisine.de.lapin.simpleblockchain.utils

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun LocalDateTime.toLongTimestamp(zoneId: ZoneId = ZoneId.systemDefault()) = ZonedDateTime.of(this, zoneId).toInstant().toEpochMilli()