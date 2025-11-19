package com.pixelro.nenoonkiosk.core.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import com.pixelro.nenoonkiosk.app.NenoonKioskApplication
import com.pixelro.nenoonkiosk.core.util.stt.SttConfig
import com.pixelro.nenoonkiosk.core.util.stt.SttCoordinator
import kotlinx.coroutines.delay

object STT {
    private val coordinator = SttCoordinator(contextProvider = { NenoonKioskApplication.applicationContext() })

    fun initSTT() = coordinator.init()

    fun configurePhraseHints(hints: Set<String>, boost: Int = SttConfig.DEFAULT_PHRASE_BOOST) {
        coordinator.configurePhraseHints(hints, boost)
    }
    
    fun enableVisualAcuityNumberHints(boost: Int = 150) {
        coordinator.enableVisualAcuityNumberHints(boost)
    }
    
    fun startListening(
        language: String = "ko-KR",
        onResult: (String) -> Unit,
        onError: ((Int) -> Unit)? = null,
        onReady: (() -> Unit)? = null,
        shortUtterance: Boolean = true,
        autoStopTimeoutMs: Long? = null,
    ) {
        coordinator.startListening(
            language = language,
            onResult = onResult,
            onError = onError,
            onReady = onReady,
            shortUtterance = shortUtterance,
            autoStopTimeoutMs = autoStopTimeoutMs,
        )
    }
    
    fun startContinuousListening(
        language: String = "ko-KR",
        onResult: (String) -> Unit,
        onError: ((Int) -> Unit)? = null,
        onReady: (() -> Unit)? = null,
        restartDelayOnResultMs: Long = 250L,
        restartDelayOnErrorMs: Long = 450L,
        shortUtterance: Boolean = true,
        autoStopTimeoutMs: Long? = null,
    ) {
        coordinator.startContinuousListening(
            language = language,
            onResult = onResult,
            onError = onError,
            onReady = onReady,
            restartDelayOnResultMs = restartDelayOnResultMs,
            restartDelayOnErrorMs = restartDelayOnErrorMs,
            shortUtterance = shortUtterance,
            autoStopTimeoutMs = autoStopTimeoutMs,
        )
    }
    
    fun stopListening() = coordinator.stopListening()

    fun cancelListening() = coordinator.cancelListening()

    fun stopContinuousListening() = coordinator.stopContinuousListening()

    fun destroySTT() = coordinator.destroy()

    fun isListening(): Boolean = coordinator.isListening()

    fun isAvailable(): Boolean = coordinator.isAvailable()

    fun getErrorMessage(error: Int): String = coordinator.getErrorMessage(error)

    fun setStateObserver(observer: ((Boolean, Boolean) -> Unit)?) {
        coordinator.setStateObserver(observer)
    }
}

@Composable
fun AutoStartSTT(
    onResult: (String) -> Unit,
    language: String = "ko-KR",
    onError: ((Int) -> Unit)? = null,
    onReady: (() -> Unit)? = null,
    enabled: Boolean = true,
    delay: Long = 0,
) {
    val latestOnResult = rememberUpdatedState(onResult)
    val latestOnError = rememberUpdatedState(onError)
    val latestOnReady = rememberUpdatedState(onReady)
    
    LaunchedEffect(enabled) {
        if (enabled) {
            STT.initSTT()
            if (delay > 0) {
                delay(delay)
            }
            STT.startContinuousListening(
                language = language,
                onResult = { latestOnResult.value(it) },
                onError = { code -> latestOnError.value?.invoke(code) },
                onReady = { latestOnReady.value?.invoke() },
            )
        } else {
            STT.stopContinuousListening()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            STT.stopContinuousListening()
        }
    }
}

