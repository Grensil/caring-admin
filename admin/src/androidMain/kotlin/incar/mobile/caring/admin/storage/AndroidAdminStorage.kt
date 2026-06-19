package incar.mobile.caring.admin.storage

import android.content.Context

class AndroidAdminStorage(private val context: Context) : AdminStorage {
    private val prefs by lazy {
        context.getSharedPreferences("caring_admin_prefs", Context.MODE_PRIVATE)
    }

    override fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    override fun read(key: String): String? = prefs.getString(key, null)

    override fun delete(key: String) {
        prefs.edit().remove(key).apply()
    }
}
