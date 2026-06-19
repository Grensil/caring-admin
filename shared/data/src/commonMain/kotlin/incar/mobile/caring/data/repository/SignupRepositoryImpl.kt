package incar.mobile.caring.data.repository

import incar.mobile.caring.data.remote.SignupApiService
import incar.mobile.caring.domain.model.FAInfo
import incar.mobile.caring.domain.model.SignupDoneData
import incar.mobile.caring.domain.repository.SignupRepository

class SignupRepositoryImpl(
    private val signupApiService: SignupApiService,
) : SignupRepository {

    override suspend fun searchFA(searchWord: String): List<FAInfo> {
        val dto = signupApiService.searchFA(searchWord)
        return dto.faList.map { FAInfo(it.faName, it.faPhone, it.faOffice) }
    }

    override suspend fun updateFA(
        tokenKey: String,
        faName: String,
        faPhone: String,
        faOffice: String,
    ): Result<Unit> {
        val dto = signupApiService.updateFA(tokenKey, faName, faPhone, faOffice)
        return if (dto.result == "success") Result.success(Unit)
        else Result.failure(Exception(dto.errorMessage))
    }

    override suspend fun addFakeCar(
        tokenKey: String,
        ownerName: String,
        ownerPhone: String,
        ownerCarrier: String,
        ownerBirth: String,
        ownerGender: String,
    ): Result<Unit> {
        val dto = signupApiService.addFakeCar(tokenKey, ownerName, ownerPhone, ownerCarrier, ownerBirth, ownerGender)
        return if (dto.result == "success") Result.success(Unit)
        else Result.failure(Exception(dto.errorMessage))
    }

    override suspend fun getSignupDone(tokenKey: String): SignupDoneData {
        val dto = signupApiService.getSignupDone(tokenKey)
        return SignupDoneData(
            userName = dto.userName,
            userPhone = dto.userPhone,
            carNum = dto.carnum,
            carName = dto.car,
            fuelType = dto.oil,
            carCnt = dto.cnt,
            insureCompany = dto.insure,
            insureEndDate = dto.endDate,
            faName = dto.faName,
            faPhone = dto.faPhone,
        )
    }
}
