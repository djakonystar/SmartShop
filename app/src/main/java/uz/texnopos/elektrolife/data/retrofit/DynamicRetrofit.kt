package uz.texnopos.elektrolife.data.retrofit

import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class DynamicRetrofit {
    companion object {
        private const val TIME_OUT = 50L
        private var BASE_URL = "https://sinyor.texnopos.site"
    }

    private fun provideGson() = GsonBuilder().setLenient().create()

    private fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIME_OUT, unit = TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    private fun buildApi() = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(provideGson()))
        .client(provideOkHttpClient())
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .build()
        .create(ApiInterface::class.java)

    var api: ApiInterface = buildApi()
        private set

    fun setUrl(url: String) {
        if (BASE_URL != url) BASE_URL = url
        api = buildApi()
    }
}
