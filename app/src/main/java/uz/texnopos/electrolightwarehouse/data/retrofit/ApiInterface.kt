package uz.texnopos.electrolightwarehouse.data.retrofit

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*
import uz.texnopos.electrolightwarehouse.data.model.clients.ClientInfo
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.model.clients.Client
import uz.texnopos.electrolightwarehouse.data.model.sales.Sales
import uz.texnopos.electrolightwarehouse.data.model.signin.SignInPayload
import uz.texnopos.electrolightwarehouse.data.model.signin.SignInPost
import uz.texnopos.electrolightwarehouse.data.model.newcategory.CategoryId
import uz.texnopos.electrolightwarehouse.data.model.newcategory.NewCategory
import uz.texnopos.electrolightwarehouse.data.model.newclient.ClientId
import uz.texnopos.electrolightwarehouse.data.model.newclient.RegisterClient
import uz.texnopos.electrolightwarehouse.data.model.newpayment.NewPayment
import uz.texnopos.electrolightwarehouse.data.model.newproduct.Categories
import uz.texnopos.electrolightwarehouse.data.model.newproduct.Product
import uz.texnopos.electrolightwarehouse.data.model.newproduct.ProductId
import uz.texnopos.electrolightwarehouse.data.model.newsale.CatalogCategory
import uz.texnopos.electrolightwarehouse.data.model.newsale.Order
import uz.texnopos.electrolightwarehouse.data.model.newsale.Products

interface ApiInterface {

    @POST("api/register")
    fun registerNewClient(
        @Header("Authorization") token: String,
        @Body registerClient: RegisterClient
    ): Observable<GenericResponse<ClientId>>

    @GET("api/clients")
    fun getClients(
        @Header("Authorization") token: String,
        @Query("search") search: String
    ): Observable<GenericResponse<List<ClientInfo>>>

    @GET("api/clients")
    fun getClients(
        @Header("Authorization") token: String,
        @Query("limit") limit: Int,
        @Query("page") page: Int,
        @Query("search") search: String
    ): Observable<GenericResponse<List<Client>>>

    @GET("api/clients")
    fun getClientsByName(
        @Header("Authorization") token: String,
        @Query("search") search: String
    ): Observable<GenericResponse<List<ClientInfo>>>


    @POST("api/client/payment")
    fun payment(
        @Header("Authorization") token: String,
        @Body newPayment: NewPayment
    ): Observable<GenericResponse<List<String>>>

    @GET("api/orders")
    fun getOrders(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<Sales>>>

    @GET("api/orders")
    fun getOrdersByDate(
        @Header("Authorization") token: String,
        @Query("to") from: String,
        @Query("do") to: String
    ): Observable<GenericResponse<List<Sales>>>

    @GET("api/warehouse")
    fun getProductsFromWarehouse(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<uz.texnopos.electrolightwarehouse.data.model.warehouse.Product>>>

    @POST("api/login")
    fun signIn(
        @Body signInPost: SignInPost
    ): Observable<GenericResponse<SignInPayload>>

    @POST("api/categories")
    fun createdCategory(
        @Header("Authorization") token: String,
        @Body newCategory: NewCategory
    ): Observable<GenericResponse<CategoryId>>

    @GET("api/categories")
    fun getCatalogCategories(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<CatalogCategory>>>

    @GET("api/products")
    fun getProductsByCategoryId(
        @Header("Authorization") token: String,
        @Query("category") categoryId: Int
    ): Observable<GenericResponse<Products>>

    @GET("api/products")
    fun getProduct(
        @Header("Authorization") token: String,
        @Query("search") name: String,
        @Query("limit") limit: Int
    ): Observable<GenericResponse<Products>>

    @POST("api/products")
    fun createdProduct(
        @Header("Authorization") token: String,
        @Body product: Product
    ): Observable<GenericResponse<ProductId>>

    @GET("api/categories")
    fun getCategories(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<Categories>>>

    @POST("api/order")
    fun order(
        @Header("Authorization") token: String,
        @Body order: Order
    ): Observable<GenericResponse<Any>>
}