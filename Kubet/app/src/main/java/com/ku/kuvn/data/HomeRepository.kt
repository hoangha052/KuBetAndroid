package com.ku.kuvn.data

import com.ku.kuvn.api.*
import com.ku.kuvn.utils.ConnectionUtilsImpl
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.BiFunction

class HomeRepository(private val connectionUtils: ConnectionUtilsImpl) {

    fun getBannersAndMenus(): Single<Result<Pair<List<Banner>, MenuResponse>>> {
        if (connectionUtils.isOnline()) {
            return Single.zip<BaseResponse<List<Banner>>, BaseResponse<MenuResponse>, Result<Pair<List<Banner>, MenuResponse>>> (
                    KuService.kuApi.getBanners(), KuService.kuApi.getMenus(),
                    BiFunction { banners, menuResponse -> Result.Success(Pair(banners.data, menuResponse.data)) })
                .onErrorReturn { Result.Error(GeneralErrorHandlerImpl().getError(it)) }
        }
        return Single.just(Result.Error(ErrorEntity.Network))
    }
}