package org.citruscircuits.standstrategist.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

/**
 * Manager for auto saving data
 */
class AutoSaveManager(private val saveData: () -> Unit, private val coroutineScope: CoroutineScope) {
    private var job: Job? = null

    private val debounce = 2000.milliseconds

    private val channel = Channel<Unit>(Channel.CONFLATED)

    fun start() {
        job =
            coroutineScope.launch(Dispatchers.IO) {
                for (ignored in channel) {
                    saveData()
                    delay(debounce)
                }
            }
    }

    suspend fun requestSave() = channel.send(Unit)

    fun forceSave() = saveData()

    suspend fun stop() = job?.cancelAndJoin()
}
