package uz.texnopos.elektrolife.data.retrofit

import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.*
import uz.texnopos.elektrolife.core.extensions.newSaleProduct
import uz.texnopos.elektrolife.core.extensions.orderBasket
import uz.texnopos.elektrolife.core.extensions.warehouseProduct
import uz.texnopos.elektrolife.data.model.GenericResponse
import uz.texnopos.elektrolife.data.model.PagingResponse
import uz.texnopos.elektrolife.data.model.clients.Client
import uz.texnopos.elektrolife.data.model.clients.ClientPayment
import uz.texnopos.elektrolife.data.model.currency.Currency
import uz.texnopos.elektrolife.data.model.finance.Cashier
import uz.texnopos.elektrolife.data.model.finance.Finance
import uz.texnopos.elektrolife.data.model.finance.FinancePost
import uz.texnopos.elektrolife.data.model.newcategory.CategoryId
import uz.texnopos.elektrolife.data.model.newcategory.NewCategory
import uz.texnopos.elektrolife.data.model.newclient.ClientId
import uz.texnopos.elektrolife.data.model.newclient.RegisterClient
import uz.texnopos.elektrolife.data.model.newpayment.NewPayment
import uz.texnopos.elektrolife.data.model.newproduct.Categories
import uz.texnopos.elektrolife.data.model.newproduct.Product
import uz.texnopos.elektrolife.data.model.newproduct.ProductId
import uz.texnopos.elektrolife.data.model.newproduct.Transaction
import uz.texnopos.elektrolife.data.model.newsale.CatalogCategory
import uz.texnopos.elektrolife.data.model.newsale.Order
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.data.model.signin.SignIn
import uz.texnopos.elektrolife.data.model.signin.SignInResponse
import uz.texnopos.elektrolife.data.model.warehouse.WarehouseItem

interface ApiInterface {

    @POST("api/register/client")
    fun registerNewClient(
        @Header("Authorization") token: String,
        @Body registerClient: RegisterClient
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
    ): Observable<GenericResponse<PagingResponse<List<Basket>>>>

    @GET("api/baskets")
    fun getBaskets(
        @Header("Authorization") token: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<List<Basket>>>>

    @GET("api/baskets")
    fun searchForBaskets(
        @Header("Authorization") token: String,
        @Query("search") searchValue: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<List<Basket>>>>

    @GET("api/baskets")
    fun searchForBaskets(
        @Header("Authorization") token: String,
        @Query("search") searchValue: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<List<Basket>>>>

    @GET("api/baskets")
    fun filterBaskets(
        @Header("Authorization") token: String,
        @Query("filter") typeOfPayment: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<List<Basket>>>>

    @GET("api/baskets")
    fun filterBaskets(
        @Header("Authorization") token: String,
        @Query("filter") typeOfPayment: String,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<List<Basket>>>>

    @GET("api/baskets")
    fun basketsOfUser(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<List<Basket>>>>

    @GET("api/baskets")
    fun basketsOfUser(
        @Header("Authorization") token: String,
        @Query("user_id") userId: Int,
        @Query("from") from: String,
        @Query("to") to: String,
        @Query("page") page: Int
    ): Observable<GenericResponse<PagingResponse<List<Basket>>>>

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
    ): Observable<GenericResponse<List<newSaleProduct>>>

    @GET("api/products")
    fun getProduct(
        @Header("Authorization") token: String,
        @Query("search") name: String,
        @Query("limit") limit: Int
    ): Observable<GenericResponse<List<newSaleProduct>>>

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

    @GET("api/currency")
    fun getCurrency(
        @Header("Authorization") token: String
    ): Observable<Response<GenericResponse<List<Currency>>>>

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
