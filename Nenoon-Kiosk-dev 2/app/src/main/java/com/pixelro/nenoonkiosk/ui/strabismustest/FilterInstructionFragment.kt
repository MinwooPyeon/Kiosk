package com.pixelro.nenoonkiosk.ui.strabismustest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import com.example.strabismustest.ui.theme.StrabismusTestTheme

class FilterInstructionFragment : Fragment() {

    companion object {
        private const val ARG_TEST_TYPE = "test_type"
        private const val ARG_IS_WEARING_GLASSES = "is_wearing_glasses"
        const val TEST_TYPE_SAWI = "sawi"
        const val TEST_TYPE_FUDO = "fudo"

        fun newInstance(testType: String, isWearingGlasses: Boolean): FilterInstructionFragment {
            val fragment = FilterInstructionFragment()
            val args = Bundle()
            args.putString(ARG_TEST_TYPE, testType)
            args.putBoolean(ARG_IS_WEARING_GLASSES, isWearingGlasses)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val navController = findNavController()
                val testType = arguments?.getString(ARG_TEST_TYPE) ?: ""
                val isWearingGlasses = arguments?.getBoolean(ARG_IS_WEARING_GLASSES) ?: false
                var showDialog by remember { mutableStateOf(false) }

                StrabismusTestTheme {
                    FilterInstructionScreen(
                        testType = testType,
                        isWearingGlasses = isWearingGlasses,
                        onNextClicked = {
                            when (testType) {
                                TEST_TYPE_SAWI -> {
                                    navController.navigate("sawi_question")
                                }
                                TEST_TYPE_FUDO -> {
                                    navController.navigate("fudo_question")
                                }
                            }
                        },
                        onBackClicked = {
                            parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                        },
                    )

                    if (showDialog) {
                        when (testType) {
                            TEST_TYPE_SAWI -> SawiHowToDialog(onDismissRequest = { showDialog = false })
                            TEST_TYPE_FUDO -> FudoHowToDialog(onDismissRequest = { showDialog = false })
                        }
                    }
                }
            }
        }
    }
}
