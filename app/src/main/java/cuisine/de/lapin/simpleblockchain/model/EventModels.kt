package cuisine.de.lapin.simpleblockchain.model

import com.google.gson.annotations.SerializedName

class BabyEvent(
    @SerializedName("type")
    val type: BabyEventType,
    @SerializedName("eventTime")
    val eventTime: Long,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("comment")
    val comment: String
)

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