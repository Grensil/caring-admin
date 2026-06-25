package incar.mobile.caring.admin.preview

import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import incar.mobile.caring.admin.model.Adjuster
import incar.mobile.caring.admin.screen.AdminSearchBar
import incar.mobile.caring.admin.screen.StatusBadge
import incar.mobile.caring.admin.theme.AdminTheme
import androidx.compose.ui.tooling.preview.Preview

// ── 샘플 데이터 ────────────────────────────────────────────────────

private val sampleAdjuster = Adjuster(
    id = 1,
    name = "엄지성",
    company = "맨체스터유나이티드",
    phone = "01077777777",
    officePhone = "02-1234-5678",
    fax = null,
    address = "서울시 강남구 테헤란로 123",
    addressDetail = "456호",
    careerYears = 7,
    regions = listOf("서울", "경기", "인천"),
    fields = listOf("자동차", "화재", "상해"),
    consultingFields = listOf("자동차보험", "실손의료보험"),
    email = "eom@example.com",
    profileImage = null,
    qualifications = listOf("손해사정사 1급", "보험심사역"),
    mainCareer = "삼성화재 10년, 현대해상 5년 근무",
    isVisible = true,
    reviewScore = 4.8,
    reviewCount = 12,
    consultingReviewScore = 4.5,
    consultingReviewCount = 8,
    lat = 37.5,
    lng = 127.0,
    createdAt = "2026-06-19T00:00:00",
    updatedAt = "2026-06-19T00:00:00",
)

// ── 프리뷰 ────────────────────────────────────────────────────────

@Preview
@Composable
fun PreviewStatusBadgePending() {
    AdminTheme { StatusBadge("pending") }
}

@Preview
@Composable
fun PreviewStatusBadgeAccepted() {
    AdminTheme { StatusBadge("accepted") }
}

@Preview
@Composable
fun PreviewStatusBadgeCompleted() {
    AdminTheme { StatusBadge("completed") }
}

@Preview
@Composable
fun PreviewStatusBadgeRejected() {
    AdminTheme { StatusBadge("rejected") }
}

@Preview
@Composable
fun PreviewAdminSearchBarEmpty() {
    AdminTheme {
        AdminSearchBar(
            query         = "",
            onQueryChange = {},
            placeholder   = "이름, 연락처, 업체명으로 검색",
            modifier      = Modifier.width(360.dp),
        )
    }
}

@Preview
@Composable
fun PreviewAdminSearchBarFilled() {
    AdminTheme {
        AdminSearchBar(
            query         = "엄지성",
            onQueryChange = {},
            placeholder   = "이름, 연락처, 업체명으로 검색",
            modifier      = Modifier.width(360.dp),
        )
    }
}

@Preview
@Composable
fun PreviewStatusBadgeCancelled() {
    AdminTheme { StatusBadge("cancelled") }
}
