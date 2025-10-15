
package com.pixelro.nenoonkiosk.ui.strabismustest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.strabismustest.ui.theme.StrabismusTestTheme

class FudoResultFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                StrabismusTestTheme {
                    val answer = arguments?.getInt("answer")
                    val difference = arguments?.getFloat("difference")

                    FudoResultScreen(
                        answer = answer,
                        difference = difference,
                        onPrintClicked = {
                            Toast.makeText(requireContext(), "프린트 기능은 아직 구현되지 않았습니다.", Toast.LENGTH_SHORT).show()
                        },
                        onBackToMainClicked = {
                            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        }
                    )
                }
            }
        }
    }
}
