package uz.texnopos.electrolightwarehouse.ui.newpayment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.electrolightwarehouse.core.Resource
import uz.texnopos.electrolightwarehouse.data.GenericResponse
import uz.texnopos.electrolightwarehouse.data.newPayment.NewPayment
import uz.texnopos.electrolightwarehouse.data.retrofit.ApiInterface

class NewPaymentViewModel(private val api: ApiInterface): ViewModel() {
    private var compositeDisposable = CompositeDisposable()

    private var mutableNewPayment: MutableLiveData<Resource<GenericResponse<String>>> = MutableLiveData()
    val newPayment: LiveData<Resource<GenericResponse<String>>> get() = mutableNewPayment

    fun newPayment(token: String,newPayment: NewPayment){
        mutableNewPayment.value = Resource.loading()
        compositeDisposable.add(api.payment(token,newPayment)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                if (it.successful)
                    {
                    mutableNewPayment.value = Resource.success(it)
                    }
                    else
                    {
                    mutableNewPayment.value = Resource.error(it.message)
                    }
                }
                ,
                {
                    mutableNewPayment.value = Resource.error(it.localizedMessage)
                }
            )
        )
    }
}