package incar.mobile.caring.domain.model

data class HomeData(
    val carNum: String,
    val carName: String,
    val carPrice: Long,
    val carLoanRate: Int? = null,           // 할부 잔여율 (%)
    val insureExpiryDate: String,
    val inspectionDate: String = "",        // 정기검사일
    val warrantyDate: String = "",          // 보증만료일
    val isInsureExpiry: Boolean,
    val faName: String,
    val faPhone: String,
    val alert: HomeAlert?,
    val services: HomeServiceFlags,
    val timeline: List<CarTimelineItem> = emptyList(),
    // FA 전용 (isFa=false면 0)
    val customerCnt: Int,
    val expiryCarCnt: Int,
)

data class HomeAlert(
    val title: String,
    val content: String,
)

data class HomeServiceFlags(
    val insureYn: Boolean,
    val carposYn: Boolean,
    val juyusoYn: Boolean,
    val chargeYn: Boolean,
    val carsuriYn: Boolean,
    val newcarYn: Boolean,
    val junggoYn: Boolean,
    val tireYn: Boolean,
    val carposMapYn: Boolean,
    val allimYn: Boolean,
)

enum class TimelineType { INSURANCE, MAINTENANCE, EXCHANGE }

data class CarTimelineItem(
    val type: TimelineType,
    val title: String,
    val date: String,
    val description: String? = null,
    val progressPercent: Int? = null,   // MAINTENANCE 타입에서 진행률 표시
)
