package uz.texnopos.elektrolife.ui.finance.salary.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.finance.salary.SalaryMonthly
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class SalaryDetailViewModel(private val api: ApiInterface, private val settings: Settings) :
    ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableMonthlySalary: MutableLiveData<Resource<List<SalaryMonthly>>> =
        MutableLiveData()
    val monthlySalary: LiveData<Resource<List<SalaryMonthly>>> = mutableMonthlySalary

    fun getMonthlySalary(employeeId: Int, from: String, to: String) {
        mutableMonthlySalary.value = Resource.loading()
        compositeDisposable.add(
            api.getMonthlySalary("Bearer ${settings.token}", employeeId, from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableMonthlySalary.value = Resource.success(response.payload)
                        } else {
                            mutableMonthlySalary.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableMonthlySalary.value = Resource.error(error.message)
                    }
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}
