package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import incar.mobile.caring.admin.model.Adjuster
import incar.mobile.caring.admin.viewmodel.AdjusterListUiState
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AdjusterListScreen(token: String, onLogout: () -> Unit) {
    val viewModel: AdjusterListViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var editingAdjuster by remember { mutableStateOf<Adjuster?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var selectedAdjuster by remember { mutableStateOf<Adjuster?>(null) }
    var inputQuery by remember { mutableStateOf("") }
    var activeQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.load(token)
    }

    if (editingAdjuster != null) {
        AdjusterEditDialog(
            adjuster = editingAdjuster!!,
            isSaving = isSaving,
            onSave = { fields ->
                isSaving = true
                viewModel.update(
                    token = token,
                    id = editingAdjuster!!.id,
                    fields = fields,
                    onSuccess = {
                        isSaving = false
                        editingAdjuster = null
                    },
                    onError = { msg ->
                        isSaving = false
                        scope.launch { snackbarHostState.showSnackbar("저장 실패: $msg") }
                    },
                )
            },
            onDismiss = { if (!isSaving) editingAdjuster = null },
        )
    }

    if (selectedAdjuster != null) {
        AdjusterDetailScreen(
            adjuster = selectedAdjuster!!,
            token = token,
            onBack = { selectedAdjuster = null },
        )
    } else {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize(),
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                // 검색바
                AdjusterSearchBar(
                    inputQuery = inputQuery,
                    onInputChange = { inputQuery = it },
                    onSearch = { activeQuery = inputQuery },
                    onClear = { inputQuery = ""; activeQuery = "" },
                )

                // 액션 바
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (uiState is AdjusterListUiState.Success) {
                        val filtered = if (activeQuery.isBlank())
                            (uiState as AdjusterListUiState.Success).adjusters
                        else
                            (uiState as AdjusterListUiState.Success).adjusters.filter {
                                it.name.contains(activeQuery, ignoreCase = true) ||
                                it.phone.contains(activeQuery, ignoreCase = true) ||
                                it.company.contains(activeQuery, ignoreCase = true)
                            }
                        Text(
                            text = "총 ${filtered.size}명",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(onClick = { viewModel.refresh(token) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }

                HorizontalDivider()

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
                                Button(onClick = { viewModel.refresh(token) }) { Text("다시 시도") }
                            }
                        }
                    }
                    is AdjusterListUiState.Success -> {
                        val filtered = if (activeQuery.isBlank()) state.adjusters
                        else state.adjusters.filter {
                            it.name.contains(activeQuery, ignoreCase = true) ||
                            it.phone.contains(activeQuery, ignoreCase = true) ||
                            it.company.contains(activeQuery, ignoreCase = true)
                        }
                        if (filtered.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("등록된 손해사정사가 없습니다.")
                            }
                        } else {
                            AdjusterTable(
                                adjusters = filtered,
                                onEdit = { editingAdjuster = it },
                                onRowClick = { selectedAdjuster = it },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun AdjusterSearchBar(
    inputQuery: String,
    onInputChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    placeholder: String = "이름, 연락처, 업체명으로 검색",
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            value = inputQuery,
            onValueChange = onInputChange,
            placeholder = {
                Text(
                    placeholder,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
            },
            trailingIcon = if (inputQuery.isNotEmpty()) {
                {
                    IconButton(onClick = onClear) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "지우기",
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            } else null,
            modifier = Modifier.width(340.dp).height(48.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
        )
        Button(
            onClick = onSearch,
            modifier = Modifier.height(48.dp),
        ) {
            Text("검색", fontSize = 14.sp)
        }
    }
}

@Composable
private fun AdjusterTable(
    adjusters: List<Adjuster>,
    onEdit: (Adjuster) -> Unit,
    onRowClick: (Adjuster) -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEF2FF))
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            TableCell("이름",   weight = 1.5f, header = true)
            TableCell("소속",   weight = 2f,   header = true)
            TableCell("연락처", weight = 2f,   header = true)
            TableCell("주소",   weight = 3f,   header = true)
            TableCell("경력",   weight = 1f,   header = true)
            TableCell("평점",   weight = 1f,   header = true)
            TableCell("노출",   weight = 1f,   header = true)
            Spacer(modifier = Modifier.width(72.dp))
        }
        HorizontalDivider()
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(adjusters) { adjuster ->
                AdjusterRow(
                    adjuster = adjuster,
                    onEdit = { onEdit(adjuster) },
                    onRowClick = { onRowClick(adjuster) },
                )
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

@Composable
private fun AdjusterRow(
    adjuster: Adjuster,
    onEdit: () -> Unit,
    onRowClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onRowClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TableCell(adjuster.name,                              weight = 1.5f)
        TableCell(adjuster.company.ifBlank { "-" },           weight = 2f)
        TableCell(adjuster.phone,                             weight = 2f)
        TableCell(adjuster.address ?: "-",                    weight = 3f)
        TableCell("${adjuster.careerYears}년",                weight = 1f)
        TableCell(adjuster.reviewScore?.toFixed1() ?: "-",    weight = 1f)
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
        OutlinedButton(
            onClick = onEdit,
            modifier = Modifier.width(72.dp).height(32.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
        ) {
            Text("편집", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun RowScope.TableCell(text: String, weight: Float, header: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontSize = if (header) 15.sp else 14.sp,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
    )
}

private fun Double.toFixed1(): String {
    val v = (this * 10 + 0.5).toLong()
    return "${v / 10}.${v % 10}"
}
