package incar.mobile.caring.domain.repository

import incar.mobile.caring.domain.model.UserType

interface UserSessionRepository {
    fun getHashValue(): String

    fun setHashValue(value: String)

    fun getUserIdx(): String

    fun setUserIdx(value: String)

    fun getPgValue(): String

    fun setPgValue(value: String)

    fun getUserName(): String

    fun setUserName(value: String)

    fun getUserPhone(): String

    fun setUserPhone(value: String)

    fun getUserBirth(): String

    fun setUserBirth(value: String)

    fun getUserGender(): String

    fun setUserGender(value: String)

    fun getUserCarrier(): String

    fun setUserCarrier(value: String)

    fun getPushId(): String

    fun setPushId(value: String)

    fun getApiServer(): String // "real" or "test"

    fun setApiServer(value: String)

    fun getUserType(): UserType

    fun setUserType(value: UserType)

    fun getCarposSeq(): Int

    fun setCarposSeq(value: Int)

    fun clearAuthData()
}
