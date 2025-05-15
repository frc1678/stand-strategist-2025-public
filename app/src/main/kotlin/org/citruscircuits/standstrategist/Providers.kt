package org.citruscircuits.standstrategist

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.compositionLocalOf
import kotlinx.coroutines.CoroutineScope
import org.citruscircuits.standstrategist.data.AppSettings
import org.citruscircuits.standstrategist.data.profiles.Profile

val LocalMatchSchedule = compositionLocalOf<Profile.MatchSchedule> { error("MatchSchedule not provided") }

val LocalTeamData = compositionLocalOf<Profile.TeamData> { error("TeamData not provided") }

val LocalTimData = compositionLocalOf<Profile.TimData> { error("TimData not provided") }

val LocalProfileSettings = compositionLocalOf<Profile.Settings> { error("Settings not provided") }

val LocalAppSettings = compositionLocalOf<AppSettings> { error("AppSettings not provided") }

val LocalCoroutineScope = compositionLocalOf<CoroutineScope> { error("CoroutineScope not provided") }

val LocalWindowSizeClass = compositionLocalOf<WindowSizeClass> { error("WindowSizeClass not provided") }
