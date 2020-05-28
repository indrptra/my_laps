package id.indosat.ml.viewmodel

import androidx.lifecycle.ViewModel
import id.indosat.ml.common.model.MLPrefModel
import id.indosat.ml.common.model.response.NotificationReadResult
import id.indosat.ml.common.model.response.NotificationResult
import id.indosat.ml.core.config.MLConfig
import id.indosat.ml.core.repository.IMLRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MLNotificationViewModel @Inject constructor(val repo: IMLRepository) : ViewModel() {
    companion object {
        const val classTag = "MLNotificationViewModel"
    }

    var fromRowSorted = 0
    var limitRowSorted = MLConfig.LimitData
    var totalDataSorted = 0
    val compositeDisposable = CompositeDisposable()
    fun clear() {
        compositeDisposable.dispose()
    }

    inline fun getNotifications(
        fromRow: Int = 0, limitRow: Int = limitRowSorted,
        crossinline callback: (Boolean, Boolean, Boolean, String, NotificationResult?) -> Unit
    ) {
        compositeDisposable.add(
            repo.getNotifications(MLPrefModel.userToken, MLPrefModel.userId, fromRow, limitRow)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
                    totalDataSorted = response.result?.numfound ?: 0
                    fromRowSorted = response.result?.endindex ?: 0
                    fromRowSorted += 1
                    val isLastPage = (totalDataSorted - 1) == fromRowSorted
                    callback(response.validtoken, response.error, isLastPage, response.message, response.result)
                }, { error ->
                    callback(true, true, true, error.localizedMessage ?: "", null)
                })
        )
    }

    inline fun markAsRead(
        id: Int,
        crossinline callback: (Boolean, Boolean, String, NotificationReadResult?) -> Unit
    ) {
        compositeDisposable.add(
            repo.markNotificationAsRead(id, MLPrefModel.userToken, MLPrefModel.userId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({ response ->
                    callback(response.validtoken, response.error, response.message, response.result)
                }, { error ->
                    callback(true, true, error.localizedMessage ?: "", null)
                })
        )
    }

}