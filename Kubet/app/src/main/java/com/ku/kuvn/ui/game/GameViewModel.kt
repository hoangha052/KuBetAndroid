package com.ku.kuvn.ui.game

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ku.kuvn.api.Game
import com.ku.kuvn.api.KuService
import com.ku.kuvn.ui.DisplayableError
import com.ku.kuvn.utils.isOnline
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class GameViewModel(val app: Application) : AndroidViewModel(app) {

    private val compositeDisposable = CompositeDisposable()

    private var gamesLiveData = MutableLiveData<List<Game>>()
    private var showLoadingLiveData = MutableLiveData<Boolean>()
    private var networkErrorLiveData = MutableLiveData<DisplayableError>()

    private var firstLoaded = false

    fun getGamesLiveData(): LiveData<List<Game>> = gamesLiveData
    fun getShowLoadingLiveData(): LiveData<Boolean> = showLoadingLiveData
    fun getNetworkErrorLiveData(): LiveData<DisplayableError> = networkErrorLiveData

    fun init() {
        if (firstLoaded) return

        if (!app.isOnline()) {
            networkErrorLiveData.value = DisplayableError(true) {
                init()
            }
            return
        }
        networkErrorLiveData.value = DisplayableError(false)
        firstLoaded = true

        compositeDisposable.add(KuService.kuApi.getGames()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoadingLiveData.value = true }
            .doOnEvent { _, _ -> showLoadingLiveData.value = false }
            .subscribe({ gameResponse ->
                gamesLiveData.value = gameResponse.data
            }, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}