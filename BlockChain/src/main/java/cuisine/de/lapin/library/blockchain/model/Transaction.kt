package cuisine.de.lapin.library.blockchain.model

data class Transaction(
    val id: String,
    val timeStamp: Long,
    val inTxs: List<Tx.InputTx>,
    val outTxs: List<Tx.OutPutTx>
)

sealed class Tx {
    data class InputTx (val txId: String, val index: Int, val owner: String)
    class OutPutTx (val owner: String, val amount: UInt)
}

class Mempool {
    val transactions = ArrayList<Transaction>()
}