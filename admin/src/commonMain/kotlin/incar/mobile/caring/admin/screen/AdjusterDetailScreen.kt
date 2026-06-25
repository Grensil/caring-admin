package incar.mobile.caring.admin.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
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
import incar.mobile.caring.admin.theme.AdminColors
import incar.mobile.caring.admin.viewmodel.ConsultingRequestUiState
import incar.mobile.caring.admin.viewmodel.ConsultingRequestViewModel
import incar.mobile.caring.admin.viewmodel.EducationRequestUiState
import incar.mobile.caring.admin.viewmodel.EducationRequestViewModel
import org.koin.compose.viewmodel.koinViewModel

private fun Double.toFixed1(): String {
    val v = (this * 10 + 0.5).toLong()
    return "${v / 10}.${v % 10}"
}

@Composable
fun AdjusterDetailScreen(
    adjuster: Adjuster,
    token: String,
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentPadding = PaddingValues(horizontal = 32.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // 기본 정보
        item {
            DetailCard(icon = Icons.Default.Person, title = "기본 정보") {
                BasicInfoList(adjuster)
            }
        }

        // 전문 분야
        if (adjuster.regions.isNotEmpty() || adjuster.fields.isNotEmpty() ||
            adjuster.consultingFields.isNotEmpty() || adjuster.qualifications.isNotEmpty() ||
            !adjuster.mainCareer.isNullOrBlank()
        ) {
            item {
                DetailCard(icon = Icons.Default.Star, title = "전문 분야") {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        ChipSection(label = "활동 지역", items = adjuster.regions)
                        ChipSection(label = "전문 분야", items = adjuster.fields)
                        ChipSection(label = "보험금 상담 분야", items = adjuster.consultingFields)
                        ChipSection(label = "자격증", items = adjuster.qualifications)
                        if (!adjuster.mainCareer.isNullOrBlank()) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                SectionLabel("주요 경력")
                                Text(adjuster.mainCareer, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }

        // 평점
        item {
            DetailCard(icon = Icons.Default.Star, title = "평점") {
                Row(horizontalArrangement = Arrangement.spacedBy(0.dp), modifier = Modifier.fillMaxWidth()) {
                    RatingItem(
                        label = "교육 평점",
                        score = adjuster.reviewScore?.toFixed1() ?: "-",
                        count = adjuster.reviewCount,
                        modifier = Modifier.weight(1f),
                    )
                    VerticalDivider(modifier = Modifier.height(56.dp))
                    RatingItem(
                        label = "보험금 상담 평점",
                        score = adjuster.consultingReviewScore?.toFixed1() ?: "-",
                        count = adjuster.consultingReviewCount,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        // 보험금 상담 요청 내역
        item {
            DetailCard(
                icon = Icons.AutoMirrored.Filled.List,
                title = "보험금 상담 요청 내역",
                badge = consultingItems.size,
            ) {
                when (consultingState) {
                    is ConsultingRequestUiState.Loading ->
                        LoadingBox()
                    is ConsultingRequestUiState.Error ->
                        ErrorText()
                    else -> MiniRequestTable(
                        headers = listOf("ID" to 0.5f, "요청자" to 1.5f, "분야" to 1.5f, "상태" to 1f, "접수일" to 1.5f),
                        rows = consultingItems.map { item ->
                            listOf(
                                item.id.toString() to 0.5f,
                                (item.requesterName ?: item.requesterNameSnapshot ?: "-") to 1.5f,
                                (item.consultingField ?: "-") to 1.5f,
                            )
                        },
                        statuses = consultingItems.map { it.status to 1f },
                        lastCols = consultingItems.map { it.createdAt.take(10) to 1.5f },
                        empty = consultingItems.isEmpty(),
                    )
                }
            }
        }

        // 교육 요청 내역
        item {
            DetailCard(
                icon = Icons.Default.DateRange,
                title = "교육 요청 내역",
                badge = educationItems.size,
            ) {
                when (educationState) {
                    is EducationRequestUiState.Loading ->
                        LoadingBox()
                    is EducationRequestUiState.Error ->
                        ErrorText()
                    else -> MiniRequestTable(
                        headers = listOf("ID" to 0.5f, "요청자" to 1.5f, "기관명" to 1.5f, "교육분야" to 1.5f, "상태" to 1f),
                        rows = educationItems.map { item ->
                            listOf(
                                item.id.toString() to 0.5f,
                                (item.requesterName ?: "-") to 1.5f,
                                (item.orgName ?: "-") to 1.5f,
                                (item.field ?: "-") to 1.5f,
                            )
                        },
                        statuses = educationItems.map { it.status to 1f },
                        lastCols = null,
                        empty = educationItems.isEmpty(),
                    )
                }
            }
        }
    }
}

// ─── 공통 컴포넌트 ────────────────────────────────────────────────

@Composable
private fun DetailCard(
    icon: ImageVector,
    title: String,
    badge: Int? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // 카드 헤더
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                Text(
                    text = if (badge != null) "$title (${badge}건)" else title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
private fun BasicInfoList(adjuster: Adjuster) {
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
    Column {
        items.forEachIndexed { index, (label, value) ->
            if (index > 0) HorizontalDivider(
                modifier = Modifier.padding(vertical = 0.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                thickness = 0.5.dp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = label,
                    modifier = Modifier.width(88.dp),
                    fontSize = 13.sp,
                    color = AdminColors.Gray,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = value,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = AdminColors.Gray,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
    )
}

@Composable
private fun ChipSection(label: String, items: List<String>) {
    if (items.isEmpty()) return
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionLabel(label)
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items.forEach { item ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun RatingItem(label: String, score: String, count: Int, modifier: Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        SectionLabel(label)
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(score, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("${count}건", fontSize = 13.sp, color = AdminColors.Gray, modifier = Modifier.padding(bottom = 3.dp))
        }
    }
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
    }
}

@Composable
private fun ErrorText() {
    Text("불러오기 실패", color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
}

@Composable
private fun MiniRequestTable(
    headers: List<Pair<String, Float>>,
    rows: List<List<Pair<String, Float>>>,
    statuses: List<Pair<String, Float>>,
    lastCols: List<Pair<String, Float>>?,
    empty: Boolean,
) {
    if (empty) {
        Box(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text("내역이 없습니다.", fontSize = 14.sp, color = AdminColors.Gray)
        }
        return
    }
    Column {
        // 헤더
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(6.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            headers.forEach { (text, w) ->
                Text(
                    text = text,
                    modifier = Modifier.weight(w),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
        rows.forEachIndexed { i, cols ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (i % 2 == 1) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                cols.forEach { (text, w) ->
                    Text(text, modifier = Modifier.weight(w), fontSize = 12.sp)
                }
                // status badge
                Box(modifier = Modifier.weight(statuses[i].second)) {
                    StatusBadge(statuses[i].first)
                }
                // optional last col
                lastCols?.getOrNull(i)?.let { (text, w) ->
                    Text(text, modifier = Modifier.weight(w), fontSize = 12.sp)
                }
            }
        }
    }
}
