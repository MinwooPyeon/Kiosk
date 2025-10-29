package com.pixelro.nenoonkiosk.feature.print

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.print.components.ResultsGrid
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle

@Composable
fun ResultPrintScreen(
    state: ResultPrintUiState,
    onBack: () -> Unit,
    onPrint: () -> Unit,
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Top bar
            NenoonTopBar(
                title = state.title,
                showBackButton = true,
                onBackClicked = onBack,
            )
            Spacer(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xff000000))
            )

            Column(
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth()
            ) {
                if (state.loading) {
                    Text(
                        text = StringProvider.getStringComposable(R.string.result_print_screen_loading_results),
                        style = bodyTextStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp)
                    )
                } else if (state.summaries.isEmpty()) {
                    Text(
                        text = StringProvider.getStringComposable(R.string.result_print_screen_loading_results), // 필요 시 별도 "결과 없음" 문자열 추가 가능
                        style = bodyTextStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 50.dp)
                    )
                } else {
                    ResultsGrid(summaries = state.summaries)
                }
            }
            Spacer(Modifier.weight(1F))
            Column(modifier = Modifier.padding(32.dp)) {
                PrimaryButton(
                    onClick = onPrint,
                    enabled = state.canPrint,
                    text = StringProvider.getStringComposable(R.string.result_print_screen_print_button)
                )
            }

        }
    }
}


@Preview(showBackground = true, widthDp = 900, heightDp = 1400, name = "ResultPrint - Loading", apiLevel = 34)
@Composable
private fun ResultPrintScreen_Preview_Loading() {
    ResultPrintScreen(
        state = ResultPrintUiState(
            loading = true,
            summaries = emptyList(),
            canPrint = false,
            title = "검사 결과"
        ),
        onBack = {},
        onPrint = {}
    )
}

@Preview(showBackground = true, widthDp = 900, heightDp = 1400, name = "ResultPrint - Data", apiLevel = 34)
@Composable
private fun ResultPrintScreen_Preview_Data() {
    val fake = listOf(
        ResultSummary("시력 검사", true),
        ResultSummary("노안 검사", false),
        ResultSummary("암슬러 검사", true),
        ResultSummary("M-Chart 검사", true),
        ResultSummary("혈압 검사", true),
        ResultSummary("악력 검사", false),
        ResultSummary("치매 설문", true),
        ResultSummary("폐기능 검사", true),
    )
    ResultPrintScreen(
        state = ResultPrintUiState(
            loading = false,
            summaries = fake,
            canPrint = true,
            title = "검사 결과"
        ),
        onBack = {},
        onPrint = {}
    )
}
