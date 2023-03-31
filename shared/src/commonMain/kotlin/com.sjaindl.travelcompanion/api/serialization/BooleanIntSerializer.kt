package com.sjaindl.travelcompanion.api.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object BooleanIntSerializer : KSerializer<BooleanInt> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("BooleanInt", PrimitiveKind.BOOLEAN)

    override fun serialize(encoder: Encoder, value: BooleanInt) {
        encoder.encodeBoolean(value = value.value)
    }

    override fun deserialize(decoder: Decoder): BooleanInt {
        return if (decoder.decodeInt() == 0) BooleanInt(value = false) else BooleanInt(value = true)
    }
}
