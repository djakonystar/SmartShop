package uz.texnopos.elektrolife.ui.settings.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.prefix.PrefixResponse
import uz.texnopos.elektrolife.data.retrofit.PrefixApi

class PrefixViewModel(private val api: PrefixApi) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _prefix: MutableLiveData<Resource<PrefixResponse>> = MutableLiveData()
    val prefix: LiveData<Resource<PrefixResponse>> = _prefix

    fun checkPrefix(prefix: String) {
        _prefix.value = Resource.loading()
        compositeDisposable.add(
            api.checkPrefix(prefix)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.isSuccessful) {
                            _prefix.value = Resource.success(response.body()!!)
                        } else {
                            _prefix.value = Resource.error(
                                if (response.code() == 404) "Prefix not found"
                                else response.message()
                            )
                        }
                    },
                    { error ->
                        _prefix.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}
