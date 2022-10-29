package uz.texnopos.elektrolife.ui.company

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.company.CompanyDetail
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class CompanyDetailsViewModel(private val api: ApiInterface, private val settings: Settings): ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _details: MutableLiveData<Resource<CompanyDetail>> = MutableLiveData()
    val details: LiveData<Resource<CompanyDetail>> = _details

    fun getDetails() {
        _details.value = Resource.loading()
        compositeDisposable.add(
            api.getCompanyDetails("Bearer ${settings.token}")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            _details.value = Resource.success(response.payload)
                        } else {
                            _details.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        _details.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    private val _updateDetails: MutableLiveData<Resource<String>> = MutableLiveData()
    val updateDetails: LiveData<Resource<String>> = _updateDetails

    fun updateCompanyDetails(details: CompanyDetail) {
        _updateDetails.value = Resource.loading()
        compositeDisposable.add(
            api.updateCompanyDetails("Bearer ${settings.token}", details)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            _updateDetails.value = Resource.success("Success!")
                        } else {
                            _updateDetails.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        _updateDetails.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
