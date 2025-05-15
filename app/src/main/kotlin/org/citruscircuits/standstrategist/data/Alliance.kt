package org.citruscircuits.standstrategist.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable(with = AllianceSerializer::class)
/**
 * @property asString The string name for this alliance, as it will appear in JSON.
 */
enum class Alliance(val asString: String, val readable: String) {
    BLUE(asString = "blue", readable = "Blue"),
    RED(asString = "red", readable = "Red")
}

/**
 * encodes/decodes a alliance to/from a JSON file
 */
class AllianceSerializer : KSerializer<Alliance> {
    override val descriptor = PrimitiveSerialDescriptor("Alliance", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Alliance) = encoder.encodeString(value.asString)

    override fun deserialize(decoder: Decoder) =
        decoder.decodeString().let { str -> Alliance.entries.firstOrNull { it.asString == str } ?: Alliance.BLUE }
}
