package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import incar.mobile.caring.admin.model.Adjuster
import incar.mobile.caring.admin.viewmodel.AdjusterListUiState
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdjusterListScreen(
    onLogout: () -> Unit,
) {
    val viewModel: AdjusterListViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "손해사정사 관리",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                    )
                    if (uiState is AdjusterListUiState.Success) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Text("↻", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(onClick = onLogout) {
                        Text("로그아웃")
                    }
                }

                when (val state = uiState) {
                    is AdjusterListUiState.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    is AdjusterListUiState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(state.message, color = MaterialTheme.colorScheme.error)
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(onClick = { viewModel.refresh() }) { Text("다시 시도") }
                            }
                        }
                    }
                    is AdjusterListUiState.Success -> {
                        if (state.adjusters.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("등록된 손해사정사가 없습니다.")
                            }
                        } else {
                            AdjusterTable(state.adjusters)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AdjusterTable(adjusters: List<Adjuster>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "총 ${adjusters.size}명",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            TableCell("이름",   weight = 1.5f, header = true)
            TableCell("소속",   weight = 2f,   header = true)
            TableCell("연락처", weight = 2f,   header = true)
            TableCell("주소",   weight = 3f,   header = true)
            TableCell("경력",   weight = 1f,   header = true)
            TableCell("평점",   weight = 1f,   header = true)
            TableCell("노출",   weight = 1f,   header = true)
        }
        HorizontalDivider()
        LazyColumn {
            items(adjusters) { adjuster ->
                AdjusterRow(adjuster)
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun AdjusterRow(adjuster: Adjuster) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TableCell(adjuster.name,                              weight = 1.5f)
        TableCell(adjuster.company.ifBlank { "-" },           weight = 2f)
        TableCell(adjuster.phone,                             weight = 2f)
        TableCell(adjuster.address.ifBlank { "-" },           weight = 3f)
        TableCell("${adjuster.careerYears}년",                weight = 1f)
        TableCell(adjuster.reviewScore?.let { "%.1f".format(it) } ?: "-", weight = 1f)
        Box(modifier = Modifier.weight(1f)) {
            Badge(
                containerColor = if (adjuster.isVisible)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.errorContainer,
            ) {
                Text(if (adjuster.isVisible) "노출" else "숨김")
            }
        }
    }
}

@Composable
private fun RowScope.TableCell(text: String, weight: Float, header: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = if (header) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
    )
}
