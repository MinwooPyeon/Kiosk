package com.pixelro.nenoonkiosk.ui.strabismustest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.strabismustest.ui.theme.StrabismusTestTheme

class FudoAdjustmentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()
                var showDialog by remember { mutableStateOf(false) }

                StrabismusTestTheme {
                    FudoAdjustmentScreen(
                        onNextClicked = { answer, difference ->
                            val resultFragment = FudoResultFragment().apply {
                                arguments = Bundle().apply {
                                    putInt("answer", answer)
                                    putFloat("difference", difference)
                                }
                            }
                            navController.navigate("fudo_result/$answer/$difference")
                        },
                        onBackClicked = {
                            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }
                    )

                    if (showDialog) {
                        FudoHowToDialog(onDismissRequest = { showDialog = false })
                    }

                }
            }
        }
    }
}
