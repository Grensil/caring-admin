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
import incar.mobile.caring.admin.data.dto.EducationRequestDto
import incar.mobile.caring.admin.viewmodel.AdjusterListUiState
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import incar.mobile.caring.admin.viewmodel.EducationRequestUiState
import incar.mobile.caring.admin.viewmodel.EducationRequestViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun EducationRequestScreen(token: String) {
    val viewModel: EducationRequestViewModel = koinViewModel()
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
                val count = (uiState as? EducationRequestUiState.Success)?.let { state ->
                    if (activeQuery.isBlank()) state.total
                    else state.items.count {
                        (it.adjusterNameFull ?: it.adjusterNameSnapshot ?: "").contains(activeQuery, ignoreCase = true)
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
                is EducationRequestUiState.Success -> {
                    val filtered = if (activeQuery.isBlank()) state.items
                    else state.items.filter {
                        (it.adjusterNameFull ?: it.adjusterNameSnapshot ?: "").contains(activeQuery, ignoreCase = true)
                    }
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
private fun EducationRequestTable(
    items: List<EducationRequestDto>,
    onRowClick: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEF2FF))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            ERCell("ID",        weight = 0.5f, header = true)
            ERCell("요청자",    weight = 1.5f, header = true)
            ERCell("연락처",    weight = 1.5f, header = true)
            ERCell("손해사정사", weight = 1.5f, header = true)
            ERCell("기관명",    weight = 1.5f, header = true)
            ERCell("교육분야",  weight = 1.5f, header = true)
            ERCell("희망일",    weight = 1.2f, header = true)
            ERCell("상태",      weight = 1f,   header = true)
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
                    ERCell(item.id.toString(),                                          weight = 0.5f)
                    ERCell(item.requesterName ?: "-",                                   weight = 1.5f)
                    ERCell(item.requesterPhone ?: "-",                                  weight = 1.5f)
                    ERCell(item.adjusterNameFull ?: item.adjusterNameSnapshot ?: "-",   weight = 1.5f)
                    ERCell(item.orgName ?: "-",                                         weight = 1.5f)
                    ERCell(item.field ?: "-",                                           weight = 1.5f)
                    ERCell(item.desiredDate?.take(10) ?: "-",                           weight = 1.2f)
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
        fontSize = if (header) 15.sp else 14.sp,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
    )
}
