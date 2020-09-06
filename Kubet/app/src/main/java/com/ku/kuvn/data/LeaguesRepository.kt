package com.ku.kuvn.data

import com.ku.kuvn.api.KuService
import com.ku.kuvn.api.league.League
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers

object LeaguesRepository {

    private val leagues = hashMapOf<String, League>()

    fun getLeague(id: String): Single<League> {
        val cachedLeague = leagues[id]
        if (cachedLeague != null) {
            return Single.just(cachedLeague)
        }
        return KuService.kuApi.getLeague(id).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess { leagues[it.data.id] = it.data }
            .map { it.data }
    }
}