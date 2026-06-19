package incar.mobile.caring.admin.model

data class Adjuster(
    val id: Int,
    val name: String,
    val company: String,
    val phone: String,
    val address: String,
    val careerYears: Int,
    val reviewScore: Double?,
    val reviewCount: Int,
    val isVisible: Boolean,
)
