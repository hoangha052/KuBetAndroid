package com.ku.kuvn.ui.league

import android.app.Application
import androidx.lifecycle.*
import com.ku.kuvn.data.LeaguesRepository
import com.ku.kuvn.api.league.Match
import com.ku.kuvn.api.league.getTopTeams
import com.ku.kuvn.ui.DisplayableError
import com.ku.kuvn.ui.league.standings.GroupItem
import com.ku.kuvn.ui.league.standings.StandingItem
import com.ku.kuvn.ui.league.standings.TeamItem
import com.ku.kuvn.utils.isOnline
import io.reactivex.rxjava3.disposables.CompositeDisposable

class LeagueViewModel(val app: Application, private val repo: LeaguesRepository,
                      private val id: String) : AndroidViewModel(app) {

    private val compositeDisposable = CompositeDisposable()

    private var standingTeamsLiveData = MutableLiveData<List<StandingItem>>()
    private var scheduleMatchLiveData = MutableLiveData<Match>()
    private var resultMatchLiveData = MutableLiveData<Match>()
    private var leagueTitleLiveData = MutableLiveData<String>()
    private var showLoadingLiveData = MutableLiveData<Boolean>()
    private var networkErrorLiveData = MutableLiveData<DisplayableError>()

    private var firstLoaded = false

    fun getStandingTeamsLiveData(): LiveData<List<StandingItem>> = standingTeamsLiveData
    fun getScheduleMatchLiveData(): LiveData<Match> = scheduleMatchLiveData
    fun getResultMatchLiveData(): LiveData<Match> = resultMatchLiveData
    fun getLeagueTitleLiveData(): LiveData<String> = leagueTitleLiveData
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

        compositeDisposable.add(repo.getLeague(id)
            .doOnSubscribe { showLoadingLiveData.value = true }
            .doOnEvent { _, _ -> showLoadingLiveData.value = false }
            .subscribe({ league ->
                if (league.getMatches().isNotEmpty()) {
                    scheduleMatchLiveData.value = league.getMatches()[0]
                }
                if (league.getHistories().isNotEmpty()) {
                    resultMatchLiveData.value = league.getHistories()[0]
                }

                leagueTitleLiveData.value = league.name

                val standingTeams = arrayListOf<StandingItem>()
                standingTeams.add(GroupItem("TEAM"))
                standingTeams.addAll(league.getTopTeams().map {
                    TeamItem(it)
                })
                if (standingTeams.size > 1) {
                    standingTeamsLiveData.value = standingTeams
                }
        }, {}))
    }

    override fun onCleared() {
        compositeDisposable.clear()
    }

    class Factory(private val app: Application, private val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return try {
                modelClass.getConstructor(Application::class.java, LeaguesRepository::class.java, String::class.java)
                    .newInstance(app, LeaguesRepository, id)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
    }
}