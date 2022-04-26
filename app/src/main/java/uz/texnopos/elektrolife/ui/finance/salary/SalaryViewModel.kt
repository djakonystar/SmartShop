package uz.texnopos.elektrolife.ui.finance.salary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import uz.texnopos.elektrolife.core.Resource
import uz.texnopos.elektrolife.data.model.finance.salary.Salary
import uz.texnopos.elektrolife.data.retrofit.ApiInterface
import uz.texnopos.elektrolife.settings.Settings

class SalaryViewModel(private val api: ApiInterface, private val settings: Settings) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private var mutableSalaries: MutableLiveData<Resource<List<Salary>>> = MutableLiveData()
    val salaries: LiveData<Resource<List<Salary>>> = mutableSalaries

    fun getSalaries(from: String, to: String) {
        mutableSalaries.value = Resource.loading()
        compositeDisposable.add(
            api.getSalaries("Bearer ${settings.token}", from, to)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        if (response.successful) {
                            mutableSalaries.value = Resource.success(response.payload)
                        } else {
                            mutableSalaries.value = Resource.error(response.message)
                        }
                    },
                    { error ->
                        mutableSalaries.value = Resource.error(error.message)
                    }
                )
        )
    }
}
