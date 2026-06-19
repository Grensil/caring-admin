package incar.mobile.caring.domain.service

interface SecureStorage {
    fun save(key: String, value: String)
    fun read(key: String): String?
    fun delete(key: String)
    fun clear()
}

object SecureStorageKeys {
    const val HASH_VALUE  = "hash_value"
    const val USER_IDX    = "user_idx"
    const val PG_VALUE    = "pg_value"
    const val USER_NAME   = "user_name"
    const val USER_PHONE  = "user_phone"
    const val USER_BIRTH  = "user_birth"
    const val USER_GENDER = "user_gender"
    const val USER_CARRIER = "user_carrier"
    const val PUSH_ID     = "pushid"
    const val API_SERVER  = "api_server"
    const val USER_TYPE   = "user_type"    // UserType.name 저장
    const val CARPOS_SEQ  = "carpos_seq"   // 카포스 점주 seq (-1 = 미등록)
}
