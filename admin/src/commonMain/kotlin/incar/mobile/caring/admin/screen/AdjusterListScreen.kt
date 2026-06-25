package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.compose.ui.tooling.preview.Preview
import incar.mobile.caring.admin.model.Adjuster
import incar.mobile.caring.admin.storage.AdminStorage
import incar.mobile.caring.admin.theme.AdminTheme
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

    var editingAdjuster by remember { mutableStateOf<Adjuster?>(null) }
    var isSaving        by remember { mutableStateOf(false) }
    var query           by remember { mutableStateOf("") }
    var columnOrder     by remember { mutableStateOf(loadColOrder(storage)) }
    var visibleColumns  by remember { mutableStateOf(loadColVisible(storage)) }

    LaunchedEffect(Unit)           { viewModel.load(token) }
    LaunchedEffect(columnOrder)    { storage.save(KEY_COL_ORDER,   columnOrder.joinToString(",") { it.name }) }
    LaunchedEffect(visibleColumns) { storage.save(KEY_COL_VISIBLE, visibleColumns.joinToString(",") { it.name }) }

    if (editingAdjuster != null) {
        AdjusterEditDialog(
            adjuster  = editingAdjuster!!,
            isSaving  = isSaving,
            onSave    = { fields ->
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
                IconButton(onClick = { viewModel.refresh(token) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.Refresh, contentDescription = "새로고침")
                }
            }

            // ── 건수 ──
            if (uiState is AdjusterListUiState.Success) {
                val cnt = filterAdjusters((uiState as AdjusterListUiState.Success).adjusters, query).size
                Text(
                    text     = "총 ${cnt}명",
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
                            adjusters             = filtered,
                            columnOrder           = columnOrder,
                            visibleColumns        = visibleColumns,
                            onColumnOrderChange   = { columnOrder = it },
                            onVisibleColumnsChange = { visibleColumns = it },
                            onEdit                = { editingAdjuster = it },
                            onRowClick            = onAdjusterSelect,
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

// ── 테이블 ──────────────────────────────────────────────────────────

@Composable
private fun AdjusterTable(
    adjusters: List<Adjuster>,
    columnOrder: List<AdjusterColumn>,
    visibleColumns: Set<AdjusterColumn>,
    onColumnOrderChange: (List<AdjusterColumn>) -> Unit,
    onVisibleColumnsChange: (Set<AdjusterColumn>) -> Unit,
    onEdit: (Adjuster) -> Unit,
    onRowClick: (Adjuster) -> Unit,
) {
    val visibleCols = columnOrder.filter { it in visibleColumns }
    val hScroll     = rememberScrollState()
    val editW       = 40.dp
    val density     = LocalDensity.current

    // 드래그 상태
    var draggingCol  by remember { mutableStateOf<AdjusterColumn?>(null) }
    var dragOffsetX  by remember { mutableStateOf(0f) }

    // 필터 드롭다운
    var showFilter by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {

        // ── 헤더 ────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(hScroll)
                .background(MaterialTheme.colorScheme.primaryContainer),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            visibleCols.forEach { col ->
                key(col) {
                    val isDragging = draggingCol == col

                    Box(
                        modifier = Modifier
                            .width(col.width)
                            .zIndex(if (isDragging) 2f else 1f)
                            .graphicsLayer { translationX = if (isDragging) dragOffsetX else 0f }
                            .then(
                                if (isDragging)
                                    Modifier.background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                else Modifier
                            )
                            .pointerInput(col) {
                                detectDragGestures(
                                    onDragStart = {
                                        draggingCol = col
                                        dragOffsetX = 0f
                                    },
                                    onDragEnd = {
                                        draggingCol = null
                                        dragOffsetX = 0f
                                    },
                                    onDragCancel = {
                                        draggingCol = null
                                        dragOffsetX = 0f
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        dragOffsetX += dragAmount.x

                                        val current = draggingCol ?: return@detectDragGestures
                                        val currentIdx = columnOrder.indexOf(current)
                                        if (currentIdx < 0) return@detectDragGestures

                                        val halfW = with(density) { current.width.toPx() / 2f }

                                        if (dragOffsetX > halfW && currentIdx < columnOrder.lastIndex) {
                                            val nextIdx = currentIdx + 1
                                            val nextW = with(density) { columnOrder[nextIdx].width.toPx() }
                                            onColumnOrderChange(columnOrder.toMutableList().also {
                                                val t = it[currentIdx]; it[currentIdx] = it[nextIdx]; it[nextIdx] = t
                                            })
                                            dragOffsetX -= nextW
                                        } else if (dragOffsetX < -halfW && currentIdx > 0) {
                                            val prevIdx = currentIdx - 1
                                            val prevW = with(density) { columnOrder[prevIdx].width.toPx() }
                                            onColumnOrderChange(columnOrder.toMutableList().also {
                                                val t = it[currentIdx]; it[currentIdx] = it[prevIdx]; it[prevIdx] = t
                                            })
                                            dragOffsetX += prevW
                                        }
                                    },
                                )
                            }
                            .padding(start = 8.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.DragIndicator,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text       = col.label,
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = MaterialTheme.colorScheme.primary,
                                maxLines   = 1,
                                overflow   = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            // 편집 컬럼 자리 확보
            Spacer(Modifier.width(editW))

            // ── 필터 버튼 ──
            Box {
                IconButton(
                    onClick  = { showFilter = !showFilter },
                    modifier = Modifier.size(36.dp),
                ) {
                    Icon(
                        if (visibleCols.size < AdjusterColumn.entries.size)
                            Icons.Default.FilterListOff else Icons.Default.FilterList,
                        contentDescription = "컬럼 필터",
                        tint     = if (visibleCols.size < AdjusterColumn.entries.size)
                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp),
                    )
                }
                DropdownMenu(
                    expanded          = showFilter,
                    onDismissRequest  = { showFilter = false },
                ) {
                    Text(
                        "표시 컬럼",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style    = MaterialTheme.typography.labelMedium,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    HorizontalDivider()
                    columnOrder.forEach { col ->
                        val checked = col in visibleColumns
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Checkbox(
                                        checked         = checked,
                                        onCheckedChange = null,
                                        modifier        = Modifier.size(20.dp),
                                    )
                                    Text(col.label, fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                onVisibleColumnsChange(
                                    if (checked) visibleColumns - col else visibleColumns + col
                                )
                            },
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        // ── 데이터 행 ─────────────────────────────────────────────────
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(adjusters) { adjuster ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(hScroll)
                        .clickable { onRowClick(adjuster) }
                        .padding(horizontal = 8.dp, vertical = 10.dp),
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
                                modifier = Modifier.width(col.width).padding(horizontal = 4.dp),
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                    OutlinedButton(
                        onClick        = { onEdit(adjuster) },
                        modifier       = Modifier.width(editW).height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                        shape          = RoundedCornerShape(6.dp),
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
    var isFocused by remember { mutableStateOf(false) }
    val primary   = MaterialTheme.colorScheme.primary
    val outline   = MaterialTheme.colorScheme.outline

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
        singleLine   = true,
        leadingIcon  = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                modifier           = Modifier.size(18.dp),
                tint               = if (isFocused) primary else outline,
            )
        },
        trailingIcon = if (query.isNotEmpty()) ({
            IconButton(onClick = { onQueryChange("") }) {
                Icon(Icons.Default.Close, contentDescription = "지우기", modifier = Modifier.size(16.dp))
            }
        }) else null,
        shape  = RoundedCornerShape(24.dp),
        // 내부 border는 숨기고 외부 Modifier.border로 직접 그림 (두께 제어)
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
        ),
        modifier  = modifier
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = if (isFocused) 2.dp else 1.5.dp,
                color = if (isFocused) primary else outline.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp),
            ),
        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
    )
}

// ── Preview ──────────────────────────────────────────────────────────

@Preview
@Composable
private fun AdminSearchBarEmptyPreview() {
    AdminTheme {
        Surface {
            AdminSearchBar(
                query         = "",
                onQueryChange = {},
                placeholder   = "이름, 연락처, 업체명으로 검색",
                modifier      = Modifier.width(360.dp).padding(16.dp),
            )
        }
    }
}

@Preview
@Composable
private fun AdminSearchBarFilledPreview() {
    AdminTheme {
        Surface {
            AdminSearchBar(
                query         = "엄지성",
                onQueryChange = {},
                placeholder   = "이름, 연락처, 업체명으로 검색",
                modifier      = Modifier.width(360.dp).padding(16.dp),
            )
        }
    }
}
