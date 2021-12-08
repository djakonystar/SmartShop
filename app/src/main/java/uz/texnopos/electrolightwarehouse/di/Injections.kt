package uz.texnopos.electrolightwarehouse.di

import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface
import uz.texnopos.electrolightwarehouse.settings.Settings
import uz.texnopos.electrolightwarehouse.ui.newpayment.NewPaymentViewModel
import uz.texnopos.electrolightwarehouse.ui.client.ClientsAdapter
import uz.texnopos.electrolightwarehouse.ui.sales.SalesAdapter
import uz.texnopos.electrolightwarehouse.ui.sales.SalesViewModel
import uz.texnopos.electrolightwarehouse.ui.sales.detail.SalesDetailAdapter
import uz.texnopos.electrolightwarehouse.ui.warehouse.WarehouseAdapter
import java.util.concurrent.TimeUnit

private const val baseUrl = "https://"
private const val timeOut = 50L

val networkModule = module {
    single {
        GsonBuilder().setLenient().create()
    }

    single {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(timeout = timeOut, TimeUnit.SECONDS)
            .readTimeout(timeout = timeOut, TimeUnit.SECONDS)
            .writeTimeout(timeout = timeOut, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(get()))
            .client(get())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(ApiInterface::class.java)
    }
}

val helperModule = module {
    single { Settings(androidApplication().applicationContext) }
}

val viewModelModule = module {
    viewModel { NewPaymentViewModel(get()) }
    viewModel { SalesViewModel()}
}

val adapterModule = module {
    single { ClientsAdapter() }
    single { SalesAdapter() }
    single { SalesDetailAdapter() }
    single { WarehouseAdapter() }
}
