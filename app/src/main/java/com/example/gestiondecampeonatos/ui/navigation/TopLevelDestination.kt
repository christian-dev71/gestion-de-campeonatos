package com.example.gestiondecampeonatos.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.gestiondecampeonatos.R

enum class TopLevelDestination(
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val iconRes: Int,
) {
    HOME(R.string.nav_home, R.drawable.ic_home),
    TEAMS(R.string.nav_teams, R.drawable.ic_teams),
    MATCHES(R.string.nav_matches, R.drawable.ic_matches),
    STANDINGS(R.string.nav_standings, R.drawable.ic_standings),
}
