package com.ku.kuvn.ui.league.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ku.kuvn.data.LeaguesRepository
import com.ku.kuvn.api.league.Match

class MatchScheduleViewModel(private val repo: LeaguesRepository, private val id: String) : ViewModel() {

    private var matchesLiveData = MutableLiveData<List<Match>>()

    fun getMatchesLiveData(): LiveData<List<Match>> = matchesLiveData

    fun init() {
        repo.getLeague(id).map { it.getMatches() }.subscribe({ matchesLiveData.value = it }, {})
    }

    class Factory(private val id: String) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return try {
                modelClass.getConstructor(LeaguesRepository::class.java, String::class.java)
                    .newInstance(LeaguesRepository, id)
            } catch (e: InstantiationException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            } catch (e: IllegalAccessException) {
                throw RuntimeException("Cannot create an instance of $modelClass", e)
            }
        }
    }
}