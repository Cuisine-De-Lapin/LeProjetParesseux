package cuisine.de.lapin.library.blockchain.model

import cuisine.de.lapin.library.blockchain.utils.assertNotMainThread
import cuisine.de.lapin.library.blockchain.utils.sha256

data class Block(
    val hash: String,
    val previousHash: String,
    val height: UInt,
    val timeStamp: Long,
    val nonce: UInt,
    val content: Any
) {
    companion object {
        private const val INIT_NONCE = 0u
        private const val ZERO = "0"

        internal fun createBlock(
            content: Any,
            previousHash: String,
            height: UInt,
            timeStamp: Long,
            difficulty: Int
        ): Block {
            var nonce: UInt = INIT_NONCE
            var payload = ""
            var hash = ""

            assertNotMainThread("Create a Block")

            while (true) {
                payload = getPayload(content, previousHash, height, timeStamp, nonce)
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
                content = content
            )
        }

        private fun getStartZeros(difficulty: Int): String {
            return ZERO.repeat(difficulty)
        }

        internal fun getPayload(
            content: Any,
            previousHash: String,
            height: UInt,
            timeStamp: Long,
            nonce: UInt
        ): String {
            return "$content$previousHash$height$timeStamp$nonce"
        }
    }

}
