package incar.mobile.caring.data.repository

import incar.mobile.caring.domain.model.UserType
import incar.mobile.caring.domain.repository.UserSessionRepository
import incar.mobile.caring.domain.service.SecureStorage

class UserSessionRepositoryImpl(
    private val secureStorage: SecureStorage,
) : UserSessionRepository {

    private fun getString(key: String) = secureStorage.read(key) ?: ""
    private fun setString(key: String, value: String) = secureStorage.save(key, value)

    override fun getHashValue() = getString(KEY_HASH_VALUE)
    override fun setHashValue(value: String) = setString(KEY_HASH_VALUE, value)

    override fun getUserIdx() = getString(KEY_USER_IDX)
    override fun setUserIdx(value: String) = setString(KEY_USER_IDX, value)

    override fun getPgValue() = getString(KEY_PG_VALUE)
    override fun setPgValue(value: String) = setString(KEY_PG_VALUE, value)

    override fun getUserName() = getString(KEY_USER_NAME)
    override fun setUserName(value: String) = setString(KEY_USER_NAME, value)

    override fun getUserPhone() = getString(KEY_USER_PHONE)
    override fun setUserPhone(value: String) = setString(KEY_USER_PHONE, value)

    override fun getUserBirth() = getString(KEY_USER_BIRTH)
    override fun setUserBirth(value: String) = setString(KEY_USER_BIRTH, value)

    override fun getUserGender() = getString(KEY_USER_GENDER)
    override fun setUserGender(value: String) = setString(KEY_USER_GENDER, value)

    override fun getUserCarrier() = getString(KEY_USER_CARRIER)
    override fun setUserCarrier(value: String) = setString(KEY_USER_CARRIER, value)

    override fun getPushId() = getString(KEY_PUSH_ID)
    override fun setPushId(value: String) = setString(KEY_PUSH_ID, value)

    override fun getApiServer() = secureStorage.read(KEY_API_SERVER) ?: "real"
    override fun setApiServer(value: String) = setString(KEY_API_SERVER, value)

    override fun getUserType(): UserType =
        UserType.fromString(secureStorage.read(KEY_USER_TYPE))

    override fun setUserType(value: UserType) =
        setString(KEY_USER_TYPE, value.name)

    override fun getCarposSeq(): Int =
        secureStorage.read(KEY_CARPOS_SEQ)?.toIntOrNull() ?: -1

    override fun setCarposSeq(value: Int) =
        setString(KEY_CARPOS_SEQ, value.toString())

    override fun clearAuthData() {
        secureStorage.delete(KEY_HASH_VALUE)
        secureStorage.delete(KEY_USER_IDX)
        secureStorage.delete(KEY_PG_VALUE)
        secureStorage.delete(KEY_USER_NAME)
        secureStorage.delete(KEY_USER_PHONE)
        secureStorage.delete(KEY_USER_BIRTH)
        secureStorage.delete(KEY_USER_GENDER)
        secureStorage.delete(KEY_USER_CARRIER)
        secureStorage.delete(KEY_PUSH_ID)
        secureStorage.delete(KEY_USER_TYPE)
        secureStorage.delete(KEY_CARPOS_SEQ)
    }

    companion object {
        const val KEY_HASH_VALUE = "hash_value"
        const val KEY_USER_IDX = "user_idx"
        const val KEY_PG_VALUE = "pg_value"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_PHONE = "user_phone"
        const val KEY_USER_BIRTH = "user_birth"
        const val KEY_USER_GENDER = "user_gender"
        const val KEY_USER_CARRIER = "user_carrier"
        const val KEY_PUSH_ID = "pushid"
        const val KEY_API_SERVER = "api_server"
        const val KEY_USER_TYPE  = "user_type"
        const val KEY_CARPOS_SEQ = "carpos_seq"
    }
}
