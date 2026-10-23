package com.example.lab10.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PostApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostModel>

    @POST("posts")
    suspend fun createPost(@Body post: PostModel): Response<PostModel>

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Int, @Body post: PostModel): Response<PostModel>

    @DELETE("posts/{id}")
    suspend fun deletePost(@Path("id") id: Int): Response<Unit>
}