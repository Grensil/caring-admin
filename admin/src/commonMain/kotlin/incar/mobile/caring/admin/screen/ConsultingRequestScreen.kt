package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import incar.mobile.caring.admin.data.dto.ConsultingRequestDto
import incar.mobile.caring.admin.viewmodel.AdjusterListUiState
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import incar.mobile.caring.admin.viewmodel.ConsultingRequestUiState
import incar.mobile.caring.admin.viewmodel.ConsultingRequestViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConsultingRequestScreen(token: String) {
    val viewModel: ConsultingRequestViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val adjusterVm: AdjusterListViewModel = koinViewModel()
    val adjusterState by adjusterVm.uiState.collectAsState()

    var selectedAdjuster by remember { mutableStateOf<incar.mobile.caring.admin.model.Adjuster?>(null) }
    var inputQuery by remember { mutableStateOf("") }
    var activeQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.load(token)
        if (adjusterState is AdjusterListUiState.Loading) adjusterVm.load(token)
    }

    if (selectedAdjuster != null) {
        AdjusterDetailScreen(
            adjuster = selectedAdjuster!!,
            token = token,
            onBack = { selectedAdjuster = null },
        )
        return
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 검색바
            AdjusterSearchBar(
                inputQuery = inputQuery,
                onInputChange = { inputQuery = it },
                onSearch = { activeQuery = inputQuery },
                onClear = { inputQuery = ""; activeQuery = "" },
                placeholder = "손해사정사 이름, 연락처, 업체명으로 검색",
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val count = (uiState as? ConsultingRequestUiState.Success)?.let { state ->
                    if (activeQuery.isBlank()) state.total
                    else state.items.count {
                        (it.adjusterNameFull ?: it.adjusterNameSnapshot ?: "").contains(activeQuery, ignoreCase = true) ||
                        (it.adjusterPhone ?: "").contains(activeQuery, ignoreCase = true) ||
                        (it.adjusterCompany ?: "").contains(activeQuery, ignoreCase = true)
                    }
                }
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
                is ConsultingRequestUiState.Loading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                is ConsultingRequestUiState.Error ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.refresh(token) }) { Text("다시 시도") }
                        }
                    }
                is ConsultingRequestUiState.Success -> {
                    val filtered = if (activeQuery.isBlank()) state.items
                    else state.items.filter {
                        (it.adjusterNameFull ?: it.adjusterNameSnapshot ?: "").contains(activeQuery, ignoreCase = true) ||
                        (it.adjusterPhone ?: "").contains(activeQuery, ignoreCase = true) ||
                        (it.adjusterCompany ?: "").contains(activeQuery, ignoreCase = true)
                    }
                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("보험금 상담 요청 내역이 없습니다.")
                        }
                    } else {
                        ConsultingRequestTable(
                            items = filtered,
                            onRowClick = { adjusterId ->
                                val adjuster = (adjusterState as? AdjusterListUiState.Success)
                                    ?.adjusters?.firstOrNull { it.id == adjusterId }
                                if (adjuster != null) selectedAdjuster = adjuster
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ConsultingRequestTable(
    items: List<ConsultingRequestDto>,
    onRowClick: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEF2FF))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            CRCell("ID",        weight = 0.5f, header = true)
            CRCell("요청자",    weight = 1.5f, header = true)
            CRCell("연락처",    weight = 1.5f, header = true)
            CRCell("손해사정사", weight = 1.5f, header = true)
            CRCell("분야",      weight = 1.5f, header = true)
            CRCell("사고유형",  weight = 1.5f, header = true)
            CRCell("상태",      weight = 1f,   header = true)
            CRCell("접수일",    weight = 1.5f, header = true)
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRowClick(item.adjusterId) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CRCell(item.id.toString(),                                          weight = 0.5f)
                    CRCell(item.requesterName ?: item.requesterNameSnapshot ?: "-",     weight = 1.5f)
                    CRCell(item.contact ?: item.requesterPhone ?: "-",                  weight = 1.5f)
                    CRCell(item.adjusterNameFull ?: item.adjusterNameSnapshot ?: "-",   weight = 1.5f)
                    CRCell(item.consultingField ?: "-",                                 weight = 1.5f)
                    CRCell(item.accidentType ?: "-",                                    weight = 1.5f)
                    Box(modifier = Modifier.weight(1f)) {
                        StatusBadge(item.status)
                    }
                    CRCell(item.createdAt.take(10),                                     weight = 1.5f)
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun RowScope.CRCell(text: String, weight: Float, header: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontSize = if (header) 15.sp else 14.sp,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
    )
}

@Composable
internal fun StatusBadge(status: String) {
    val (text, color) = when (status) {
        "pending"   -> "대기중" to MaterialTheme.colorScheme.primaryContainer
        "accepted"  -> "수락" to MaterialTheme.colorScheme.tertiaryContainer
        "completed" -> "완료" to MaterialTheme.colorScheme.secondaryContainer
        "rejected"  -> "거절" to MaterialTheme.colorScheme.errorContainer
        "cancelled" -> "취소" to MaterialTheme.colorScheme.surfaceVariant
        else        -> status to MaterialTheme.colorScheme.surfaceVariant
    }
    Badge(containerColor = color) { Text(text, style = MaterialTheme.typography.labelSmall) }
}
