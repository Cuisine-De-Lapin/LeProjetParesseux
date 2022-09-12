package cuisine.de.lapin.simpleblockchain.model

import com.google.gson.annotations.SerializedName
import cuisine.de.lapin.simpleblockchain.blockchain.model.Event

class BabyEvent(
    @SerializedName("type")
    val type: BabyEventType,
    @SerializedName("eventTime")
    val eventTime: Long,
    detail: String
) : Event(detail)

enum class BabyEventType {
    @SerializedName("Pee")
    PEE,

    @SerializedName("Poop")
    POOP,

    @SerializedName("Vomit")
    VOMIT,

    @SerializedName("Meal")
    MEAL,

    @SerializedName("Weight")
    WEIGHT,

    @SerializedName("BodyHeat")
    BODY_HEAT,

    @SerializedName("Other")
    OTHER
}