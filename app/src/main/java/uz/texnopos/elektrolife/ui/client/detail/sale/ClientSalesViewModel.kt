package uz.texnopos.elektrolife.ui.client.detail.sale

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.PagingResponse
import uz.texnopos.elektrolife.data.model.sales.Basket
import uz.texnopos.elektrolife.data.model.sales.BasketResponse
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class ClientSalesViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableBaskets: MutableLiveData<Resource<PagingResponse<BasketResponse>>> =
        MutableLiveData()
    val baskets: LiveData<Resource<PagingResponse<BasketResponse>>> = mutableBaskets

    fun basketsOfUser(userId: Int) {
        mutableBaskets.value = Resource.loading()
        compositeDisposable.add(
            api.basketsOfUser(token = "Bearer ${settings.token}", userId = userId, page = 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableBaskets.value = Resource.success(response.payload)
                        } else {
                            mutableBaskets.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableBaskets.value = Resource.error(error.message)
                    }
                )
        )
    }

    fun basketsOfUser(userId: Int, from: String, to: String) {
        mutableBaskets.value = Resource.loading()
        compositeDisposable.add(
            api.basketsOfUser(
                token = "Bearer ${settings.token}",
                userId = userId,
                from = from,
                to = to,
                page = 1
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableBaskets.value = Resource.success(response.payload)
                        } else {
                            mutableBaskets.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableBaskets.value = Resource.error(error.message)
                    }
                )
        )
    }

    override fun onCleared() {
        compositeDisposable.clear()
        super.onCleared()
    }
}
