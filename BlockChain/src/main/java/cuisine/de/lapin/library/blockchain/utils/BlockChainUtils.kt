package cuisine.de.lapin.library.blockchain.utils

import android.os.Looper
import com.google.gson.Gson
import cuisine.de.lapin.library.blockchain.model.Block
import java.math.BigInteger
import java.security.MessageDigest

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

fun Block.toJson(): String {
    return Gson().toJson(this)
}

fun String.toBlock(): Block? {
    return try {
        Gson().fromJson(this, Block::class.java)
    } catch (exception: Exception) {
        null
    }
}

fun Block.toPayLoad(): String {
    return Block.getPayload(content, previousHash, height, timeStamp, nonce)
}