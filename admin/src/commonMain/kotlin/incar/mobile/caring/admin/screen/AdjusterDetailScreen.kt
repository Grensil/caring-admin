package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import incar.mobile.caring.admin.model.Adjuster
import incar.mobile.caring.admin.viewmodel.ConsultingRequestUiState
import incar.mobile.caring.admin.viewmodel.ConsultingRequestViewModel
import incar.mobile.caring.admin.viewmodel.EducationRequestUiState
import incar.mobile.caring.admin.viewmodel.EducationRequestViewModel
import org.koin.compose.viewmodel.koinViewModel

private fun Double.toFixed1(): String {
    val v = (this * 10 + 0.5).toLong()
    return "${v / 10}.${v % 10}"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdjusterDetailScreen(
    adjuster: Adjuster,
    token: String,
    onBack: () -> Unit,
) {
    val consultingVm: ConsultingRequestViewModel = koinViewModel()
    val educationVm: EducationRequestViewModel = koinViewModel()

    val consultingState by consultingVm.uiState.collectAsState()
    val educationState by educationVm.uiState.collectAsState()

    LaunchedEffect(Unit) {
        if (consultingState is ConsultingRequestUiState.Loading) consultingVm.load(token)
        if (educationState is EducationRequestUiState.Loading) educationVm.load(token)
    }

    val consultingItems = when (val s = consultingState) {
        is ConsultingRequestUiState.Success -> s.items.filter { it.adjusterId == adjuster.id }
        else -> emptyList()
    }

    val educationItems = when (val s = educationState) {
        is EducationRequestUiState.Success -> s.items.filter { it.adjusterId == adjuster.id }
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(adjuster.name, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // 기본 정보 섹션
            item {
                DetailSectionCard(
                    icon = Icons.Default.Person,
                    title = "기본 정보",
                ) {
                    BasicInfoGrid(adjuster)
                }
            }

            // 전문 분야 섹션
            item {
                DetailSectionCard(
                    icon = Icons.Default.Star,
                    title = "전문 분야",
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ChipRow(label = "활동 지역", items = adjuster.regions)
                        ChipRow(label = "전문 분야", items = adjuster.fields)
                        ChipRow(label = "보험금 상담 분야", items = adjuster.consultingFields)
                        ChipRow(label = "자격증", items = adjuster.qualifications)
                        if (!adjuster.mainCareer.isNullOrBlank()) {
                            Column {
                                Text(
                                    "주요 경력",
                                    fontSize = 12.sp,
                                    color = Color(0xFF757575),
                                    fontWeight = FontWeight.Medium,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(adjuster.mainCareer, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }

            // 평점 섹션
            item {
                DetailSectionCard(
                    icon = Icons.Default.ThumbUp,
                    title = "평점",
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                        Column {
                            Text("교육 평점", fontSize = 12.sp, color = Color(0xFF757575))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${adjuster.reviewScore?.toFixed1() ?: "-"} (${adjuster.reviewCount}건)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        Column {
                            Text("보험금 상담 평점", fontSize = 12.sp, color = Color(0xFF757575))
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${adjuster.consultingReviewScore?.toFixed1() ?: "-"} (${adjuster.consultingReviewCount}건)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            // 보험금 상담 요청 내역
            item {
                DetailSectionCard(
                    icon = Icons.AutoMirrored.Filled.List,
                    title = "보험금 상담 요청 내역 (${consultingItems.size}건)",
                ) {
                    when (consultingState) {
                        is ConsultingRequestUiState.Loading -> {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                        is ConsultingRequestUiState.Error -> {
                            Text("불러오기 실패", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                        }
                        else -> {
                            if (consultingItems.isEmpty()) {
                                Text("요청 내역이 없습니다.", fontSize = 14.sp, color = Color(0xFF757575))
                            } else {
                                Column {
                                    // 헤더
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFEEF2FF))
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                    ) {
                                        MiniCell("ID", weight = 0.5f, header = true)
                                        MiniCell("요청자", weight = 1.5f, header = true)
                                        MiniCell("분야", weight = 1.5f, header = true)
                                        MiniCell("상태", weight = 1f, header = true)
                                        MiniCell("접수일", weight = 1.5f, header = true)
                                    }
                                    HorizontalDivider()
                                    consultingItems.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            MiniCell(item.id.toString(), weight = 0.5f)
                                            MiniCell(item.requesterName ?: item.requesterNameSnapshot ?: "-", weight = 1.5f)
                                            MiniCell(item.consultingField ?: "-", weight = 1.5f)
                                            Box(modifier = Modifier.weight(1f)) {
                                                StatusBadge(item.status)
                                            }
                                            MiniCell(item.createdAt.take(10), weight = 1.5f)
                                        }
                                        HorizontalDivider(thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 교육 요청 내역
            item {
                DetailSectionCard(
                    icon = Icons.Default.DateRange,
                    title = "교육 요청 내역 (${educationItems.size}건)",
                ) {
                    when (educationState) {
                        is EducationRequestUiState.Loading -> {
                            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                        is EducationRequestUiState.Error -> {
                            Text("불러오기 실패", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                        }
                        else -> {
                            if (educationItems.isEmpty()) {
                                Text("요청 내역이 없습니다.", fontSize = 14.sp, color = Color(0xFF757575))
                            } else {
                                Column {
                                    // 헤더
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFEEF2FF))
                                            .padding(horizontal = 8.dp, vertical = 8.dp),
                                    ) {
                                        MiniCell("ID", weight = 0.5f, header = true)
                                        MiniCell("요청자", weight = 1.5f, header = true)
                                        MiniCell("기관명", weight = 1.5f, header = true)
                                        MiniCell("교육분야", weight = 1.5f, header = true)
                                        MiniCell("상태", weight = 1f, header = true)
                                    }
                                    HorizontalDivider()
                                    educationItems.forEach { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 8.dp, vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            MiniCell(item.id.toString(), weight = 0.5f)
                                            MiniCell(item.requesterName ?: "-", weight = 1.5f)
                                            MiniCell(item.orgName ?: "-", weight = 1.5f)
                                            MiniCell(item.field ?: "-", weight = 1.5f)
                                            Box(modifier = Modifier.weight(1f)) {
                                                StatusBadge(item.status)
                                            }
                                        }
                                        HorizontalDivider(thickness = 0.5.dp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 리뷰 리스트 (준비 중)
            item {
                DetailSectionCard(
                    icon = Icons.Default.Favorite,
                    title = "리뷰",
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("리뷰 목록은 준비 중입니다.", fontSize = 14.sp, color = Color(0xFF757575))
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF5C6BC0))
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun BasicInfoGrid(adjuster: Adjuster) {
    val items = listOf(
        "이름" to adjuster.name,
        "소속" to adjuster.company.ifBlank { "-" },
        "휴대폰" to adjuster.phone.ifBlank { "-" },
        "사무실" to (adjuster.officePhone ?: "-"),
        "팩스" to (adjuster.fax ?: "-"),
        "이메일" to (adjuster.email ?: "-"),
        "경력" to "${adjuster.careerYears}년",
        "노출 여부" to (if (adjuster.isVisible) "노출" else "숨김"),
        "주소" to (adjuster.address ?: "-"),
        "상세주소" to (adjuster.addressDetail ?: "-"),
        "등록일" to adjuster.createdAt.take(10),
        "수정일" to adjuster.updatedAt.take(10),
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { pair ->
            Row(modifier = Modifier.fillMaxWidth()) {
                pair.forEach { (label, value) ->
                    Column(modifier = Modifier.weight(1f)) {
                        Text(label, fontSize = 12.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(2.dp))
                        Text(value, fontSize = 14.sp)
                    }
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ChipRow(label: String, items: List<String>) {
    if (items.isEmpty()) return
    Column {
        Text(label, fontSize = 12.sp, color = Color(0xFF757575), fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(4.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            items.forEach { item ->
                SuggestionChip(
                    onClick = {},
                    label = { Text(item, fontSize = 12.sp) },
                )
            }
        }
    }
}

@Composable
private fun RowScope.MiniCell(text: String, weight: Float, header: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.weight(weight),
        fontSize = if (header) 12.sp else 12.sp,
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
        color = if (header) Color(0xFF424242) else Color.Unspecified,
    )
}
