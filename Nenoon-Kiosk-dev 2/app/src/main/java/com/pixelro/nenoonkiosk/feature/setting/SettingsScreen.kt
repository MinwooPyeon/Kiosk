package com.pixelro.nenoonkiosk.feature.setting

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.main.NenoonViewModel
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel

// 환경설정 뷰
@RequiresApi(Build.VERSION_CODES.S)
@Composable
fun SettingsScreen(
    viewModel: NenoonViewModel,
    isSignedIn: Boolean,
    toSignInScreen: () -> Unit,
    toSoftwareInfoScreen: () -> Unit,
    loginViewModel: LoginViewModel,
    onBack: () -> Unit,
) {
    val settingsDialogState by viewModel.settingsDialogState.collectAsState()
    val isUserSignedIn by loginViewModel.isUserSignedIn.collectAsState()
    val isLocationSignedIn by loginViewModel.isLocationSignedIn.collectAsState()
    val isSeniorValue by viewModel.isSenior.collectAsState()
    val context = LocalContext.current

    when (settingsDialogState) {
        NenoonViewModel.SettingsDialogState.Language -> {
            LanguageSelectDialog(
                updateLanguage = {
                    viewModel.updateLanguage(it)
                },
            ) {
                viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.None)
            }
        }
        NenoonViewModel.SettingsDialogState.BloodPressureMonitorType -> {
            BloodPressureMonitorSelector {
                viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.None)
            }
        }
        else -> {}
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        /**
         * 상단 바
         */
        Box(
            modifier =
                Modifier
                    .padding(top = (GlobalValue.statusBarPadding + 20).dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .height(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            onBack()
                        },
                contentAlignment = Alignment.CenterStart,
            ) {
                Image(
                    modifier =
                        Modifier
                            .padding(start = 40.dp, top = 4.dp)
                            .width(28.dp),
                    painter = painterResource(id = R.drawable.icon_back_black),
                    contentDescription = "",
                )
            }
            Text(
                textAlign = TextAlign.Center,
                text = StringProvider.getString(R.string.settings_title),
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = Color(0xffdddddd),
                    ),
        )
        /**
         * 언어
         */
        Box(
            modifier =
                Modifier
                    .clickable {
                        viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.Language)
                    },
        ) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, top = 10.dp, bottom = 10.dp),
                text = StringProvider.getString(R.string.settings_language),
                fontSize = 30.sp,
            )
        }
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = Color(0xffdddddd),
                    ),
        )
        /**
         * 시니어용 목록화면
         */
//        Row(
//            modifier = Modifier
//                .fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(
//                modifier = Modifier
//                    .padding(start = 40.dp, top = 10.dp, bottom = 10.dp),
//                text = StringProvider.getString(R.string.settings_senior),
//                fontSize = 30.sp
//            )
//            Switch(
//                modifier = Modifier
//                    .padding(end = 40.dp, top = 10.dp, bottom = 10.dp),
//                checked = isSeniorValue,
//                onCheckedChange = {
//                    viewModel.updateIsSenior(it)
//                })
//        }
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(1.dp)
//                .background(
//                    color = Color(0xffdddddd)
//                )
//        )
        /**
         * 로그인 되어 있으면 -> 로그아웃
         * 로그인 안 되어 있으면 -> 로그인
         */
        if (isLocationSignedIn) {
            Box(
                modifier =
                    Modifier
                        .clickable {
                            if (isSignedIn) {
                                if (isUserSignedIn) {
                                    loginViewModel.userSignOut()
                                } else {
                                    loginViewModel.locationSignOut()
                                }
                            }
                            toSignInScreen()
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 40.dp, top = 10.dp, bottom = 10.dp),
                    text =
                        when (isSignedIn) {
                            true ->
                                StringProvider.getString(
                                    R.string.settings_signout,
                                )
                            false -> StringProvider.getString(R.string.signin)
                        },
                    fontSize = 30.sp,
                    color =
                        when (isSignedIn) {
                            true -> Color(0xFFFF0000)
                            false -> Color(0xFF000000)
                        },
                )
            }

            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
        }

        /**
         * 혈압계 선택
         */
        Box(
            modifier =
                Modifier
                    .clickable {
                        viewModel.setSettingsDialogState(NenoonViewModel.SettingsDialogState.BloodPressureMonitorType)
                    },
        ) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 40.dp, top = 10.dp, bottom = 10.dp),
                text =
                    StringProvider.getString(
                        R.string.blood_pressure_monitor_image_content_description,
                    ) +
                        " (${SharedPreferencesManager.getBloodPressureMonitorType().name})",
                fontSize = 30.sp,
            )
        }
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = Color(0xffdddddd),
                    ),
        )
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(1.dp)
//                .background(
//                    color = Color(0xffdddddd)
//                )
//        )

        /**
         * 이용약관 (내용 없음)
         */

