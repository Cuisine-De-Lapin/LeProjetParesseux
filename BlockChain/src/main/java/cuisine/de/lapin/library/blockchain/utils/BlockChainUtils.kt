package cuisine.de.lapin.library.blockchain.utils

import com.google.gson.Gson
import cuisine.de.lapin.library.blockchain.model.Block

private const val INIT_NONCE = 0u
private const val ZERO = "0"

internal fun Block.toJson(): String {
    return Gson().toJson(this)
}

internal fun createBlock(
    content: Any,
    previousHash: String,
    height: UInt,
    timeStamp: Long,
    difficulty: UInt
): Block {
    var nonce: UInt = INIT_NONCE
    var payload = ""
    var hash = ""

    while (true) {
        payload = getPayload(content, previousHash, height, timeStamp, difficulty, nonce)
        hash = payload.sha256()
        if (hash.startsWith(getStartZeros(difficulty))) {
            break
        } else {
            nonce++
        }
    }

    return Block(
        hash = hash,
        previousHash = previousHash,
        height = height,
        timeStamp = timeStamp,
        nonce = nonce,
        difficulty = difficulty,
        content = content
    )
}

private fun getStartZeros(difficulty: UInt): String {
    return ZERO.repeat(difficulty.toInt())
}

internal fun getPayload(block: Block): String {
    return getPayload(block.content, block.previousHash, block.height, block.timeStamp, block.difficulty, block.nonce)
}

internal fun getPayload(
    content: Any,
    previousHash: String,
    height: UInt,
    timeStamp: Long,
    difficulty: UInt,
    nonce: UInt
): String {
    return "$content$previousHash$height$timeStamp$difficulty$nonce"
}
