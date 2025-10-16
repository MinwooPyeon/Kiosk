package com.pixelro.nenoonkiosk.feature.screen

import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.constants.AppConstants
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.feature.components.Logo
import com.pixelro.nenoonkiosk.feature.components.SettingsButton
import com.pixelro.nenoonkiosk.feature.theme.cafe24Family

//시작 버튼 있는 화면
@Composable
fun IntroScreen(
    toSurveyScreen: () -> Unit,
    toSettingsScreen: () -> Unit
) {
    val transition = rememberInfiniteTransition()
    val alphaVal by transition.animateFloat(
        initialValue = 1f, targetValue = 0f, animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 700
            },
            repeatMode = RepeatMode.Reverse
        )
    )
    val logoClickedCount = remember { mutableStateOf(0) }
    /**
     * 맥박수 연결
     */
    val context = LocalContext.current
    val openFitcareHeartoolApp: () -> Unit = {
        /**
         * Fitcare Heartool 앱을 열기 위한 Intent 생성
         */
        val intent = context.packageManager.getLaunchIntentForPackage("cn.fitcare.heartool")
        if (intent != null) {
            /**
             * Intent를 처리할 수 있는 앱이 있으면 앱을 엽니다.
             */
            context.startActivity(intent)
        } else {
            /**
             * 처리할 수 있는 앱이 없으면 사용자에게 알립니다.
             */
            Toast.makeText(context, "Fitcare Heartool 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /**
             * 상단 바
             */
            Box(
                modifier = Modifier
                    .padding(
                        start = 40.dp,
                        end = 40.dp,
                        bottom = 20.dp
                    )
                    .fillMaxWidth()
                    .height(40.dp)
            ) {

                SettingsButton(toSettingsScreen)

            }
            /**
             * 로고
             */
            Logo()
            Spacer(
                modifier = Modifier
                    .height(40.dp)
            )
            Text(
                text = StringProvider.getString(R.string.intropage_appname, ),
                fontSize = 60.sp,
                fontFamily = cafe24Family,

                textAlign = TextAlign.Center,
                color = Color(0xFF1D71E1)
            )
            Text(
                text = StringProvider.getString(R.string.intropage_title, ),
                fontSize = 36.sp,
                fontFamily = cafe24Family,

                textAlign = TextAlign.Center,
                color = Color(0xFF1D71E1),
                modifier = Modifier.padding(horizontal = 40.dp)
            )
            /**
             * 버전 표시
             */
            Text(
                modifier = Modifier
                    .padding(top = 20.dp, end = 50.dp)
                    .fillMaxWidth(),
                text = "${AppConstants.APP_VERSION}",
                fontSize = 20.sp,
                fontFamily = cafe24Family,
                textAlign = TextAlign.End,
                color = Color(0xff848484)
            )
            Spacer(
                modifier = Modifier
                    .height(150.dp)
            )
            /**
             * 시작 버튼
             */
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(520.dp)
                        .height(240.dp)
                        .alpha(alphaVal)
                        .border(
                            border = BorderStroke(20.dp, Color(0xFF1D71E1)),
                            shape = RoundedCornerShape(26.dp)
                        )
                )
                Box(
                    modifier = Modifier
                        .width(440.dp)
                        .height(160.dp)
                        .clip(
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = Color(0xFF1D71E1),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            toSurveyScreen()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = StringProvider.getString(
                            R.string.intropage_start_button),
                        fontSize = 75.sp,
                        color = Color(0xFFFFFFFF),
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Text(
                modifier = Modifier
                    .padding(top = 50.dp),
                text = StringProvider.getString(
                    R.string.intropage_description1,
                    
                ),
                fontSize = 28.sp,
                color = Color(0xFF1D71E1),
                fontWeight = FontWeight.Bold
            )
            Text(
                modifier = Modifier
                    .padding(top = 5.dp),
                text = StringProvider.getString(
                    R.string.intropage_description2,
                    
                ),
                fontSize = 28.sp,
                color = Color(0xFF1D71E1),
                fontWeight = FontWeight.Bold
            )
        }
    }
}