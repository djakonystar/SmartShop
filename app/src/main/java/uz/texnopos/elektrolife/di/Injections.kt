package uz.texnopos.elektrolife.di

import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings
import uz.texnopos.elektrolife.ui.client.ClientsAdapter
import uz.texnopos.elektrolife.ui.client.ClientsViewModel
import uz.texnopos.elektrolife.ui.main.MainViewModel
import uz.texnopos.elektrolife.ui.newcategory.NewCategoryViewModel
import uz.texnopos.elektrolife.ui.newclient.NewClientViewModel
import uz.texnopos.elektrolife.ui.newpayment.NewPaymentViewModel
import uz.texnopos.elektrolife.ui.newproduct.NewProductViewModel
import uz.texnopos.elektrolife.ui.newsale.CategoriesViewModel
import uz.texnopos.elektrolife.ui.newsale.NewSaleProductAdapter
import uz.texnopos.elektrolife.ui.newsale.order.OrderAdapter
import uz.texnopos.elektrolife.ui.newsale.order.OrderViewModel
import uz.texnopos.elektrolife.ui.sales.SalesAdapter
import uz.texnopos.elektrolife.ui.sales.SalesViewModel
import uz.texnopos.elektrolife.ui.sales.detail.SalesDetailAdapter
import uz.texnopos.elektrolife.ui.signin.SignInViewModel
import uz.texnopos.elektrolife.ui.warehouse.WarehouseAdapter
import uz.texnopos.elektrolife.ui.warehouse.WarehouseViewModel
import java.util.concurrent.TimeUnit

private const val baseUrl = "http://electro-life.texnopos.site/"
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
    viewModel { CategoriesViewModel(get(), get()) }
    viewModel { NewPaymentViewModel(get(), get()) }
    viewModel { SalesViewModel(get(), get())}
    viewModel { NewCategoryViewModel(get(), get()) }
    viewModel { NewProductViewModel(get(),get()) }
    viewModel { ClientsViewModel(get(), get()) }
    viewModel { WarehouseViewModel(get(), get()) }
    viewModel { SignInViewModel(get(), get()) }
    viewModel { NewClientViewModel(get(), get()) }
    viewModel { OrderViewModel(get(),get()) }
    viewModel { MainViewModel(get(), get()) }
}

val adapterModule = module {
    single { ClientsAdapter() }
    single { SalesAdapter() }
    single { SalesDetailAdapter() }
    single { WarehouseAdapter() }
    single { NewSaleProductAdapter() }
    single { OrderAdapter() }
}
