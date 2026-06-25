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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import incar.mobile.caring.admin.model.Adjuster
import incar.mobile.caring.admin.storage.AdminStorage
import incar.mobile.caring.admin.viewmodel.AdjusterListUiState
import incar.mobile.caring.admin.viewmodel.AdjusterListViewModel
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

// ── 컬럼 정의 ───────────────────────────────────────────────────────

enum class AdjusterColumn(val label: String, val width: Dp) {
    NAME("이름", 120.dp),
    COMPANY("소속", 160.dp),
    PHONE("연락처", 140.dp),
    ADDRESS("주소", 220.dp),
    CAREER("경력", 70.dp),
    SCORE("평점", 80.dp),
    VISIBLE("노출", 70.dp),
}

private const val KEY_COL_ORDER   = "adjuster_col_order"
private const val KEY_COL_VISIBLE = "adjuster_col_visible"

private fun loadColOrder(storage: AdminStorage): List<AdjusterColumn> {
    val saved = storage.read(KEY_COL_ORDER) ?: return AdjusterColumn.entries.toList()
    val parsed = saved.split(",").mapNotNull { n -> AdjusterColumn.entries.firstOrNull { it.name == n } }
    val missing = AdjusterColumn.entries.filter { it !in parsed }
    return parsed + missing
}

private fun loadColVisible(storage: AdminStorage): Set<AdjusterColumn> {
    val saved = storage.read(KEY_COL_VISIBLE) ?: return AdjusterColumn.entries.toSet()
    if (saved.isBlank()) return AdjusterColumn.entries.toSet()
    return saved.split(",").mapNotNull { n -> AdjusterColumn.entries.firstOrNull { it.name == n } }.toSet()
}

// ── 메인 스크린 ──────────────────────────────────────────────────────

@Composable
fun AdjusterListScreen(
    token: String,
    onLogout: () -> Unit,
    onAdjusterSelect: (Adjuster) -> Unit = {},
) {
    val storage: AdminStorage = koinInject()
    val viewModel: AdjusterListViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var editingAdjuster  by remember { mutableStateOf<Adjuster?>(null) }
    var isSaving         by remember { mutableStateOf(false) }
    var query            by remember { mutableStateOf("") }
    var showColPicker    by remember { mutableStateOf(false) }
    var columnOrder      by remember { mutableStateOf(loadColOrder(storage)) }
    var visibleColumns   by remember { mutableStateOf(loadColVisible(storage)) }

    LaunchedEffect(Unit) { viewModel.load(token) }
    LaunchedEffect(columnOrder)    { storage.save(KEY_COL_ORDER, columnOrder.joinToString(",") { it.name }) }
    LaunchedEffect(visibleColumns) { storage.save(KEY_COL_VISIBLE, visibleColumns.joinToString(",") { it.name }) }

    if (editingAdjuster != null) {
        AdjusterEditDialog(
            adjuster = editingAdjuster!!,
            isSaving = isSaving,
            onSave = { fields ->
                isSaving = true
                viewModel.update(
                    token     = token,
                    id        = editingAdjuster!!.id,
                    fields    = fields,
                    onSuccess = { isSaving = false; editingAdjuster = null },
                    onError   = { msg -> isSaving = false; scope.launch { snackbarHostState.showSnackbar("저장 실패: $msg") } },
                )
            },
            onDismiss = { if (!isSaving) editingAdjuster = null },
        )
    }

    if (showColPicker) {
        ColumnPickerDialog(
            columns  = columnOrder,
            visible  = visibleColumns,
            onDismiss = { showColPicker = false },
            onConfirm = { order, vis -> columnOrder = order; visibleColumns = vis; showColPicker = false },
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier     = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

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
                    placeholder   = "이름, 연락처, 업체명으로 검색",
                    modifier      = Modifier.widthIn(min = 200.dp, max = 400.dp),
                )
                Spacer(Modifier.weight(1f))
                OutlinedButton(
                    onClick = { showColPicker = true },
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Default.ViewColumn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("컬럼", fontSize = 13.sp)
                }
                IconButton(onClick = { viewModel.refresh(token) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                }
            }

            // ── 건수 ──
            val filteredForCount = filterAdjusters(
                (uiState as? AdjusterListUiState.Success)?.adjusters ?: emptyList(), query
            )
            if (uiState is AdjusterListUiState.Success) {
                Text(
                    text     = "총 ${filteredForCount.size}명",
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 8.dp),
                    style    = MaterialTheme.typography.bodyMedium,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            HorizontalDivider()

            when (val state = uiState) {
                is AdjusterListUiState.Loading ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
                is AdjusterListUiState.Error ->
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(state.message, color = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { viewModel.refresh(token) }) { Text("다시 시도") }
                        }
                    }
                is AdjusterListUiState.Success -> {
                    val filtered = filterAdjusters(state.adjusters, query)
                    if (filtered.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("등록된 손해사정사가 없습니다.")
                        }
                    } else {
                        AdjusterTable(
                            adjusters      = filtered,
                            columnOrder    = columnOrder,
                            visibleColumns = visibleColumns,
                            onEdit         = { editingAdjuster = it },
                            onRowClick     = onAdjusterSelect,
                        )
                    }
                }
            }
        }
    }
}

