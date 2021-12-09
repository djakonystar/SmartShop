package uz.texnopos.electrolightwarehouse.data.retrofit

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*
import uz.texnopos.electrolightwarehouse.data.ClientInfo
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.model.CatalogCategory
import uz.texnopos.electrolightwarehouse.data.model.Client
import uz.texnopos.electrolightwarehouse.data.model.Order
import uz.texnopos.electrolightwarehouse.data.model.Products
import uz.texnopos.electrolightwarehouse.data.newClient.ClientId
import uz.texnopos.electrolightwarehouse.data.newClient.RegisterClient
import uz.texnopos.electrolightwarehouse.data.newPayment.NewPayment

interface ApiInterface {

    @POST("api/register")
    fun registerNewClient(
        @Header("Authorization") token: String,
        @Body registerClient: RegisterClient
    ): Observable<GenericResponse<ClientId>>

    @GET("api/clients")
    fun getClients(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<Client>>>

    @GET("api/clients")
    fun getClientsByName(
        @Header("Authorization") token: String,@Query("search") search: String
    ): Observable<GenericResponse<List<ClientInfo>>>


    @POST("api/client/payment")
    fun payment(
        @Header("Authorization") token: String,
        @Body newPayment: NewPayment
    ): Observable<GenericResponse<String>>

//    @GET("api/orders")
//    fun getOrders(
//        @Header("Authorization") token: String
//    ): Observable<GenericResponse<List<Sales>>>

    @GET("api/categories")
    fun getCategories(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<CatalogCategory>>>

    @GET("api/products")
    fun getCategoriesById(
        @Header("Authorization") token: String,
        @Query("category") id: Int):Observable<GenericResponse<Products>>

    @GET("api/products")
    fun getProduct(
        @Header("Authorization") token: String,
        @Query("search") name:String): Observable<GenericResponse<Products>>

    @POST("api/order")
    fun order(
        @Header("Authorization") token: String,
        @Body order:Order
    ):Observable<GenericResponse<Any>>
}