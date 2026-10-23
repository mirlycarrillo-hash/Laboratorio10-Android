package com.example.lab10.data

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProductoApiService {

    @GET("producto/")
    suspend fun getProductos(): List<ProductoModel>

    @POST("producto/")
    suspend fun addProducto(@Body producto: ProductoModel): ProductoModel

    @PUT("producto/{id}/")
    suspend fun updateProducto(@Path("id") id: Int, @Body producto: ProductoModel): ProductoModel

    @DELETE("producto/{id}/")
    suspend fun deleteProducto(@Path("id") id: Int): Unit
}