package com.pixelro.nenoonkiosk.core.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.dataprovider.TestType

/**
 * Dialog 내용
 */
@Composable
fun SurveyRecommendationDialog(
    onDismissRequest: () -> Unit,
    toTestScreen: (TestType) -> Unit,
    toIntroScreen: () -> Unit,
    selectedTest: TestType,
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(),
    ) {
        Column(
            modifier =
                Modifier
                    .width(800.dp)
                    .height(1000.dp)
                    .background(
                        color = Color(0xffffffff),
                    ),
        ) {
            Text(
                modifier =
                    Modifier
                        .padding(20.dp),
                text =
                    buildAnnotatedString {
                        withStyle(
                            style =
                                SpanStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 42.sp,
                                ),
                        ) {
                            append(
                                StringProvider.getString(
                                    R.string.test_list_dialog_warning1,
                                ),
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.test_list_dialog_warning2,
                            ),
                        )
                        withStyle(
                            style =
                                SpanStyle(
                                    color = Color(0xff1d71e1),
                                ),
                        ) {
                            append(
                                " \'" +
                                    StringProvider.getString(
                                        R.string.navigation_tosurvey_button,
                                    ) + "\' ",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.test_list_dialog_warning3,
                            ),
                        )
                        withStyle(
                            style =
                                SpanStyle(
                                    color = Color(0xff1d71e1),
                                ),
                        ) {
                            append(
                                " \'" +
                                    StringProvider.getString(
                                        R.string.test_list_dialog_re_button,
                                    ) + "\' ",
                            )
                        }
                        append(
                            StringProvider.getString(
                                R.string.test_list_dialog_warning4,
                            ),
                        )
                    },
                fontSize = 32.sp,
                fontWeight = FontWeight.Medium,
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(
                                        RoundedCornerShape(8.dp),
                                    )
                                    .border(
                                        border =
                                            BorderStroke(
                                                width = 1.dp,
                                                color = Color(0xff999999),
                                            ),
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .background(
                                        color = Color(0x00000000),
                                        RoundedCornerShape(8.dp),
                                    )
                                    .clickable {
                                        onDismissRequest()
                                        toIntroScreen()
                                    }
                                    .padding(top = 4.dp),
                            text =
                                StringProvider.getString(
                                    R.string.navigation_tosurvey_button,
                                ),
                            textAlign = TextAlign.Center,
                            fontSize = if (savedLanguage == "ru") 20.sp else 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(
                            modifier =
                                Modifier
                                    .width(20.dp),
                        )
                        Text(
                            modifier =
                                Modifier
                                    .padding(top = 20.dp, end = 20.dp, bottom = 20.dp)
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .clip(
                                        RoundedCornerShape(8.dp),
                                    )
                                    .border(
                                        border =
                                            BorderStroke(
                                                width = 1.dp,
                                                color = Color(0xff999999),
                                            ),
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .background(
                                        color = Color(0x00000000),
                                        RoundedCornerShape(8.dp),
                                    )
                                    .clickable {
                                        onDismissRequest()
                                        toTestScreen(selectedTest)
                                    }
                                    .padding(top = 4.dp),
                            text =
                                StringProvider.getString(
                                    R.string.test_list_dialog_re_button,
                                ),
                            textAlign = TextAlign.Center,
                            fontSize = if (savedLanguage == "ru") 20.sp else 32.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Text(
                        modifier =
                            Modifier
                                .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .clip(
                                    RoundedCornerShape(8.dp),
                                )
                                .border(
                                    border =
                                        BorderStroke(
                                            width = 1.dp,
                                            color = Color(0xff999999),
                                        ),
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .background(
                                    color = Color(0x00000000),
                                    RoundedCornerShape(8.dp),
                                )
                                .clickable {
                                    onDismissRequest()
                                }
                                .padding(top = 4.dp),
                        text =
                            StringProvider.getString(
                                R.string.test_list_dialog_cancel_button,
                            ),
                        textAlign = TextAlign.Center,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}
