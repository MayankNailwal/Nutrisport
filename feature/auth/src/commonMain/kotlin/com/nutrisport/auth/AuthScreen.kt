package com.nutrisport.auth

import ContentWithMessageBar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import com.nutrisport.auth.component.GoogleButton
import com.nutrisport.shared.Alpha
import com.nutrisport.shared.FontSize
import com.nutrisport.shared.Surface
import com.nutrisport.shared.TextPrimary
import com.nutrisport.shared.TextSecondary
import com.nutrisport.shared.bebasNeueFont
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import rememberMessageBarState

@Composable
fun AuthScreen() {

    val messageBarState = rememberMessageBarState()
    var loadingState by remember { mutableStateOf(false) }

    Scaffold { paddingValues ->
        ContentWithMessageBar(
            contentBackgroundColor = Surface,
            modifier = Modifier.padding(
                top = paddingValues.calculateTopPadding(),
                bottom = paddingValues.calculateBottomPadding()
            ),
            messageBarState = messageBarState,
            errorMaxLines = 2
        ) {
            Column(modifier = Modifier
                .fillMaxSize().padding(24.dp)
            ) {
                Column (
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = "NUTRISPORT",
                        textAlign = TextAlign.Center,
                        fontFamily = bebasNeueFont(),
                        fontSize = FontSize.EXTRA_LARGE,
                        color = TextSecondary
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth().alpha(Alpha.HALF),
                        text = "Sign in to continue",
                        textAlign = TextAlign.Center,
                        fontSize = FontSize.EXTRA_REGULAR,
                        color = TextPrimary
                    )
                }
                GoogleButtonUiContainerFirebase(
                    linkAccount = false,
                    onResult = { result ->
                        CoroutineScope(Dispatchers.Main).launch {
                            result.onSuccess { user ->
                                messageBarState.addSuccess("Authentication Successful")
                                loadingState = false
                            }.onFailure { error ->
                                if(error.message?.contains("A network error") == true) {
                                    messageBarState.addError("Internet Connection Unavailable")
                                } else if(error.message?.contains("Idtoken is null") == true) {
                                    messageBarState.addError("Sign in canceled.")
                                    print("${error.message}")
                                } else {
                                    messageBarState.addError(error.message ?: "Unknown")
                                }
                                loadingState = false
                            }
                        }
                    },
                    content = {
                        GoogleButton (
                            loading = loadingState
                        ){
                            loadingState = true
                            this@GoogleButtonUiContainerFirebase.onClick()
                        }
                    }
                )
            }
        }

    }
}
