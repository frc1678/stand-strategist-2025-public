package org.citruscircuits.standstrategist

import org.citruscircuits.standstrategist.data.Alliance
import org.citruscircuits.standstrategist.data.datapoints.DataPoints
import org.citruscircuits.standstrategist.data.profiles.Profile
import org.citruscircuits.standstrategist.data.profiles.mergeProfiles
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class MergeProfilesTest {
    private val profile1 =
        Profile(
            matchSchedule =
                mapOf(
                    "1" to
                        Profile.MatchSchedule.Match(
                            listOf(
                                Profile.MatchSchedule.Team(color = Alliance.BLUE, number = "12345"),
                                Profile.MatchSchedule.Team(color = Alliance.RED, number = "56789")
                            )
                        )
                ),
            teamData =
                mapOf(
                    "12345" to DataPoints.Team.TeamDataEntry(strengths = "good driving"),
                    "56789" to DataPoints.Team.TeamDataEntry(weaknesses = "bad driving")
                ),
            timData =
                mapOf(
                    "1" to
                        mapOf(
                            "12345" to DataPoints.Tim.TimDataEntry(played_defense = true, defense_rating = 3),
                            "56789" to DataPoints.Tim.TimDataEntry(notes ="hello", broken_mechanism = "stuff")
                        )
                )
        )

    private val profile2 =
        Profile(
            matchSchedule =
                mapOf(
                    "2" to
                        Profile.MatchSchedule.Match(
                            listOf(
                                Profile.MatchSchedule.Team(color = Alliance.BLUE, number = "56789"),
                                Profile.MatchSchedule.Team(color = Alliance.RED, number = "12345")
                            )
                        )
                ),
            teamData = mapOf("12345" to DataPoints.Team.TeamDataEntry(strengths = "good robot")),
            timData =
                mapOf(
                    "1" to
                        mapOf(
                            "12345" to
                                DataPoints.Tim.TimDataEntry(
                                    played_defense = true, defense_rating = 5, notes ="hello", broken_mechanism = "stuff"
                                )
                        ),
                    "2" to mapOf("12345" to DataPoints.Tim.TimDataEntry(notes ="hello", broken_mechanism = "stuff"))
                )
        )

    private val profile3 = Profile()

    @Test
    fun `no changes with only one profile`() {
        val merged = mergeProfiles(listOf(profile1))
        with(merged.matchSchedule.schedule.value) {
            assertEquals(setOf("1"), keys)
            assertEquals(this["1"]!!.teams.size, 2)
            assertContentEquals(
                this["1"]!!.teams,
                listOf(
                    Profile.MatchSchedule.Team(color = Alliance.BLUE, number = "12345"),
                    Profile.MatchSchedule.Team(color = Alliance.RED, number = "56789")
                )
            )
        }
        with(merged.teamData.data.value) {
            assertEquals(setOf("12345", "56789"), keys)
            assertEquals(this["12345"], DataPoints.Team.TeamDataEntry(strengths = "good driving"))
            assertEquals(this["56789"], DataPoints.Team.TeamDataEntry(weaknesses = "bad driving"))
        }
        with(merged.timData.data.value) {
            assertEquals(setOf("1"), keys)
            assertEquals(this["1"]!!.keys, setOf("12345", "56789"))
            assertEquals(this["1"]!!["12345"], DataPoints.Tim.TimDataEntry(played_defense = true, defense_rating = 3))
            assertEquals(this["1"]!!["56789"], DataPoints.Tim.TimDataEntry(notes ="hello", broken_mechanism = "stuff"))
        }
    }

    @Test
    fun `merging two profiles`() {
        val merged = mergeProfiles(listOf(profile1, profile2))
        with(merged.matchSchedule.schedule.value) {
            assertEquals(setOf("1"), keys)
            assertEquals(this["1"]!!.teams.size, 2)
            assertContentEquals(
                this["1"]!!.teams,
                listOf(
                    Profile.MatchSchedule.Team(color = Alliance.BLUE, number = "12345"),
                    Profile.MatchSchedule.Team(color = Alliance.RED, number = "56789")
                )
            )
        }
        with(merged.teamData.data.value) {
            assertEquals(setOf("12345", "56789"), keys)
            assertEquals(this["12345"], DataPoints.Team.TeamDataEntry(strengths = "good driving\ngood robot"))
            assertEquals(this["56789"], DataPoints.Team.TeamDataEntry(weaknesses = "bad driving"))
        }
        with(merged.timData.data.value) {
            assertEquals(setOf("1", "2"), keys)
            assertEquals(this["1"]!!.keys, setOf("12345", "56789"))
            assertEquals(
                this["1"]!!["12345"],
                DataPoints.Tim.TimDataEntry(played_defense = true, defense_rating = 3, notes ="hello", broken_mechanism = "stuff")
            )
            assertEquals(this["1"]!!["56789"], DataPoints.Tim.TimDataEntry(notes ="hello", broken_mechanism = "stuff"))
            assertEquals(this["2"]!!.keys, setOf("12345"))
            assertEquals(this["2"]!!["12345"], DataPoints.Tim.TimDataEntry(notes ="hello", broken_mechanism = "stuff"))
        }
    }

    @Test
    fun `empty profile gets overridden`() {
        val merged = mergeProfiles(listOf(profile3, profile1))
        with(merged.matchSchedule.schedule.value) {
            assertEquals(setOf("1"), keys)
            assertEquals(this["1"]!!.teams.size, 2)
            assertContentEquals(
                this["1"]!!.teams,
                listOf(
                    Profile.MatchSchedule.Team(color = Alliance.BLUE, number = "12345"),
                    Profile.MatchSchedule.Team(color = Alliance.RED, number = "56789")
                )
            )
        }
        with(merged.teamData.data.value) {
            assertEquals(setOf("12345", "56789"), keys)
            assertEquals(this["12345"], DataPoints.Team.TeamDataEntry(strengths = "good driving"))
            assertEquals(this["56789"], DataPoints.Team.TeamDataEntry(weaknesses = "bad driving"))
        }
        with(merged.timData.data.value) {
            assertEquals(setOf("1"), keys)
            assertEquals(this["1"]!!.keys, setOf("12345", "56789"))
            assertEquals(this["1"]!!["12345"], DataPoints.Tim.TimDataEntry(played_defense = true, defense_rating = 3))
            assertEquals(this["1"]!!["56789"], DataPoints.Tim.TimDataEntry(notes ="hello", broken_mechanism = "stuff"))
        }
    }
}
