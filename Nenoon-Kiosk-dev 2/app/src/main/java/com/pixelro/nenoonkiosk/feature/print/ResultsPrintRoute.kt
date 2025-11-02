package com.pixelro.nenoonkiosk.feature.print

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.harang.data.model.dto.User
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.dataprovider.CompoundTestResult
import com.pixelro.nenoonkiosk.feature.print.model.rememberStrings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ResultPrintRoute(
    surveyId: Long,
    navController: NavController,
    qrCode: Bitmap?,
    userData: User?,
    viewModel: ResultPrintViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val strings = rememberStrings()
    val scope = rememberCoroutineScope()

    var compoundResults by remember { mutableStateOf<List<CompoundTestResult>>(emptyList()) }
    val sharedPreferences = remember {
        context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }
    val locale = sharedPreferences.getString("language", "defaultLanguage")

    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(surveyId, userData?.accessToken) {
        loading = true
        scope.launch(Dispatchers.IO) {
            val results = when {
                AppConstants.MANAGE_USERS_INTERNALLY -> {
                    // 내부 사용자 관리일 때는 필요 데이터 정의에 맞게 사용
                    // 기존 코드가 빈 리스트로 표시하므로 유지
                    emptyList()
                }
                DebugConstants.PRINTER_USE_DUMMY_DATA -> {
                    listOf(CompoundTestResult(viewModel.createDummyCompoundTestResult()))
                }
                else -> {
                    val token = userData?.accessToken.orEmpty()
                    viewModel.getCompoundTestResult(token).map { CompoundTestResult(it) }
                }
            }
            compoundResults = results
            loading = false
        }
    }

    val latest = compoundResults.firstOrNull()
    val summaries = remember(latest) { mapToSummaries(latest, strings) }

    val uiState = ResultPrintUiState(
        loading = loading,
        summaries = summaries,
        canPrint = compoundResults.isNotEmpty(),
        title = strings.pageTitle
    )

    ResultPrintScreen(
        state = uiState,
        onBack = { navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false) },
        onPrint = {
            if (uiState.canPrint) {
                scope.launch(Dispatchers.IO) {
                    viewModel.printContent(context, compoundResults, qrCode, locale, userData)
                }
            }
        }
    )
}