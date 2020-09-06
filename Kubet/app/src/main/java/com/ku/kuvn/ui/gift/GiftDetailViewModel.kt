package com.ku.kuvn.ui.gift

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ku.kuvn.api.Blog
import com.ku.kuvn.api.KuService
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class GiftDetailViewModel : ViewModel() {

    private var giftLiveData = MutableLiveData<Blog>()
    private var showLoadingLiveData = MutableLiveData<Boolean>()
    private val compositeDisposable = CompositeDisposable()

    fun getGiftsLiveData(): LiveData<Blog> = giftLiveData
    fun getShowLoadingLiveData(): LiveData<Boolean> = showLoadingLiveData

    fun init(id: String) {
        compositeDisposable.add(KuService.kuApi.getGift(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoadingLiveData.value = true }
            .subscribe({ giftResponse ->
                giftLiveData.value = giftResponse.data
            }, {
                showLoadingLiveData.value = false
            }))
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }
}