private fun filterAdjusters(adjusters: List<Adjuster>, query: String): List<Adjuster> =
    if (query.isBlank()) adjusters
    else adjusters.filter {
        it.name.contains(query, ignoreCase = true) ||
        it.phone.contains(query, ignoreCase = true) ||
        it.company.contains(query, ignoreCase = true)
    }

// ── 컬럼 피커 다이얼로그 ──────────────────────────────────────────────

@Composable
private fun ColumnPickerDialog(
    columns: List<AdjusterColumn>,
    visible: Set<AdjusterColumn>,
    onDismiss: () -> Unit,
    onConfirm: (List<AdjusterColumn>, Set<AdjusterColumn>) -> Unit,
) {
    var localOrder   by remember { mutableStateOf(columns) }
    var localVisible by remember { mutableStateOf(visible) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("표시 컬럼 설정", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    "표시할 컬럼을 선택하고 순서를 조정하세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                localOrder.forEachIndexed { idx, col ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Checkbox(
                            checked = col in localVisible,
                            onCheckedChange = { checked ->
                                localVisible = if (checked) localVisible + col else localVisible - col
                            },
                        )
                        Text(col.label, modifier = Modifier.weight(1f), fontSize = 14.sp)
                        IconButton(
                            onClick = {
                                if (idx > 0) localOrder = localOrder.toMutableList().also {
                                    val t = it[idx]; it[idx] = it[idx - 1]; it[idx - 1] = t
                                }
                            },
                            modifier = Modifier.size(28.dp),
                            enabled  = idx > 0,
                        ) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "위로", modifier = Modifier.size(16.dp))
                        }
                        IconButton(
                            onClick = {
                                if (idx < localOrder.lastIndex) localOrder = localOrder.toMutableList().also {
                                    val t = it[idx]; it[idx] = it[idx + 1]; it[idx + 1] = t
                                }
                            },
                            modifier = Modifier.size(28.dp),
                            enabled  = idx < localOrder.lastIndex,
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "아래로", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onConfirm(localOrder, localVisible) }) { Text("확인") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("취소") } },
    )
}

// ── 테이블 (가로 스크롤) ─────────────────────────────────────────────

@Composable
private fun AdjusterTable(
    adjusters: List<Adjuster>,
    columnOrder: List<AdjusterColumn>,
    visibleColumns: Set<AdjusterColumn>,
    onEdit: (Adjuster) -> Unit,
    onRowClick: (Adjuster) -> Unit,
) {
    val visibleCols = columnOrder.filter { it in visibleColumns }
    val hScroll     = rememberScrollState()
    val editW       = 80.dp

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(hScroll)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            visibleCols.forEach { col ->
                Text(
                    text       = col.label,
                    modifier   = Modifier.width(col.width),
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.width(editW))
        }
        HorizontalDivider()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(adjusters) { adjuster ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(hScroll)
                        .clickable { onRowClick(adjuster) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    visibleCols.forEach { col ->
                        when (col) {
                            AdjusterColumn.VISIBLE -> Box(modifier = Modifier.width(col.width)) {
                                Badge(
                                    containerColor = if (adjuster.isVisible)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.errorContainer,
                                ) {
                                    Text(if (adjuster.isVisible) "노출" else "숨김")
                                }
                            }
                            else -> Text(
                                text     = adjuster.cellValue(col),
                                modifier = Modifier.width(col.width),
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    OutlinedButton(
                        onClick         = { onEdit(adjuster) },
                        modifier        = Modifier.width(editW).height(32.dp),
                        contentPadding  = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape           = RoundedCornerShape(6.dp),
                    ) {
                        Text("편집", style = MaterialTheme.typography.labelMedium)
                    }
                }
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

private fun Adjuster.cellValue(col: AdjusterColumn): String = when (col) {
    AdjusterColumn.NAME    -> name
    AdjusterColumn.COMPANY -> company.ifBlank { "-" }
    AdjusterColumn.PHONE   -> phone
    AdjusterColumn.ADDRESS -> address ?: "-"
    AdjusterColumn.CAREER  -> "${careerYears}년"
    AdjusterColumn.SCORE   -> reviewScore?.toFixed1() ?: "-"
    AdjusterColumn.VISIBLE -> ""
}

private fun Double.toFixed1(): String {
    val v = (this * 10 + 0.5).toLong()
    return "${v / 10}.${v % 10}"
}

// ── 공통 검색바 ──────────────────────────────────────────────────────

@Composable
internal fun AdminSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String = "검색",
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value         = query,
        onValueChange = onQueryChange,
        placeholder   = {
            Text(
                text     = placeholder,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        singleLine  = true,
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
                tint               = MaterialTheme.colorScheme.primary,
            )
        },
        trailingIcon = if (query.isNotEmpty()) ({
            IconButton(onClick = { onQueryChange("") }) {
                Icon(Icons.Default.Close, contentDescription = "지우기", modifier = Modifier.size(16.dp))
            }
        }) else null,
        shape  = RoundedCornerShape(24.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier   = modifier.height(44.dp),
        textStyle  = LocalTextStyle.current.copy(fontSize = 14.sp),
    )
}
