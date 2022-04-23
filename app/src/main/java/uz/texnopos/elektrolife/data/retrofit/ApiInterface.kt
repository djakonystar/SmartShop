package uz.texnopos.elektrolife.data.retrofit

import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.*
import uz.texnopos.elektrolife.core.extensions.*
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.PagingResponse
import uz.texnopos.elektrolife.data.model.category.CategoryResponse
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.data.model.clients.ClientPayment
import uz.texnopos.elektrolife.data.model.currency.Currency
import uz.texnopos.elektrolife.data.model.finance.Cashier
import uz.texnopos.elektrolife.data.model.finance.Finance
import uz.texnopos.elektrolife.data.model.finance.FinancePost
import uz.texnopos.elektrolife.data.model.newcategory.CategoryPost
import uz.texnopos.elektrolife.data.model.newclient.ClientId
import uz.texnopos.elektrolife.data.model.newpayment.NewPayment
import uz.texnopos.elektrolife.data.model.newproduct.Transaction
import uz.texnopos.elektrolife.data.model.newsale.Order
import uz.texnopos.elektrolife.data.model.qrcode.ProductResponse
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.data.model.sales.BasketResponse
import uz.texnopos.elektrolife.data.model.signin.SignIn
import uz.texnopos.elektrolife.data.model.signin.SignInResponse
import uz.texnopos.elektrolife.data.model.warehouse.WarehouseItem

interface ApiInterface {

    @POST("api/register/client")
    fun addNewClient(
        @Header("Authorization") token: String,
        @Body client: newClient
    ): Observable<GenericResponse<ClientId>>

    @GET("api/clients")
    fun getClients(
        @Header("Authorization") token: String,
        @Query("search") search: String
    ): Observable<GenericResponse<List<Client>>>

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
    ): Observable<GenericResponse<List<Client>>>

    @POST("api/client/payment")
    fun payment(
        @Header("Authorization") token: String,
        @Body newPayment: NewPayment
    ): Observable<GenericResponse<List<String>>>

    @GET("api/baskets")
    fun getBaskets(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<BasketResponse>>>

    @GET("api/baskets")
    fun getBaskets(
        @Header("Authorization") token: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<BasketResponse>>>

    @GET("api/baskets")
    fun searchForBaskets(
        @Header("Authorization") token: String,
        @Query("search") searchValue: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<BasketResponse>>>

    @GET("api/baskets")
    fun searchForBaskets(
        @Header("Authorization") token: String,
        @Query("search") searchValue: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<BasketResponse>>>

    @GET("api/baskets")
    fun filterBaskets(
        @Header("Authorization") token: String,
        @Query("filter") typeOfPayment: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<BasketResponse>>>

    @GET("api/baskets")
    fun filterBaskets(
        @Header("Authorization") token: String,
        @Query("filter") typeOfPayment: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<BasketResponse>>>

    @GET("api/baskets")
    fun basketsOfUser(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<BasketResponse>>>

    @GET("api/baskets")
    fun basketsOfUser(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<BasketResponse>>>

    @GET("api/orders")
    fun getOrders(
        @Header("Authorization") token: String,
        @Query("basket_id") basketId: Int
    ): Observable<GenericResponse<List<orderBasket>>>

    @GET("api/warehouse")
    fun warehouseProducts(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<WarehouseItem>>>

    @GET("api/warehouse")
    fun warehouseProducts(
        @Header("Authorization") token: String,
        @Query("search") search: String
    ): Observable<GenericResponse<List<WarehouseItem>>>

    @POST("api/login")
    fun signIn(
        @Body signIn: SignIn
    ): Observable<GenericResponse<SignInResponse>>

    @POST("api/categories")
    fun createCategory(
        @Header("Authorization") token: String,
        @Body categoryPost: CategoryPost
    ): Observable<GenericResponse<newCategory>>

    @GET("api/categories")
    fun getCategories(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<CategoryResponse>>>

    @GET("api/products")
    fun getProductsByCategoryId(
        @Header("Authorization") token: String,
        @Query("category") categoryId: Int
    ): Observable<GenericResponse<List<newSaleProduct>>>

    @GET("api/products")
    fun getProducts(
        @Header("Authorization") token: String
    ): Observable<GenericResponse<List<newSaleProduct>>>

    @GET("api/products")
    fun getProducts(
        @Header("Authorization") token: String,
        @Query("search") name: String
    ): Observable<GenericResponse<List<newSaleProduct>>>

    @POST("api/product")
    fun createProduct(
        @Header("Authorization") token: String,
        @Body product: creatingProduct
    ): Observable<GenericResponse<createdProduct>>

    @POST("api/order")
    fun order(
        @Header("Authorization") token: String,
        @Body order: Order
    ): Observable<GenericResponse<Any>>

    @GET("api/currency")
    fun getCurrency(
        @Header("Authorization") token: String
    ): Observable<Response<GenericResponse<List<Currency>>>>

    @GET("api/qrcode/read")
    fun scanCode(
        @Header("Authorization") token: String,
        @Query("type") type: String,
        @Query("uuid") code: String
    ): Observable<GenericResponse<ProductResponse>>

    /**
     * Finance: Get cashbox balance and profit in date range [from] - [to]
     */
    @GET("api/casheir")
    fun getCashier(
        @Header("Authorization") token: String,
        @Query("to") from: String,
        @Query("do") to: String
    ): Observable<GenericResponse<Cashier>>

    /**
     * Finance: Add new finance detail
     */
    @POST("api/consumption/new")
    fun addFinanceDetail(
        @Header("Authorization") token: String,
        @Body finance: FinancePost
    ): Observable<GenericResponse<List<String>>>

    /**
     * Finance: Get finance details between two dates.
     * [from] - start date;
     * [to] - end date;
     * [type] - type of finance detail
     */
    @GET("api/consumption")
    fun getFinanceDetails(
        @Header("Authorization") token: String,
        @Query("to") from: String,
        @Query("do") to: String,
        @Query("type") type: String
    ): Observable<GenericResponse<List<Finance>>>

    /**
     * Get payment history of client
     */
    @GET("api/client/payment/history")
    fun getClientPayments(
        @Header("Authorization") token: String,
        @Query("client_id") clientId: Int
    ): Observable<GenericResponse<List<ClientPayment>>>

    /**
     * Add-on new quantity to existing product (Transaction)
     */
    @POST("api/warehouse")
    fun newTransaction(
        @Header("Authorization") token: String,
        @Body transaction: Transaction
    ): Observable<GenericResponse<List<Any>>>
}
