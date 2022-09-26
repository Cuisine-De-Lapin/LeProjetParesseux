package cuisine.de.lapin.simpleblockchain.utils

import java.math.BigInteger
import java.security.MessageDigest
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

fun String.sha256(): String {
    return BigInteger(
        1,
        MessageDigest
            .getInstance("SHA-256")
            .digest(toByteArray())
    )
        .toString(16)
        .padStart(64, '0')
}