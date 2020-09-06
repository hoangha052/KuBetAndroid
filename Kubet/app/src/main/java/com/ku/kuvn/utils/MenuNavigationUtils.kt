package com.ku.kuvn.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.ku.kuvn.api.Menu
import com.ku.kuvn.ui.InAppBrowserActivity
import com.ku.kuvn.ui.MainActivity
import com.ku.kuvn.ui.game.GameActivity
import com.ku.kuvn.ui.game.GameDetailActivity
import com.ku.kuvn.ui.gift.BlogsByCategoryActivity
import com.ku.kuvn.ui.gift.GiftDetailActivity
import com.ku.kuvn.ui.league.LeagueDetailActivity
import com.ku.kuvn.ui.league.LeaguesActivity

fun openMenu(activity: Activity, menu: Menu) {
    when (menu.attr.type) {
        0 -> activity.startActivity(InAppBrowserActivity.getIntent(activity, menu.attr.data))
        1 -> {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(menu.attr.data))
            if (intent.resolveActivity(activity.packageManager) != null) {
                activity.startActivity(intent)
            }
        }
        2 -> when (menu.attr.name) {
            "g_list" -> activity.startActivity(Intent(activity, GameActivity::class.java))
            "g_detail" -> activity.startActivity(
                    GameDetailActivity.getIntent(activity, menu.attr.data))

            "l_list" -> activity.startActivity(Intent(activity, LeaguesActivity::class.java))
            "l_detail" -> activity.startActivity(
                    LeagueDetailActivity.getIntent(activity, menu.attr.data, ""))

            "b_list" -> (activity as? MainActivity)?.openGift()
            "b_detail" -> activity.startActivity(
                    GiftDetailActivity.getIntent(activity, menu.attr.data))
            "b_cat_blogs" -> activity.startActivity(
                    BlogsByCategoryActivity.getIntent(activity, menu.title, menu.attr.data))

            "calc" -> (activity as? MainActivity)?.openCalculation()
            "contact" -> (activity as? MainActivity)?.openSupport()
        }
    }
}