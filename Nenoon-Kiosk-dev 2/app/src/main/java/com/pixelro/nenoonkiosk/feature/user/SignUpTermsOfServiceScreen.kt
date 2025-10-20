package com.pixelro.nenoonkiosk.feature.user

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.feature.intro.TermsOfServiceCheckboxes
import com.pixelro.nenoonkiosk.feature.intro.TermsOfServiceTable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpTermsOfServiceViewModel @Inject constructor() : ViewModel() {
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
fun SignUpTermsOfServiceScreen(
    onTermsAccepted: () -> Unit,
    onTermsRejected: () -> Unit,
    viewModel: SignUpTermsOfServiceViewModel = hiltViewModel(),
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

                // Title
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xff1d71e1),
                                fontSize = 40.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(StringProvider.getString(R.string.signup_terms_title_primary) + "\n")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(StringProvider.getString(R.string.signup_terms_title_secondary))
                        }
                    },
                    style = TextStyle(
                        textAlign = TextAlign.Center, lineHeight = 40.sp
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Description
                Text(
                    text = StringProvider.getString(R.string.signup_terms_description),
                    style = TextStyle(fontSize = textSize),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(30.dp))

                // Personal Information Collection and Use Details
                Column {
                    TermsOfServiceTable(
                        label = StringProvider.getString(R.string.signup_personal_info_table_label),
                        column1 = StringProvider.getString(R.string.signup_personal_info_table_column1),
                        column2 = StringProvider.getString(R.string.signup_personal_info_table_column2),
                        column3 = StringProvider.getString(R.string.signup_personal_info_table_column3),
                        textSize = textSize,
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    TermsOfServiceCheckboxes(
                        description = StringProvider.getString(R.string.signup_personal_info_checkbox_description),
                        question = StringProvider.getString(R.string.signup_personal_info_checkbox_question),
                        accepted = acceptedPersonalInformationTerms,
                        onAcceptedChange = { viewModel.onPersonalInformationTermsAcceptedChange(it) },
                        textSize = textSize,
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Sensitive Information Collection and Use Details
                Column {
                    TermsOfServiceTable(
                        label = StringProvider.getString(R.string.signup_sensitive_info_table_label),
                        column1 = StringProvider.getString(R.string.signup_sensitive_info_table_column1),
                        column2 = StringProvider.getString(R.string.signup_sensitive_info_table_column2),
                        column3 = StringProvider.getString(R.string.signup_sensitive_info_table_column3),
                        textSize = textSize,
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    TermsOfServiceCheckboxes(
                        description = StringProvider.getString(R.string.signup_sensitive_info_checkbox_description),
                        question = StringProvider.getString(R.string.signup_sensitive_info_checkbox_question),
                        accepted = acceptedSensitiveInformationTerms,
                        onAcceptedChange = { viewModel.onSensitiveInformationTermsAcceptedChange(it) },
                        textSize = textSize,
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


@Preview(showBackground = true, widthDp = 888, heightDp = 1422)
@Composable
fun SignUpTermsOfServiceScreenPreview() {
    SignUpTermsOfServiceScreen(
        onTermsAccepted = {},
        onTermsRejected = {},
    )
}
