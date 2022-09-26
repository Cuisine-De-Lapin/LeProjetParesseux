package cuisine.de.lapin.library.blockchain.model

data class ProofData(
    val index: Int,
    val timeStamp: Long,
    val content: Any,
    val previousHash: String,
    var nonce: Long = 0L
)

class Block (
    val index: Int,
    val timeStamp: Long,
    val content: Any,
    val previousHash: String,
    val nonce: Long,
    val hash: String)

class Genesis(message: String)

fun Block.toProofData(): ProofData {
    return ProofData(
        index = index,
        timeStamp = timeStamp,
        content = content,
        previousHash = previousHash,
        nonce = nonce
    )
}

fun ProofData.toBlock(hash: String): Block {
    return Block(
        index = index,
        timeStamp = timeStamp,
        content = content,
        previousHash = previousHash,
        nonce = nonce,
        hash = hash
    )
}

