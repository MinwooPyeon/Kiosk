package com.pixelro.nenoonkiosk.user

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.data.StringProvider
import com.pixelro.nenoonkiosk.ui.components.PrimaryButton
import com.pixelro.nenoonkiosk.ui.components.ProgressIndicator
import com.pixelro.nenoonkiosk.ui.components.StyledText
import kotlinx.coroutines.launch

@Composable
fun IdPasswordSignInScreen(
    updateIsSignedIn: (Boolean) -> Unit,
    signInViewModel: SignInViewModel,
    navController: NavController,
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false) }
    var signupSuccess by remember { mutableStateOf(false) }
    var signInError by remember { mutableStateOf(false) }
    var signingIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        signupSuccess = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (signupSuccess) Arrangement.Center else Arrangement.Top
    ) {
        StyledText(StringProvider.getString(R.string.id_pw_sign_in_title), com.pixelro.nenoonkiosk.ui.components.TextStyle.Title)

        InputTextField(
            value = id,
            onValueChange = { id = it },
            label = StringProvider.getString(R.string.id_pw_sign_in_id_hint),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        Spacer(modifier = Modifier.height(20.dp))

        InputTextField(
            value = password,
            onValueChange = { password = it },
            label = StringProvider.getString(R.string.id_pw_sign_in_pw_hint),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(if (passwordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off),
                        contentDescription = if (passwordVisible) StringProvider.getString(R.string.id_pw_sign_in_pw_hide) else StringProvider.getString(R.string.id_pw_sign_in_pw_show)
                    )
                }
            },
        )

        Spacer(modifier = Modifier.weight(0.5f))

        if (signingIn) {
            ProgressIndicator()
            Spacer(modifier = Modifier.weight(0.5f))
        }

        if (signInError) {
            StyledText(
                text = StringProvider.getString(R.string.toast_input_id_pw),
                style = com.pixelro.nenoonkiosk.ui.components.TextStyle.Error
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        PrimaryButton(
            text = StringProvider.getString(R.string.id_pw_sign_in_button),
            onClick = {
                coroutineScope.launch {
                    signingIn = true
                    signInViewModel.userSignIn(id, password, updateIsSignedIn).also { success ->
                        signInError = !success
                        signingIn = false
                    }
                }
            },
            enabled = id.isNotBlank() && password.isNotBlank()
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
            text = StringProvider.getString(R.string.button_back),
            onClick = {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        )
    }
}

@Composable
private fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 30.sp,
            ),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = colorResource(if (isError) R.color.error else R.color.gray2)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(start = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (value.isEmpty()) {
                            StyledText(label, com.pixelro.nenoonkiosk.ui.components.TextStyle.Hint)
                        }
                        innerTextField()
                    }
                    trailingIcon?.invoke()
                }
            }
        )
        if (isError && errorMessage != null) {
            StyledText(
                text = errorMessage,
                style = com.pixelro.nenoonkiosk.ui.components.TextStyle.InputError,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp)
            )
        }
    }
}
