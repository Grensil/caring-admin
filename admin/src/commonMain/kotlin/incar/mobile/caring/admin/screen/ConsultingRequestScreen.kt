package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import incar.mobile.caring.admin.data.dto.ConsultingRequestDto
import incar.mobile.caring.admin.model.Adjuster
import incar.mobile.caring.admin.viewmodel.AdjusterListUiState
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import incar.mobile.caring.admin.viewmodel.ConsultingRequestUiState
import incar.mobile.caring.admin.viewmodel.ConsultingRequestViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ConsultingRequestScreen(
    token: String,
    onAdjusterSelect: (Adjuster) -> Unit = {},
) {
    val viewModel: ConsultingRequestViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    val adjusterVm: AdjusterListViewModel = koinViewModel()
    val adjusterState by adjusterVm.uiState.collectAsState()

    var query by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.load(token)
        if (adjusterState is AdjusterListUiState.Loading) adjusterVm.load(token)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ── 툴바 ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                AdminSearchBar(
                    query         = query,
                    onQueryChange = { query = it },
                    placeholder   = "손해사정사 이름, 연락처, 업체명으로 검색",
                    modifier      = Modifier.widthIn(min = 200.dp, max = 400.dp),
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { viewModel.refresh(token) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                }
            }

            // ── 건수 ──
            val count = (uiState as? ConsultingRequestUiState.Success)?.let { state ->
                if (query.isBlank()) state.total
                else state.items.count { filterConsulting(it, query) }
            }
            if (count != null) {
                Text(
                    text     = "총 ${count}건",
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 8.dp),
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            HorizontalDivider()

            when (val state = uiState) {
                is ConsultingRequestUiState.Loading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is ConsultingRequestUiState.Error ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.refresh(token) }) { Text("다시 시도") }
                        }
                    }
                is ConsultingRequestUiState.Success -> {
                    val filtered = if (query.isBlank()) state.items
                    else state.items.filter { filterConsulting(it, query) }
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
                                if (adjuster != null) onAdjusterSelect(adjuster)
                            },
                        )
                    }
                }
            }
        }
    }
}

private fun filterConsulting(item: ConsultingRequestDto, query: String): Boolean =
    (item.adjusterNameFull ?: item.adjusterNameSnapshot ?: "").contains(query, ignoreCase = true) ||
    (item.adjusterPhone ?: "").contains(query, ignoreCase = true) ||
    (item.adjusterCompany ?: "").contains(query, ignoreCase = true)

@Composable
private fun ConsultingRequestTable(
    items: List<ConsultingRequestDto>,
    onRowClick: (Int) -> Unit,
) {
    val hScroll = rememberScrollState()

    // 헤더 정의: label to width
    data class Col(val label: String, val w: Float)
    val cols = listOf(
        Col("ID",       0.5f),
        Col("요청자",   1.5f),
        Col("연락처",   1.5f),
        Col("손해사정사", 1.5f),
        Col("분야",     1.5f),
        Col("사고유형", 1.5f),
    )

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            cols.forEach { col ->
                Text(
                    text       = col.label,
                    modifier   = Modifier.weight(col.w),
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.primary,
                )
            }
            // 상태 + 접수일
            Text("상태",  modifier = Modifier.weight(1f),   fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
            Text("접수일", modifier = Modifier.weight(1.5f), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
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
                    CRCell(item.id.toString(),                                        weight = 0.5f)
                    CRCell(item.requesterName ?: item.requesterNameSnapshot ?: "-",   weight = 1.5f)
                    CRCell(item.contact ?: item.requesterPhone ?: "-",                weight = 1.5f)
                    CRCell(item.adjusterNameFull ?: item.adjusterNameSnapshot ?: "-", weight = 1.5f)
                    CRCell(item.consultingField ?: "-",                               weight = 1.5f)
                    CRCell(item.accidentType ?: "-",                                  weight = 1.5f)
                    Box(modifier = Modifier.weight(1f)) { StatusBadge(item.status) }
                    CRCell(item.createdAt.take(10),                                   weight = 1.5f)
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun RowScope.CRCell(text: String, weight: Float) {
    Text(
        text     = text,
        modifier = Modifier.weight(weight),
        fontSize = 14.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
internal fun StatusBadge(status: String) {
    val (text, color) = when (status) {
        "pending"   -> "대기중" to MaterialTheme.colorScheme.primaryContainer
        "accepted"  -> "수락"   to MaterialTheme.colorScheme.tertiaryContainer
        "completed" -> "완료"   to MaterialTheme.colorScheme.secondaryContainer
        "rejected"  -> "거절"   to MaterialTheme.colorScheme.errorContainer
        "cancelled" -> "취소"   to MaterialTheme.colorScheme.surfaceVariant
        else        -> status   to MaterialTheme.colorScheme.surfaceVariant
    }
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = color,
    ) {
        Text(
            text     = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style    = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
        )
    }
}
