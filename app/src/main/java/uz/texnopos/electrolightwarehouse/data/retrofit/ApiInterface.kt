package uz.texnopos.electrolightwarehouse.data.retrofit

import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import uz.texnopos.electrolightwarehouse.data.ClientInfo
import uz.texnopos.electrolightwarehouse.data.GenericResponse
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
    ): Observable<GenericResponse<List<ClientInfo>>>

    @POST("api/client/payment")
    fun payment(@Header("Authorization") token: String,@Body newPayment: NewPayment ):  Observable<GenericResponse<String>>
//    @GET("api/orders")
//    fun getOrders(
//        @Header("Authorization") token: String
//    ): Observable<GenericResponse<List<Sales>>>

}