package uz.texnopos.elektrolife.data.retrofit

import io.reactivex.rxjava3.core.Observable
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import uz.texnopos.elektrolife.data.model.newproduct.Image

interface ImageApiInterface {
    @Multipart
    @POST("v1_1/{cloud_name}/image/upload")
    fun uploadImage(
        @Path("cloud_name") cloudName: String,
        @Part image: MultipartBody.Part,
        @Part preset: MultipartBody.Part
    ): Observable<Image>
}