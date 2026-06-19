package incar.mobile.caring.data.repository

import incar.mobile.caring.data.remote.HomeApiService
import incar.mobile.caring.domain.model.HomeAlert
import incar.mobile.caring.domain.model.HomeData
import incar.mobile.caring.domain.model.HomeServiceFlags
import incar.mobile.caring.domain.repository.HomeRepository

class HomeRepositoryImpl(
    private val homeApiService: HomeApiService,
) : HomeRepository {

    override suspend fun getHomeData(tokenKey: String): HomeData {
        val dto = homeApiService.getTab1Main(tokenKey)

        if (dto.result != "success") {
            throw IllegalStateException(dto.errorMessage ?: "홈 데이터 조회 실패")
        }

        return HomeData(
            carNum         = dto.carNum ?: "",
            carName        = dto.carName ?: "",
            carPrice       = dto.carPrice ?: 0L,
            insureExpiryDate = dto.insurExpCsv?.split(",")?.firstOrNull() ?: "",
            isInsureExpiry  = dto.isInsurExpiry == "Y",
            faName         = dto.faName ?: "",
            faPhone        = dto.faPhone ?: "",
            alert          = if (dto.isAlert == "Y" && dto.titleAlert != null) {
                HomeAlert(title = dto.titleAlert, content = dto.contAlert ?: "")
            } else null,
            services = HomeServiceFlags(
                insureYn     = dto.insurYn == "Y",
                carposYn    = dto.carposYn == "Y",
                juyusoYn    = dto.juyusoYn == "Y",
                chargeYn    = dto.chargeYn == "Y",
                carsuriYn   = dto.carsuriYn == "Y",
                newcarYn    = dto.newcarYn == "Y",
                junggoYn    = dto.junggoYn == "Y",
                tireYn      = dto.tireYn == "Y",
                carposMapYn = dto.carposMapYn == "Y",
                allimYn     = dto.allimYn == "Y",
            ),
            customerCnt  = dto.userCnt ?: 0,
            expiryCarCnt = dto.expiryCarCnt ?: 0,
        )
    }
}
