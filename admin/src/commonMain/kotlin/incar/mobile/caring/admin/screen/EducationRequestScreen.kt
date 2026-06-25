package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import incar.mobile.caring.admin.data.dto.EducationRequestDto
import incar.mobile.caring.admin.model.Adjuster
import incar.mobile.caring.admin.viewmodel.AdjusterListUiState
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import incar.mobile.caring.admin.viewmodel.EducationRequestUiState
import incar.mobile.caring.admin.viewmodel.EducationRequestViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EducationRequestScreen(
    token: String,
    onAdjusterSelect: (Adjuster) -> Unit = {},
) {
    val viewModel: EducationRequestViewModel = koinViewModel()
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
                    placeholder   = "손해사정사 이름으로 검색",
                    modifier      = Modifier.widthIn(min = 200.dp, max = 400.dp),
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { viewModel.refresh(token) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                }
            }

            // ── 건수 ──
            val count = (uiState as? EducationRequestUiState.Success)?.let { state ->
                if (query.isBlank()) state.total
                else state.items.count { filterEducation(it, query) }
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
                is EducationRequestUiState.Loading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is EducationRequestUiState.Error ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.refresh(token) }) { Text("다시 시도") }
                        }
                    }
                is EducationRequestUiState.Success -> {
                    val filtered = if (query.isBlank()) state.items
                    else state.items.filter { filterEducation(it, query) }
                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("교육 요청 내역이 없습니다.")
                        }
                    } else {
                        EducationRequestTable(
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

private fun filterEducation(item: EducationRequestDto, query: String): Boolean =
    (item.adjusterNameFull ?: item.adjusterNameSnapshot ?: "").contains(query, ignoreCase = true)

@Composable
private fun EducationRequestTable(
    items: List<EducationRequestDto>,
    onRowClick: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            ERCell("ID",         0.5f, header = true)
            ERCell("요청자",     1.5f, header = true)
            ERCell("연락처",     1.5f, header = true)
            ERCell("손해사정사", 1.5f, header = true)
            ERCell("기관명",     1.5f, header = true)
            ERCell("교육분야",   1.5f, header = true)
            ERCell("희망일",     1.2f, header = true)
            ERCell("상태",       1f,   header = true)
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
                    ERCell(item.id.toString(),                                         0.5f)
                    ERCell(item.requesterName ?: "-",                                  1.5f)
                    ERCell(item.requesterPhone ?: "-",                                 1.5f)
                    ERCell(item.adjusterNameFull ?: item.adjusterNameSnapshot ?: "-",  1.5f)
                    ERCell(item.orgName ?: "-",                                        1.5f)
                    ERCell(item.field ?: "-",                                          1.5f)
                    ERCell(item.desiredDate?.take(10) ?: "-",                          1.2f)
                    Box(modifier = Modifier.weight(1f)) { StatusBadge(item.status) }
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun RowScope.ERCell(text: String, weight: Float, header: Boolean = false) {
    Text(
        text       = text,
        modifier   = Modifier.weight(weight),
        fontSize   = if (header) 13.sp else 14.sp,
        fontWeight = if (header) FontWeight.SemiBold else FontWeight.Normal,
        color      = if (header) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
        maxLines   = 1,
        overflow   = TextOverflow.Ellipsis,
    )
}
