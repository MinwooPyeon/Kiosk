package com.pixelro.nenoonkiosk.ui.screen

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.constants.NavConstants
import com.pixelro.nenoonkiosk.data.StringProvider
import com.pixelro.nenoonkiosk.ui.components.PrimaryButton
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TermsOfServiceViewModel @Inject constructor() : ViewModel() {
    private val _acceptedPersonalInformationTerms = mutableStateOf<Boolean?>(null)
    val acceptedPersonalInformationTerms: State<Boolean?> = _acceptedPersonalInformationTerms

    private val _acceptedSensitiveInformationTerms = mutableStateOf<Boolean?>(null)
    val acceptedSensitiveInformationTerms: State<Boolean?> = _acceptedSensitiveInformationTerms

    fun onPersonalInformationTermsAcceptedChange(newValue: Boolean?) {
        _acceptedPersonalInformationTerms.value = newValue
    }
    fun onSensitiveInformationTermsAcceptedChange(newValue: Boolean?){
        _acceptedSensitiveInformationTerms.value = newValue
    }
}

@Composable
fun TermsOfServiceScreen(
    onTermsAccepted: () -> Unit,
    onTermsRejected: () -> Unit,
    viewModel: TermsOfServiceViewModel = hiltViewModel(),
) {
    val acceptedPersonalInformationTerms by viewModel.acceptedPersonalInformationTerms
    val acceptedSensitiveInformationTerms by viewModel.acceptedSensitiveInformationTerms

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val textSize = if (savedLanguage == "en") 16.sp else 20.sp

    Scaffold { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(40.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly,
            ) {
                Spacer(modifier = Modifier.height(30.dp))

                // 제목
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color(0xff1d71e1), fontSize = 40.sp, fontWeight = FontWeight.SemiBold)) {
                            append(StringProvider.getString(R.string.terms_of_service_title_primary) + "\n")
                        }
                        withStyle(style = SpanStyle(fontSize = 26.sp, fontWeight = FontWeight.Bold)) {
                            append(StringProvider.getString(R.string.terms_of_service_title_secondary))
                        }
                    },
                    style = TextStyle(
                        textAlign = TextAlign.Center, lineHeight = 40.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 설명
                Text(
                    text = StringProvider.getString(R.string.terms_of_service_description),
                    style = TextStyle(fontSize = textSize),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                // 개인정보 수집 이용 내역
                Column {
                    TermsOfServiceTable(
                        label = StringProvider.getString(R.string.personal_info_table_label),
                        column1 = StringProvider.getString(R.string.personal_info_table_column1),
                        column2 = StringProvider.getString(R.string.personal_info_table_column2),
                        column3 = StringProvider.getString(R.string.personal_info_table_column3),
                        textSize = textSize,
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    TermsOfServiceCheckboxes(
                        description = StringProvider.getString(R.string.personal_info_checkbox_description),
                        question = StringProvider.getString(R.string.personal_info_checkbox_question),
                        accepted = acceptedPersonalInformationTerms,
                        textSize = textSize,
                        onAcceptedChange = { viewModel.onPersonalInformationTermsAcceptedChange(it) }
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // 민감정보 수집 이용 내역
                Column {
                    TermsOfServiceTable(
                        label = StringProvider.getString(R.string.sensitive_info_table_label),
                        column1 = StringProvider.getString(R.string.sensitive_info_table_column1),
                        column2 = StringProvider.getString(R.string.sensitive_info_table_column2),
                        column3 = StringProvider.getString(R.string.sensitive_info_table_column3),
                        textSize = textSize,
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    TermsOfServiceCheckboxes(
                        description = StringProvider.getString(R.string.sensitive_info_checkbox_description),
                        question = StringProvider.getString(R.string.sensitive_info_checkbox_question),
                        accepted = acceptedSensitiveInformationTerms,
                        textSize = textSize,
                        onAcceptedChange = { viewModel.onSensitiveInformationTermsAcceptedChange(it) }
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    onClick = {
                        if (acceptedPersonalInformationTerms == true && acceptedSensitiveInformationTerms == true) {
                            onTermsAccepted()
                        }
                    },
                    enabled = acceptedPersonalInformationTerms == true && acceptedSensitiveInformationTerms == true,
                    text = StringProvider.getString(R.string.button_agree)
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    onClick = {
                        onTermsRejected()
                    },
                    text = StringProvider.getString(R.string.back)
                )
            }
        }
    }
}

@Composable
fun TermsOfServiceTable(
    label: String,
    column1: String,
    column2: String,
    column3: String,
    isEvenlySpaced: Boolean = false,
    textSize: TextUnit,
) {isEvenlySpaced

    val strokeWidthDp = 1.dp
    val cellPaddingDp = 8.dp

    val density = LocalDensity.current
    val strokeWidthPx = with(density) { strokeWidthDp.toPx() }
    val cellPaddingPx = with(density) { cellPaddingDp.toPx() }

    var height by remember { mutableStateOf(0f) }
    var firstColumnHeight by remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0f) }

    Text(
        text = label,
        style = TextStyle(fontSize = textSize, background = Color(0xffffff00), fontWeight = FontWeight.SemiBold),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(8.dp))

    Column(modifier = Modifier
        .onGloballyPositioned {
            height = it.size.height.toFloat()
            width = it.size.width.toFloat()
        }
        .drawWithContent {
            drawContent()
            drawLine(
                color = Color(0xff000000),
                start = Offset(0f, firstColumnHeight),
                end = Offset(width, firstColumnHeight),
                strokeWidth = strokeWidthPx
            )
            drawLine(
                color = Color(0xff000000),
                start = Offset(width * if (isEvenlySpaced) 0.33f else 0.25f, 0f),
                end = Offset(width * if (isEvenlySpaced) 0.33f else 0.25f, height),
                strokeWidth = strokeWidthPx
            )
            drawLine(
                color = Color(0xff000000),
                start = Offset(width * if (isEvenlySpaced) 0.67f else 0.75f, 0f),
                end = Offset(width * if (isEvenlySpaced) 0.67f else 0.75f, height),
                strokeWidth = strokeWidthPx
            )
        }
        .fillMaxWidth()
        .border(1.dp, Color.Black, shape = RectangleShape)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xffebebeb))
                .onGloballyPositioned {
                    firstColumnHeight = it.size.height.toFloat()
                }
        ) {
            Text(
                text = StringProvider.getString(R.string.table_header_item),
                modifier = Modifier
                    .weight(1f)
                    .padding(cellPaddingDp),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = textSize, textAlign = TextAlign.Center)
            )
            Text(
                text = StringProvider.getString(R.string.table_header_purpose),
                modifier = Modifier
                    .weight(if (isEvenlySpaced) 1f else 2f)
                    .padding(cellPaddingDp),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = textSize, textAlign = TextAlign.Center)
            )
            Text(
                text = StringProvider.getString(R.string.table_header_period),
                modifier = Modifier
                    .weight(1f)
                    .padding(cellPaddingDp),
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = textSize, textAlign = TextAlign.Center),
                textAlign = TextAlign.Center
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = column1,
                modifier = Modifier
                    .weight(1f)
                    .padding(cellPaddingDp),
                style = TextStyle(fontSize = textSize, textAlign = TextAlign.Center)
            )
            Text(
                text = column2,
                modifier = Modifier
                    .weight(if (isEvenlySpaced) 1f else 2f)
                    .padding(cellPaddingDp),
                style = TextStyle(fontSize = textSize, textAlign = TextAlign.Center)
            )
            Text(
                text = column3,
                modifier = Modifier
                    .weight(1f)
                    .padding(cellPaddingDp),
                style = TextStyle(fontSize = textSize, textAlign = TextAlign.Center),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun TermsOfServiceCheckboxes(
    description: String,
    question: String,
    accepted: Boolean?,
    onAcceptedChange: (Boolean?) -> Unit,
    textSize: TextUnit
) {

    Text(
        text = description,
        style = TextStyle(fontSize = textSize)
    )

    Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = question,
            style = TextStyle(fontSize = textSize),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = StringProvider.getString(R.string.checkbox_agree),
            color = Color(0xff1d71e1),
            style = TextStyle(fontSize = textSize)
        )
        Checkbox(
            checked = accepted == true,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xff1d71e1),
                uncheckedColor = Color(0xff1d71e1),
                checkmarkColor = Color(0xff1d71e1)
            ),
            onCheckedChange = { onAcceptedChange(true) },
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = StringProvider.getString(R.string.checkbox_disagree),
            color = Color(0xff1d71e1),
            style = TextStyle(fontSize = textSize)
        )
        Checkbox(
            checked = accepted == false,
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xff1d71e1),
                uncheckedColor = Color(0xff1d71e1),
                checkmarkColor = Color(0xff1d71e1)
            ),
            onCheckedChange = { onAcceptedChange(false) },
        )
    }
}


@Preview(showBackground = true, widthDp = 888, heightDp = 1422)
@Composable
fun TermsOfServiceScreenPreview() {
    TermsOfServiceScreen(
        onTermsAccepted = {},
        onTermsRejected = {},
    )
}