package com.pixelro.nenoonkiosk.feature.inspection.strabismus.phoria.intro

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.common.component.InspectionIntroScreen
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.phoria.howtodialog.PhoriaHowToDialog
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

@Composable
fun PhoriaIntroScreen(
    onStartClicked: () -> Unit,
    onHowToClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    InspectionIntroScreen(
        title = StringProvider.getStringComposable(R.string.sawi_intro_title),
        description = StringProvider.getStringComposable(R.string.sawi_intro_description),
        onStartClicked = onStartClicked,
        onBackClicked = onBackClicked,
        howToDialog = { onDismiss -> PhoriaHowToDialog(onDismissRequest = onDismiss) }
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PhoriaIntroScreenHorizontalPreview() {
    NenoonKioskTheme {
        PhoriaIntroScreen({}, {}, {})
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PhoriaIntroScreenVerticalPreview() {
    NenoonKioskTheme {
        PhoriaIntroScreen({}, {}, {})
    }
}

