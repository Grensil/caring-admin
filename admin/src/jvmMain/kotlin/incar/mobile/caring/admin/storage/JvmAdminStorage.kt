package incar.mobile.caring.admin.storage

import java.util.prefs.Preferences

class JvmAdminStorage : AdminStorage {
    private val prefs: Preferences = Preferences.userRoot().node("incar/caring-admin")
    override fun save(key: String, value: String) { prefs.put(key, value); prefs.flush() }
    override fun read(key: String): String? = prefs.get(key, null).takeIf { it?.isNotEmpty() == true }
    override fun delete(key: String) { prefs.remove(key); prefs.flush() }
}
