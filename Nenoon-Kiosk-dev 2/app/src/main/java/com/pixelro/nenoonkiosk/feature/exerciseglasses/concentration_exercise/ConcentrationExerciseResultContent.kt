package com.pixelro.nenoonkiosk.feature.exerciseglasses.concentration_exercise

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun ConcentrationExerciseResultContent(
    testResult: ConcentrationExerciseResult,
    navController: NavHostController,
) {
    val result = testResult
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(
                        start = 20.dp,
                        end = 20.dp,
                        top = 100.dp,
                        bottom = 20.dp,
                    )
                    .fillMaxWidth()
                    .height(300.dp)
                    .border(
                        border = BorderStroke(1.dp, Color(0xffffffff)),
                        shape = RoundedCornerShape(8.dp),
                    ),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Text(
                modifier =
                    Modifier
                        .padding(start = 40.dp, top = 40.dp, bottom = 10.dp),
                text =
                    StringProvider.getString(
                        R.string.test_result_my_result,
                    ),
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xffffffff),
            )
            Text(
                modifier =
                    Modifier
                        .padding(start = 40.dp, top = 20.dp),
                text = StringProvider.getString(R.string.stage) + "${testResult.concentrationExerciseValue} ",
                fontSize = 70.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xffffffff),
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier =
                    Modifier
                        .padding(top = 20.dp),
                text = StringProvider.getString(R.string.test_complete),
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xffffffff),
            )
            Text(
                modifier =
                    Modifier
                        .padding(top = 115.dp, bottom = 10.dp),
                text =
                    StringProvider.getString(
                        R.string.experience_glasses,
                    ),
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xffffffff),
            )
        }
        Box(
            modifier =
                Modifier
                    .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                modifier =
                    Modifier
                        .padding(top = 20.dp, bottom = 10.dp),
                text = StringProvider.getString(R.string.consistently_wear),
                fontSize = 30.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xffffffff),
            )
        }
    }
}
