package cuisine.de.lapin.simpleblockchain.blockchain.model

data class ProofData(
    val index: Int,
    val timeStamp: Long,
    val event: Event,
    val previousHash: String,
    var nonce: Long = 0L
)

class Block (
    val index: Int,
    val timeStamp: Long,
    val event: Event,
    val previousHash: String,
    val nonce: Long,
    val hash: String)

data class Event(
    val message: String
)

fun Block.toProofData(): ProofData {
    return ProofData(
        index = index,
        timeStamp = timeStamp,
        event = event,
        previousHash = previousHash,
        nonce = nonce
    )
}

fun ProofData.toBlock(hash: String): Block {
    return Block(
        index = index,
        timeStamp = timeStamp,
        event = event,
        previousHash = previousHash,
        nonce = nonce,
        hash = hash
    )
}

