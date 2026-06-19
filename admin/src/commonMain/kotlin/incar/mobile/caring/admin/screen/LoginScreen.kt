package incar.mobile.caring.admin.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import incar.mobile.caring.admin.BuildConfig
import incar.mobile.caring.admin.viewmodel.LoginUiState
import incar.mobile.caring.admin.viewmodel.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(onLoginSuccess: (token: String) -> Unit) {
    val viewModel: LoginViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    var id by remember { mutableStateOf(viewModel.savedId) }
    var pw by remember { mutableStateOf(viewModel.savedPw) }
    var autoLogin by remember { mutableStateOf(viewModel.autoLoginEnabled) }

    LaunchedEffect(uiState) {
        if (uiState is LoginUiState.Success) {
            onLoginSuccess((uiState as LoginUiState.Success).token)
            viewModel.resetState()
        }
    }

    val envLabel = when (BuildConfig.ENV) {
        "release" -> "Production"
        "stage" -> "Stage"
        else -> "Dev"
    }

    Surface(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Card(
                    modifier = Modifier.width(400.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Caring Admin",
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        SuggestionChip(
                            onClick = {},
                            label = { Text(envLabel) },
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = id,
                            onValueChange = { id = it },
                            label = { Text("아이디") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = uiState !is LoginUiState.Loading,
                        )

                        OutlinedTextField(
                            value = pw,
                            onValueChange = { pw = it },
                            label = { Text("비밀번호") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onKeyEvent { event ->
                                    if (event.key == Key.Enter) {
                                        viewModel.login(id, pw, saveLogin = autoLogin)
                                        true
                                    } else false
                                },
                            enabled = uiState !is LoginUiState.Loading,
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = autoLogin,
                                onCheckedChange = { autoLogin = it },
                                enabled = uiState !is LoginUiState.Loading,
                            )
                            Text(
                                text = "자동 로그인",
                                style = MaterialTheme.typography.bodyMedium,
                            )
                        }

                        if (uiState is LoginUiState.Error) {
                            Text(
                                text = (uiState as LoginUiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }

                        Button(
                            onClick = { viewModel.login(id, pw, saveLogin = autoLogin) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            enabled = uiState !is LoginUiState.Loading,
                        ) {
                            if (uiState is LoginUiState.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                Text("로그인")
                            }
                        }
                    }
                }
            }
        }
}
