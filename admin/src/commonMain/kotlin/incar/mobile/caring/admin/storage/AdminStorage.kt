package incar.mobile.caring.admin.storage

interface AdminStorage {
    fun save(key: String, value: String)
    fun read(key: String): String?
    fun delete(key: String)
}
