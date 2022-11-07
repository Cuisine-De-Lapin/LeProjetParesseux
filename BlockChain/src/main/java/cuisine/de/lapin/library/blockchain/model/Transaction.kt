package cuisine.de.lapin.library.blockchain.model

data class Transaction(
    val id: String,
    val timeStamp: Long,
    val inTxs: List<Tx.InputTx>,
    val outTxs: List<Tx.OutPutTx>
)

sealed class Tx(val owner: String, val amount: UInt) {
    class InputTx
    class OutPutTx
}
