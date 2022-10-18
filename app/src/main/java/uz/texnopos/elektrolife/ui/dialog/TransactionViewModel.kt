package uz.texnopos.elektrolife.ui.dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.GenericResponse
import uz.texnopos.elektrolife.data.model.newproduct.Transaction
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class TransactionViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableTransaction: MutableLiveData<Resource<GenericResponse<List<Any>>>> =
        MutableLiveData()
    val transaction: LiveData<Resource<GenericResponse<List<Any>>>> = mutableTransaction

    fun newTransaction(transaction: Transaction) {
        mutableTransaction.value = Resource.loading()
        compositeDisposable.add(
            api.newTransaction("Bearer ${settings.token}", transaction = transaction)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        mutableTransaction.value = Resource.success(response)
                    },
                    { error ->
                        mutableTransaction.value = Resource.error(error.localizedMessage)
                    }
                )
        )
    }
}
