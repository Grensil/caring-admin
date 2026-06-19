package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import incar.mobile.caring.admin.data.dto.EducationRequestDto
import incar.mobile.caring.admin.viewmodel.EducationRequestUiState
import incar.mobile.caring.admin.viewmodel.EducationRequestViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EducationRequestScreen(token: String) {
    val viewModel: EducationRequestViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.load(token) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val count = (uiState as? EducationRequestUiState.Success)?.total
                Text(
                    text = if (count != null) "총 ${count}건" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = { viewModel.refresh(token) }) {
                    Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                }
            }
            HorizontalDivider()

            when (val state = uiState) {
                is EducationRequestUiState.Loading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                is EducationRequestUiState.Error ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.refresh(token) }) { Text("다시 시도") }
                        }
                    }
                is EducationRequestUiState.Success ->
                    if (state.items.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("교육 요청 내역이 없습니다.")
                        }
                    } else {
                        EducationRequestTable(state.items)
                    }
            }
        }
    }
}

@Composable
private fun EducationRequestTable(items: List<EducationRequestDto>) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            ERCell("ID",       weight = 0.5f, header = true)
            ERCell("요청자",   weight = 1.5f, header = true)
            ERCell("연락처",   weight = 1.5f, header = true)
            ERCell("손해사정사", weight = 1.5f, header = true)
            ERCell("기관명",   weight = 1.5f, header = true)
            ERCell("교육분야", weight = 1.5f, header = true)
            ERCell("희망일",   weight = 1.2f, header = true)
            ERCell("상태",     weight = 1f,   header = true)
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ERCell(item.id.toString(),                                       weight = 0.5f)
                    ERCell(item.requesterName ?: "-",                                weight = 1.5f)
                    ERCell(item.requesterPhone ?: "-",                               weight = 1.5f)
                    ERCell(item.adjusterNameFull ?: item.adjusterNameSnapshot ?: "-", weight = 1.5f)
                    ERCell(item.orgName ?: "-",                                      weight = 1.5f)
                    ERCell(item.field ?: "-",                                        weight = 1.5f)
                    ERCell(item.desiredDate?.take(10) ?: "-",                        weight = 1.2f)
                    Box(modifier = Modifier.weight(1f)) {
                        StatusBadge(item.status)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun RowScope.ERCell(text: String, weight: Float, header: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        style = if (header) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodySmall,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
    )
}
