package com.pixelro.nenoonkiosk.feature.print

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.print.components.ResultsGrid
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle

@Composable
fun ResultPrintScreen(
    state: ResultPrintUiState,
    onBack: () -> Unit,
    onPrint: () -> Unit,
) {
    val isLandscape = isLandscape()

    if (isLandscape) {
        LandscapeResultPrintScreen(
            state = state,
            onBack = onBack,
            onPrint = onPrint
        )
    } else {
        PortraitResultPrintScreen(
            state = state,
            onBack = onBack,
            onPrint = onPrint
        )
    }
}

@Composable
private fun PortraitResultPrintScreen(
    state: ResultPrintUiState,
    onBack: () -> Unit,
    onPrint: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            NenoonTopBar(
                title = state.title,
                showBackButton = true,
                onBackClicked = onBack,
                containerColor = White,
                contentColor = Black
            )

            Spacer(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Gray)
            )

            Column(
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth()
            ) {
                when {
                    state.loading -> {
                        Text(
                            text = StringProvider.getStringComposable(R.string.result_print_screen_loading_results),
                            style = bodyTextStyle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 50.dp)
                        )
                    }

                    state.summaries.isEmpty() -> {
                        Text(
                            text = StringProvider.getStringComposable(R.string.result_print_screen_loading_results),
                            style = bodyTextStyle,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 50.dp)
                        )
                    }

                    else -> {
                        ResultsGrid(summaries = state.summaries)
                    }
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

@Composable
private fun LandscapeResultPrintScreen(
    state: ResultPrintUiState,
    onBack: () -> Unit,
    onPrint: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = White
    ) {
        Column {
            NenoonTopBar(
                title = state.title,
                showBackButton = true,
                onBackClicked = onBack,
                containerColor = White,
                contentColor = Black
            )

            Spacer(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Gray)
            )

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 30.dp),
                horizontalArrangement = Arrangement.spacedBy(40.dp)
            ) {
                // 왼쪽: 검사 결과 그리드
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when {
                        state.loading -> {
                            Text(
                                text = StringProvider.getStringComposable(R.string.result_print_screen_loading_results),
                                style = bodyTextStyle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 50.dp)
                            )
                        }

                        state.summaries.isEmpty() -> {
                            Text(
                                text = StringProvider.getStringComposable(R.string.result_print_screen_loading_results),
                                style = bodyTextStyle,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 50.dp)
                            )
                        }

                        else -> {
                            ResultsGrid(summaries = state.summaries)
                        }
                    }
                }

                // 구분선
                Spacer(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(Gray)
                )

                // 오른쪽: 프린트 버튼 영역
                Column(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    PrimaryButton(
                        onClick = onPrint,
                        enabled = state.canPrint,
                        text = StringProvider.getStringComposable(R.string.result_print_screen_print_button),
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(100.dp)
                    )
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 900,
    heightDp = 1400,
    name = "ResultPrint - Portrait Loading",
    apiLevel = 34
)
@Composable
private fun ResultPrintScreen_Preview_Portrait_Loading() {
    PortraitResultPrintScreen(
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

@Preview(
    showBackground = true,
    widthDp = 900,
    heightDp = 1400,
    name = "ResultPrint - Portrait Data",
    apiLevel = 34
)
@Composable
private fun ResultPrintScreen_Preview_Portrait_Data() {
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
    PortraitResultPrintScreen(
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

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    name = "ResultPrint - Landscape Loading",
    apiLevel = 34
)
@Composable
private fun ResultPrintScreen_Preview_Landscape_Loading() {
    LandscapeResultPrintScreen(
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

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    name = "ResultPrint - Landscape Data",
    apiLevel = 34
)
@Composable
private fun ResultPrintScreen_Preview_Landscape_Data() {
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
    LandscapeResultPrintScreen(
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
