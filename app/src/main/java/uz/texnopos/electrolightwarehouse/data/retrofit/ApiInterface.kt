package uz.texnopos.electrolightwarehouse.data.retrofit

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.model.Client
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.data.model.warehouse.WarehouseProduct
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

    @POST("api/client/payment")
    fun payment(
        @Header("Authorization") token: String,
        @Body newPayment: NewPayment
    ): Observable<GenericResponse<String>>

    @GET("api/orders")
    fun getOrders(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<Sales>>>

    @GET("api/warehouse")
    fun getProductsFromWarehouse(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<WarehouseProduct>>>

//    @GET("api/categories")
//    fun getCategories(
//        @Header("Authorization") token: String
//    ): Observable<GenericResponse<List<CatalogCategory>>>
//
//    @GET("api/products")
//    fun getCategoriesById(
//        @Header("Authorization") token: String,
//        @Query("category") id: Int):Observable<GenericResponse<List<Product>>>
}