package uz.texnopos.electrolightwarehouse.data.retrofit

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.*
import uz.texnopos.electrolightwarehouse.data.ClientInfo
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.model.CatalogCategory
import uz.texnopos.electrolightwarehouse.data.model.Client
import uz.texnopos.electrolightwarehouse.data.model.Products
import uz.texnopos.electrolightwarehouse.data.model.Sales
import uz.texnopos.electrolightwarehouse.data.model.signin.SignInPayload
import uz.texnopos.electrolightwarehouse.data.model.signin.SignInPost
import uz.texnopos.electrolightwarehouse.data.model.warehouse.WarehouseProduct
import uz.texnopos.electrolightwarehouse.data.newCategory.CategoryId
import uz.texnopos.electrolightwarehouse.data.newCategory.NewCategory
import uz.texnopos.electrolightwarehouse.data.newClient.ClientId
import uz.texnopos.electrolightwarehouse.data.newClient.RegisterClient
import uz.texnopos.electrolightwarehouse.data.newPayment.NewPayment
import uz.texnopos.electrolightwarehouse.data.newProduct.Categories
import uz.texnopos.electrolightwarehouse.data.newProduct.Product
import uz.texnopos.electrolightwarehouse.data.newProduct.ProductId

interface ApiInterface {

    @POST("api/register")
    fun registerNewClient(
        @Header("Authorization") token: String,
        @Body registerClient: RegisterClient
    ): Observable<GenericResponse<ClientId>>

    @GET("api/clients")
    fun getClients(
        @Header("Authorization") token: String,@Query("search") search: String
    ): Observable<GenericResponse<List<ClientInfo>>>

    @GET("api/clients")
    fun getClients(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<Client>>>

    @POST("api/client/payment")
    fun payment(
        @Header("Authorization") token: String,
        @Body newPayment: NewPayment
    ): Observable<GenericResponse<List<String>>>

    @GET("api/orders")
    fun getOrders(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<Sales>>>

    @GET("api/warehouse")
    fun getProductsFromWarehouse(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<WarehouseProduct>>>

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
    fun getCategoriesById(
        @Header("Authorization") token: String,
        @Query("category") id: Int):Observable<GenericResponse<Products>>

    @GET("api/products")
    fun getProduct(
        @Header("Authorization") token: String,
        @Query("search") name:String): Observable<GenericResponse<Products>>

    @POST("api/products")
    fun createdProduct(
        @Header("Authorization") token: String,
        @Body product: Product
    ): Observable<GenericResponse<ProductId>>

    @GET("api/categories")
    fun getCategories(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<Categories>>>
}