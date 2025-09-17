package com.example.firstproject.Api

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ApiService {
    private val client = OkHttpClient()
    private val token = "//此处为在API获取的token密钥"
    private val baseUrl = "https://v3.alapi.cn/api/comment"

    suspend fun fetchSongData(): Result<SongData> = suspendCoroutine { continuation ->
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val jsonObject = JSONObject()
        jsonObject.put("token", token)

        val requestBody = jsonObject.toString().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(baseUrl)
            .post(requestBody)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resume(Result.failure(e))
            }

            override fun onResponse(call: Call, response: Response) {
                try {
                    val responseData = response.body?.string()
                    if (response.isSuccessful && responseData != null) {
                        try {
                            val jsonObject = JSONObject(responseData)
                            if (jsonObject.getInt("code") == 200) {
                                val dataObject = jsonObject.getJSONObject("data")
                                val songId = dataObject.getLong("song_id").toString()
                                val title = dataObject.getString("title")
                                val author = dataObject.getString("author")
                                val imageUrl = dataObject.getString("image")
                                val commentContent = dataObject.getString("comment_content")
                                val commentPublishedDate = dataObject.getString("comment_published_date")

                                val songData = SongData(
                                    songId = songId,
                                    title = title,
                                    author = author,
                                    imageUrl = imageUrl,
                                    commentContent = commentContent,
                                    commentPublishedDate = commentPublishedDate
                                )

                                continuation.resume(Result.success(songData))
                            } else {
                                val msg = jsonObject.getString("message")
                                continuation.resume(Result.failure(Exception("服务器返回错误: $msg")))
                            }
                        } catch (e: Exception) {
                            continuation.resume(Result.failure(e))
                        }
                    } else {
                        continuation.resume(Result.failure(Exception("服务器返回错误: ${response.code}")))
                    }
                } finally {
                    response.close()
                }
            }
        })
    }
}
