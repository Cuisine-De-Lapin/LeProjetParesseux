package cuisine.de.lapin.library.blockchain.model

data class Block(
    val hash: String,
    val previousHash: String,
    val height: UInt,
    val timeStamp: Long,
    val nonce: UInt,
    val difficulty: Int,
    val content: Any
)