package uz.texnopos.elektrolife.data.retrofit

import io.reactivex.rxjava3.core.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import uz.texnopos.elektrolife.data.model.prefix.PrefixResponse

interface PrefixApi {
    @GET("/api/domains/{prefix}")
    fun checkPrefix(
        @Path("prefix") prefix: String
    ): Observable<Response<PrefixResponse>>
}
