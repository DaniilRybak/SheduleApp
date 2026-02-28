package com.example.scheduleapp.data.model

import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonTransformingSerializer

object EventRoomsSerializer : JsonTransformingSerializer<List<HrefWrapper>>(ListSerializer(HrefWrapper.serializer())) {
    // If the format is a JSON object, deserialize it as a list of one element.
    override fun transformDeserialize(element: JsonElement): JsonElement =
        if (element is JsonObject) JsonArray(listOf(element)) else element
}
