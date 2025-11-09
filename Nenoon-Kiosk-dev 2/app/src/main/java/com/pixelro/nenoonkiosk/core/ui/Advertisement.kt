package com.pixelro.nenoonkiosk.core.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harang.data.repository.AdImageRepository
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import dagger.hilt.android.EntryPointAccessors

/**
 * 광고 내용
 */
@Composable
fun Advertisement(idx: Int) {
    val context = LocalContext.current
    val sharedPreferences =
        remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage =
        sharedPreferences.getString("language", "ko") ?: "ko"

    // Hilt EntryPoint를 통해 Repository 가져오기
    val repository = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            AdImageRepositoryEntryPoint::class.java
        ).adImageRepository()
    }

    // locationId 1 = TEST_LIST_SCREEN
    val adImages by repository.getAdImagesByLocationAndLanguage(1, savedLanguage).collectAsState(initial = emptyList())

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(300.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = Color(0xffffffff),
            ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (adImages.isNotEmpty()) {
                val imageUrl = adImages[idx % adImages.size].url
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "광고",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}

@dagger.hilt.EntryPoint
@dagger.hilt.InstallIn(dagger.hilt.components.SingletonComponent::class)
interface AdImageRepositoryEntryPoint {
    fun adImageRepository(): AdImageRepository
}
