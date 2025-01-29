package com.github.michaeltomlinsontuks.bitburnerplugin

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import java.net.ConnectException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

class BitburnerApiService {
    companion object {
        private const val REQUEST_TIMEOUT = 5000L
        private const val CONNECT_TIMEOUT = 5000L
        
        fun createHttpClient(): HttpClient {
            return HttpClient {
                install(HttpTimeout) {
                    requestTimeoutMillis = REQUEST_TIMEOUT
                    connectTimeoutMillis = CONNECT_TIMEOUT
                }
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
            }
        }
    }

    private val client = createHttpClient()

    @Serializable
    data class JsonRpcRequest(
        val jsonrpc: String = "2.0", 
        val id: Int, 
        val method: String, 
        val params: JsonElement? = null
    )
    
    @Serializable
    data class JsonRpcResponse<T>(
        val jsonrpc: String, 
        val id: Int, 
        val result: T?, 
        val error: String?
    )

    private suspend inline fun <reified T> sendRequest(request: JsonRpcRequest, authToken: String): JsonRpcResponse<T> {
        return try {
            val response: HttpResponse = client.post(GameConfig.getServerUrl()) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $authToken")
                setBody(request)
            }
            response.body()
        } catch (e: ConnectException) {
            JsonRpcResponse("2.0", request.id, null, "Connection failed: ${e.message}")
        } catch (e: Exception) {
            JsonRpcResponse("2.0", request.id, null, "Request failed: ${e.message}")
        }
    }

    fun pushFile(id: Int, filename: String, content: String, server: String, authToken: String): JsonRpcResponse<Boolean> = runBlocking {
        if (!GameConfig.isValidFileExtension(filename)) {
            return@runBlocking JsonRpcResponse("2.0", id, null, "Invalid file extension. Supported: ${GameConfig.VALID_FILE_EXTENSIONS}")
        }
        val params = JsonObject(mapOf(
            "filename" to JsonPrimitive(filename),
            "content" to JsonPrimitive(content),
            "server" to JsonPrimitive(server)
        ))
        sendRequest(JsonRpcRequest(id = id, method = "pushFile", params = params), authToken)
    }

    fun getFile(id: Int, filename: String, server: String, authToken: String): JsonRpcResponse<String> = runBlocking {
        val params = JsonObject(mapOf(
            "filename" to JsonPrimitive(filename),
            "server" to JsonPrimitive(server)
        ))
        sendRequest(JsonRpcRequest(id = id, method = "getFile", params = params), authToken)
    }

    suspend fun deleteFile(id: Int, filename: String, server: String, authToken: String): JsonRpcResponse<Boolean> {
        val params = JsonObject(mapOf(
            "filename" to JsonPrimitive(filename),
            "server" to JsonPrimitive(server)
        ))
        return sendRequest(JsonRpcRequest(id = id, method = "deleteFile", params = params), authToken)
    }

    suspend fun getFileNames(id: Int, server: String, authToken: String): JsonRpcResponse<List<String>> {
        val params = JsonObject(mapOf(
            "server" to JsonPrimitive(server)
        ))
        return sendRequest(JsonRpcRequest(id = id, method = "getFileNames", params = params), authToken)
    }

    suspend fun getAllFiles(id: Int, server: String, authToken: String): JsonRpcResponse<Map<String, String>> {
        val params = JsonObject(mapOf(
            "server" to JsonPrimitive(server)
        ))
        return sendRequest(JsonRpcRequest(id = id, method = "getAllFiles", params = params), authToken)
    }

    suspend fun calculateRam(id: Int, filename: String, server: String, authToken: String): JsonRpcResponse<Double> {
        if (!GameConfig.isValidFileExtension(filename)) {
            return JsonRpcResponse("2.0", id, null, "Invalid file extension. Supported: ${GameConfig.VALID_FILE_EXTENSIONS}")
        }
        val params = JsonObject(mapOf(
            "filename" to JsonPrimitive(filename),
            "server" to JsonPrimitive(server)
        ))
        return sendRequest(JsonRpcRequest(id = id, method = "calculateRam", params = params), authToken)
    }

    suspend fun getDefinitionFile(id: Int, authToken: String): JsonRpcResponse<String> {
        return sendRequest(JsonRpcRequest(id = id, method = "getDefinitionFile"), authToken)
    }
}