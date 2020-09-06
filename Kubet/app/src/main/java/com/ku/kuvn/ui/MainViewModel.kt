package com.ku.kuvn.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ku.kuvn.api.KuService
import com.ku.kuvn.api.Menu
import com.ku.kuvn.utils.SingleLiveEvent
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class MainViewModel : ViewModel() {

    private val compositeDisposable = CompositeDisposable()

    private var menuSettingLiveData = SingleLiveEvent<Menu>()
    private var menuTitleLiveData = MutableLiveData<String>()
    private var supportUrlLiveData = MutableLiveData<String>()
    private var showLoadingLiveData = MutableLiveData<Boolean>()
    private var firstLoaded = false
    private var isFetchingMenu = false

    fun getMenuSettingLiveData(): LiveData<Menu> = menuSettingLiveData
    fun getMenuTitleLiveData(): LiveData<String> = menuTitleLiveData
    fun getSupportUrlLiveData(): LiveData<String> = supportUrlLiveData
    fun getShowLoadingLiveData(): LiveData<Boolean> = showLoadingLiveData

    fun init() {
        if (firstLoaded) return
        firstLoaded = true

        compositeDisposable.add(KuService.kuApi.getMenuSetting()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ menuSettingResponse ->
                menuTitleLiveData.value = menuSettingResponse.data.title
            }, {}))

        compositeDisposable.add(KuService.kuApi.getSettings()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ settingsResponse ->
                supportUrlLiveData.value = settingsResponse.data.zaloUrl
            }, {}))
    }

    fun fetchMenuSetting() {
        if (isFetchingMenu) return

        isFetchingMenu = true

        compositeDisposable.add(KuService.kuApi.getMenuSetting()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoadingLiveData.value = true }
            .doOnEvent { _, _ ->
                isFetchingMenu = false
                showLoadingLiveData.value = false
            }
            .subscribe({ menuSettingResponse ->
                menuSettingLiveData.value = menuSettingResponse.data
            }, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}