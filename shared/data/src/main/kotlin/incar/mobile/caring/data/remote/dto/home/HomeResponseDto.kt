package incar.mobile.caring.data.remote.dto.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HomeResponseDto(
    val result: String,
    @SerialName("error_message") val errorMessage: String? = null,
    // 차량 정보
    @SerialName("car_num") val carNum: String? = null,
    @SerialName("car_name") val carName: String? = null,
    @SerialName("car_price") val carPrice: Long? = null,
    // FA 담당자
    @SerialName("fa_name") val faName: String? = null,
    @SerialName("fa_phone") val faPhone: String? = null,
    // 보험
    @SerialName("is_insur_expiry") val isInsurExpiry: String? = null,  // "Y"/"N"
    @SerialName("insur_exp_csv") val insurExpCsv: String? = null,
    // 공지
    @SerialName("is_alert") val isAlert: String? = null,               // "Y"/"N"
    @SerialName("title_alert") val titleAlert: String? = null,
    @SerialName("cont_alert") val contAlert: String? = null,
    // FA 전용
    @SerialName("is_fa") val isFa: String? = null,                     // "t"/"f"
    @SerialName("user_cnt") val userCnt: Int? = null,
    @SerialName("expiry_car_cnt") val expiryCarCnt: Int? = null,
    // 서비스 활성화 여부
    @SerialName("insur_yn") val insurYn: String? = null,
    @SerialName("carpos_yn") val carposYn: String? = null,
    @SerialName("juyuso_yn") val juyusoYn: String? = null,
    @SerialName("charge_yn") val chargeYn: String? = null,
    @SerialName("carsuri_yn") val carsuriYn: String? = null,
    @SerialName("newcar_yn") val newcarYn: String? = null,
    @SerialName("junggo_yn") val junggoYn: String? = null,
    @SerialName("tire_yn") val tireYn: String? = null,
    @SerialName("carpos_map_yn") val carposMapYn: String? = null,
    @SerialName("allim_yn") val allimYn: String? = null,
)
