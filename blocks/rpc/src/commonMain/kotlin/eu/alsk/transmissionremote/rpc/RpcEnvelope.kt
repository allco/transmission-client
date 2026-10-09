package eu.alsk.transmissionremote.rpc

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/** Builds the request body and reads the response body for one [RpcFormat]. */
internal object RpcEnvelope {

    /** Builds the body of a request. See section 3 of the RPC reference. */
    fun encode(format: RpcFormat, method: String, params: JsonObject, id: Long): String {
        val body = when (format) {
            RpcFormat.JsonRpc2 -> buildJsonObject {
                put("jsonrpc", "2.0")
                put("method", method)
                put("params", params)
                put("id", id)
            }
            RpcFormat.Legacy -> buildJsonObject {
                put("method", method)
                put("arguments", params)
                put("tag", id)
            }
        }
        return body.toString()
    }

    /**
     * Returns the result of a response, or throws [RpcException.ServerError] when the server sent
     * an RPC error.
     */
    fun decode(format: RpcFormat, text: String): JsonObject {
        val root = try {
            Json.parseToJsonElement(text).jsonObject
        } catch (e: IllegalArgumentException) {
            throw RpcException.InvalidResponse("The response is not a JSON object", e)
        }
        return when (format) {
            RpcFormat.JsonRpc2 -> decodeJsonRpc2(root)
            RpcFormat.Legacy -> decodeLegacy(root)
        }
    }

    private fun decodeJsonRpc2(root: JsonObject): JsonObject {
        val error = root["error"]
        if (error != null && error != JsonNull) {
            val errorObject = error.jsonObject
            throw RpcException.ServerError(
                code = errorObject["code"]?.jsonPrimitive?.intOrNull,
                message = errorObject["message"]?.jsonPrimitive?.contentOrNull ?: "RPC error",
                details = errorObject["data"]?.let(::errorString),
            )
        }
        return root["result"].asObjectOrEmpty()
    }

    private fun decodeLegacy(root: JsonObject): JsonObject {
        val result = root["result"]?.jsonPrimitive?.contentOrNull
            ?: throw RpcException.InvalidResponse("The response has no \"result\"")
        // The legacy format puts an error message in "result". "success" means no error.
        if (result != "success") throw RpcException.ServerError(code = null, message = result)
        return root["arguments"].asObjectOrEmpty()
    }

    private fun errorString(data: JsonElement): String? =
        (data as? JsonObject)?.get("error_string")?.let { (it as? JsonPrimitive)?.contentOrNull }

    private fun JsonElement?.asObjectOrEmpty(): JsonObject = this as? JsonObject ?: JsonObject(emptyMap())
}
