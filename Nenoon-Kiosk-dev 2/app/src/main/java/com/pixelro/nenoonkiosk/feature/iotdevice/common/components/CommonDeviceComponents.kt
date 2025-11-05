package com.pixelro.nenoonkiosk.feature.iotdevice.common.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.core.ui.AccentedText
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle

/**
 * AccentedText + 버튼 조합
 */
@Composable
fun AccentedTextWithButton(
    prefixRes: Int,
    accentRes: Int,
    suffixRes: Int,
    buttonTextRes: Int,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AccentedText(
                prefix = stringResource(prefixRes),
                accent = stringResource(accentRes),
                suffix = stringResource(suffixRes),
            )
        }
        PrimaryButton(
            onClick = onButtonClick,
            text = stringResource(buttonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

/**
 * AccentedText + 2개 버튼 조합
 */
@Composable
fun AccentedTextWithTwoButtons(
    prefixRes: Int,
    accentRes: Int,
    suffixRes: Int,
    primaryButtonTextRes: Int,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonTextRes: Int,
    onSecondaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AccentedText(
                prefix = stringResource(prefixRes),
                accent = stringResource(accentRes),
                suffix = stringResource(suffixRes),
            )
        }
        PrimaryButton(
            onClick = onPrimaryButtonClick,
            text = stringResource(primaryButtonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
        PrimaryButton(
            onClick = onSecondaryButtonClick,
            text = stringResource(secondaryButtonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

/**
 * 텍스트 + 버튼 조합
 */
@Composable
fun TextWithButton(
    textRes: Int,
    buttonTextRes: Int,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    topPadding: Dp = 180.dp,
    textStyle: TextStyle = TextStyle.Message,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            StyledText(
                stringResource(textRes),
                style = textStyle,
            )
        }
        PrimaryButton(
            onClick = onButtonClick,
            text = stringResource(buttonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

/**
 * 텍스트 + 2개 버튼 조합
 */
@Composable
fun TextWithTwoButtons(
    textRes: Int,
    primaryButtonTextRes: Int,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonTextRes: Int,
    onSecondaryButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = TextStyle.Message,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            StyledText(
                stringResource(textRes),
                style = textStyle,
            )
        }
        PrimaryButton(
            onClick = onPrimaryButtonClick,
            text = stringResource(primaryButtonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
        PrimaryButton(
            onClick = onSecondaryButtonClick,
            text = stringResource(secondaryButtonTextRes),
            modifier = Modifier.padding(bottom = 20.dp),
        )
    }
}

/**
 * 로딩 인디케이터 + 텍스트
 */
@Composable
fun LoadingWithText(
    textRes: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        ProgressIndicator()
        StyledText(
            text = stringResource(textRes),
        )
    }
}