package com.ku.kuvn.ui.league.standings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ku.kuvn.data.LeaguesRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers

class StandingsViewModel(private val repo: LeaguesRepository, private val id: String) :
    ViewModel() {

    private var standingTeamsLiveData = MutableLiveData<List<StandingItem>>()

    fun getStandingTeamsLiveData(): LiveData<List<StandingItem>> = standingTeamsLiveData

    fun init() {
        repo.getLeague(id).map { league ->
                val standingItems = arrayListOf<StandingItem>()
                val standings = league.getStandings().filter { it.teams.isNotEmpty() }
                for (standing in standings) {
                    standingItems.add(GroupItem(standing.group))
                    standingItems.addAll(standing.teams.map { TeamItem(it) })
                }
                standingItems
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                standingTeamsLiveData.value = it
            }, {})
    }

    class Factory(private val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return try {
                modelClass.getConstructor(LeaguesRepository::class.java,
                        String::class.java).newInstance(
                        LeaguesRepository, id)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
    }
}