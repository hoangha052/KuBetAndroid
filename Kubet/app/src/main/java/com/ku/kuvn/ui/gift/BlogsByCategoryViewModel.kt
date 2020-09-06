package com.ku.kuvn.ui.gift

import android.app.Application
import androidx.lifecycle.*
import com.ku.kuvn.api.Blog
import com.ku.kuvn.api.KuService
import com.ku.kuvn.data.LeaguesRepository
import com.ku.kuvn.ui.DisplayableError
import com.ku.kuvn.utils.isOnline
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class BlogsByCategoryViewModel(val app: Application, private val id: String) : AndroidViewModel(app) {

    private val compositeDisposable = CompositeDisposable()

    private var giftsLiveData = MutableLiveData<List<Blog>>()
    private var showLoadingLiveData = MutableLiveData<Boolean>()
    private var networkErrorLiveData = MutableLiveData<DisplayableError>()

    private val gifts = arrayListOf<Blog>()
    private var totalPages = 1
    private var currentPage = 1
    private var firstLoaded = false

    fun getGiftsLiveData(): LiveData<List<Blog>> = giftsLiveData
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

        compositeDisposable.add(KuService.kuApi.getBlogsByCategory(id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoadingLiveData.value = true }
            .doOnEvent { _, _ -> showLoadingLiveData.value = false }
            .subscribe({ giftsResponse ->
                gifts.addAll(giftsResponse.data.gifts)
                totalPages = giftsResponse.data.totalPages
                giftsLiveData.value = gifts
            }, {}))
    }

    fun loadMore() {
        if (showLoadingLiveData.value == true || currentPage <= totalPages) return

        compositeDisposable.add(KuService.kuApi.getGifts(++currentPage).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoadingLiveData.value = true }
            .doOnEvent { _, _ -> showLoadingLiveData.value = false }
            .subscribe({ giftsResponse ->
                gifts.addAll(giftsResponse.data.gifts)
                giftsLiveData.value = gifts
            }, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

    class Factory(private val app: Application, private val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return try {
                modelClass.getConstructor(Application::class.java, String::class.java).newInstance(app, id)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
    }
}