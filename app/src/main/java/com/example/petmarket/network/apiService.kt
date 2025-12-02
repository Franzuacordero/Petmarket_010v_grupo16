package com.example.petmarket.network

import com.example.petmarket.model.*
import retrofit2.http.*

interface ApiService {

    //  productos
    @GET("productos")
    suspend fun getProductos(): List<Producto>

    @POST("productos")
    suspend fun addProducto(@Body p: Producto): Producto

    @PUT("productos/{id}")
    suspend fun updateProducto(@Path("id") id: String, @Body p: Producto): Producto

    @DELETE("productos/{id}")
    suspend fun deleteProducto(@Path("id") id: String)


    //  pedidos
    @POST("pedidos")
    suspend fun enviarPedido(@Body pedido: Pedido): Pedido

    @GET("pedidos")
    suspend fun getPedidos(): List<Pedido>


    // foro
    @GET("foro")
    suspend fun getForos(): List<Foro>

    @POST("foro")
    suspend fun addForo(@Body foro: Foro): Foro

    @DELETE("foro/{id}")
    suspend fun deleteForo(@Path("id") id: String)

    @GET("comentarios")
    suspend fun getComentarios(): List<Comentario>

    @POST("comentarios")
    suspend fun addComentario(@Body comentario: Comentario): Comentario



    // servicios
    @GET("servicios")
    suspend fun obtenerServicios(): List<Servicio>

    @POST("servicios")
    suspend fun crearServicio(@Body servicio: Servicio): Servicio



    //  RESERVAS
    @GET("reservas")
    suspend fun obtenerReservas(): List<Reserva>

    @POST("reservas")
    suspend fun crearReserva(@Body reserva: Reserva): Reserva

    @GET("https://dog.ceo/api/breeds/image/random")
    suspend fun getRandomDog(): RandomDogResponse

}
