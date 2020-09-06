package com.ku.kuvn.ui.home

import android.app.Application
import androidx.lifecycle.*
import com.ku.kuvn.KuApplication
import com.ku.kuvn.api.KuService
import com.ku.kuvn.data.ErrorEntity
import com.ku.kuvn.data.HomeRepository
import com.ku.kuvn.data.Result
import com.ku.kuvn.ui.DisplayableError
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

class HomeViewModel(val app: Application, private val homeRepository: HomeRepository) : AndroidViewModel(app) {

    private val compositeDisposable = CompositeDisposable()

    private var homeItemsLiveData = MutableLiveData<List<HomeItem>>()
    private var showLoadingLiveData = MutableLiveData<Boolean>()
    private var networkErrorLiveData = MutableLiveData<DisplayableError>()

    private val homeItems = arrayListOf<HomeItem>()
    private var totalPages = 1
    private var currentPage = 1
    private var firstLoaded = false

    fun getHomeItemsLiveData(): LiveData<List<HomeItem>> = homeItemsLiveData
    fun getShowLoadingLiveData(): LiveData<Boolean> = showLoadingLiveData
    fun getNetworkErrorLiveData(): LiveData<DisplayableError> = networkErrorLiveData

    fun init() {
        if (firstLoaded) return
        firstLoaded = true

        loadMenusAndBanners()
    }

    private fun loadMenusAndBanners() {
        if (showLoadingLiveData.value == true) return

        networkErrorLiveData.value = DisplayableError(false)

        compositeDisposable.add(homeRepository.getBannersAndMenus()
            .map<Result<Pair<List<HomeItem>, Int>>> { result ->
                return@map when (result) {
                    is Result.Success -> {
                        val items = mutableListOf<HomeItem>().apply {
                            add(BannerItem(result.data.first))
                            add(GameItem())
                            add(SportItem())
                            addAll(result.data.second.menus.map { MenuItem(it) })
                        }
                        Result.Success(Pair(items, result.data.second.totalPages))
                    }
                    is Result.Error -> Result.Error(result.error)
                }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showLoadingLiveData.value = true }
            .doOnEvent { _, _ -> showLoadingLiveData.value = false }
            .subscribe({ result ->
                when (result) {
                    is Result.Success -> {
                        homeItems.addAll(result.data.first)
                        totalPages = result.data.second
                        homeItemsLiveData.value = homeItems
                    }
                    is Result.Error -> when (result.error) {
                        ErrorEntity.Network -> networkErrorLiveData.value = DisplayableError(true) {
                            loadMenusAndBanners()
                        }
                    }
                }
            }, {}))
    }

    fun loadMore() {
        if (showLoadingLiveData.value == true || currentPage <= totalPages) return

        compositeDisposable.add(KuService.kuApi.getMenus(++currentPage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showLoadingLiveData.value = true }
                    .doOnEvent { _, _ -> showLoadingLiveData.value = false }
                    .subscribe({ menuResponse ->
                        homeItems.addAll(menuResponse.data.menus.map { MenuItem(it) })
                        homeItemsLiveData.value = homeItems
                    }, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

    class Factory(private val app: KuApplication) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return try {
                modelClass.getConstructor(Application::class.java, HomeRepository::class.java)
                    .newInstance(app, app.getHomeRepository())
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
    }
}