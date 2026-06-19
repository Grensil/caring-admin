package incar.mobile.caring.admin.storage

import platform.Foundation.NSUserDefaults

class IosAdminStorage : AdminStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    override fun save(key: String, value: String) {
        defaults.setObject(value, forKey = key)
        defaults.synchronize()
    }

    override fun read(key: String): String? = defaults.stringForKey(key)

    override fun delete(key: String) {
        defaults.removeObjectForKey(key)
        defaults.synchronize()
    }
}