//        Box(
//            modifier = Modifier
//                .clickable {
//
//                }
//        ) {
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 40.dp, top = 10.dp, bottom = 10.dp),
//                text = StringProvider.getString(R.string.settings_terms),
//                fontSize = 30.sp
//            )
//        }
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(1.dp)
//                .background(
//                    color = Color(0xffdddddd)
//                )
//        )
        /**
         * 소프트웨어 정보
         */
//        Box(
//            modifier = Modifier
//                .clickable {
//                    toSoftwareInfoScreen()
//                }
//        ) {
//            Text(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(start = 40.dp, top = 10.dp, bottom = 10.dp),
//                text = StringProvider.getString(R.string.setting_software_info),
//                fontSize = 30.sp,
//                color = Color(0xFF000000)
//            )
//        }
    }
}

/**
 * 언어 선택 dialog
 */
@Composable
fun LanguageSelectDialog(
    updateLanguage: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(),
    ) {
        Column(
            modifier =
                Modifier
                    .width(600.dp)
                    .height(1000.dp)
                    .background(
                        color = Color(0xffffffff),
                        shape = RoundedCornerShape(8.dp),
                    ),
        ) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                text = StringProvider.getString(R.string.settings_language),
                fontSize = 30.sp,
            )
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            updateLanguage("ko")
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "한국어",
                    fontSize = 30.sp,
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            updateLanguage("en")
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "English",
                    fontSize = 30.sp,
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            updateLanguage("zh")
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "汉语",
                    fontSize = 30.sp,
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            updateLanguage("ja")
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "日本語",
                    fontSize = 30.sp,
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            updateLanguage("fr")
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "Français",
                    fontSize = 30.sp,
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            updateLanguage("ru")
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "Русский",
                    fontSize = 30.sp,
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            updateLanguage("es")
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "español",
                    fontSize = 30.sp,
                )
            }
        }
    }
}

/**
 * 혈압계 선택 dialog
 */
@Composable
fun BloodPressureMonitorSelector(onDismissRequest: () -> Unit) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(),
    ) {
        Column(
            modifier =
                Modifier
                    .width(600.dp)
                    .height(1000.dp)
                    .background(
                        color = Color(0xffffffff),
                        shape = RoundedCornerShape(8.dp),
                    ),
        ) {
            Text(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 20.dp),
                textAlign = TextAlign.Center,
                text =
                    StringProvider.getString(
                        R.string.blood_pressure_monitor_image_content_description,
                    ),
                fontSize = 30.sp,
            )
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            SharedPreferencesManager.putBloodPressureMonitorType(SharedPreferencesManager.BloodPressureMonitorType.BPBIO320)
                            onDismissRequest()
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "BPBIO320",
                    fontSize = 30.sp,
                )
            }
            Spacer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(
                            color = Color(0xffdddddd),
                        ),
            )
            Box(
                modifier =
                    Modifier
                        .clickable {
                            SharedPreferencesManager.putBloodPressureMonitorType(SharedPreferencesManager.BloodPressureMonitorType.BP170B)
                            onDismissRequest()
                        },
            ) {
                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 10.dp, bottom = 10.dp),
                    text = "BP170B",
                    fontSize = 30.sp,
                )
            }
        }
    }
}
