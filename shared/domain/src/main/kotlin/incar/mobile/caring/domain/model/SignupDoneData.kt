package incar.mobile.caring.domain.model

data class SignupDoneData(
    val userName: String,
    val userPhone: String,
    val carNum: String,
    val carName: String,
    val fuelType: String,
    val carCnt: Int,
    val insureCompany: String,
    val insureEndDate: String,
    val faName: String,
    val faPhone: String,
)
