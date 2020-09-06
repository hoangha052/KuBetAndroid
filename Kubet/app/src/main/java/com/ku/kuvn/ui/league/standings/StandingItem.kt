package com.ku.kuvn.ui.league.standings

import com.ku.kuvn.api.league.Team

sealed class StandingItem

data class GroupItem(val title: String) : StandingItem()
data class TeamItem(val team: Team) : StandingItem()