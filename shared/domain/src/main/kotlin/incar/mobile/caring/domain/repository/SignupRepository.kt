package incar.mobile.caring.domain.repository

import incar.mobile.caring.domain.model.FAInfo
import incar.mobile.caring.domain.model.SignupDoneData

interface SignupRepository {
    suspend fun searchFA(searchWord: String): List<FAInfo>
    suspend fun updateFA(tokenKey: String, faName: String, faPhone: String, faOffice: String): Result<Unit>
    suspend fun addFakeCar(
        tokenKey: String,
        ownerName: String,
        ownerPhone: String,
        ownerCarrier: String,
        ownerBirth: String,
        ownerGender: String,
    ): Result<Unit>
    suspend fun getSignupDone(tokenKey: String): SignupDoneData
}
