package incar.mobile.caring.domain.model

enum class HomeFeature {
    // 일반유저
    INSURANCE_RENEWAL,
    ACCIDENT_CARE,
    CAR_TRADE_NEW,
    CAR_TRADE_USED,
    // 카포스
    CARPOS_MANAGEMENT,
    // FA 공통
    CUSTOMER_LIST,
    FA_FEED,
    FA_BENEFITS,
    // 손해사정사
    ADJUSTER_EDU_REQUEST,
    // S급 FA
    INSURANCE_CONSULT_DASHBOARD,
}

enum class UserType {
    NORMAL,    // 일반유저 (is_live=false FA 포함)
    CARPOS,    // 카포스 점주 (carpos_seq != -1)
    FA_LIVE,   // 일반 FA (is_fa && is_live)
    FA_SUPER,  // S급 FA / 영업관리자 (is_fa && is_live && is_super)
    ADJUSTER,  // 손해사정사 (is_fa && is_live && is_adjuster)
    ;

    val isFa get() = this == FA_LIVE || this == FA_SUPER || this == ADJUSTER

    val homeFeatures: List<HomeFeature> get() = when (this) {
        NORMAL   -> listOf(HomeFeature.INSURANCE_RENEWAL, HomeFeature.ACCIDENT_CARE, HomeFeature.CAR_TRADE_NEW, HomeFeature.CAR_TRADE_USED)
        CARPOS   -> listOf(HomeFeature.CARPOS_MANAGEMENT)
        FA_LIVE  -> listOf(HomeFeature.CUSTOMER_LIST, HomeFeature.FA_FEED, HomeFeature.FA_BENEFITS)
        FA_SUPER -> listOf(HomeFeature.CUSTOMER_LIST, HomeFeature.FA_FEED, HomeFeature.FA_BENEFITS, HomeFeature.INSURANCE_CONSULT_DASHBOARD)
        ADJUSTER -> listOf(HomeFeature.CUSTOMER_LIST, HomeFeature.FA_FEED, HomeFeature.FA_BENEFITS, HomeFeature.ADJUSTER_EDU_REQUEST)
    }

    companion object {
        fun from(
            isFa: Boolean,
            isLive: Boolean,
            isSuper: Boolean,
            isAdjuster: Boolean,
            carposSeq: Int,
        ): UserType = when {
            !isFa || !isLive -> if (carposSeq != -1) CARPOS else NORMAL
            isAdjuster       -> ADJUSTER
            isSuper          -> FA_SUPER
            else             -> FA_LIVE
        }

        fun fromString(value: String?): UserType =
            entries.firstOrNull { it.name == value } ?: NORMAL
    }
}