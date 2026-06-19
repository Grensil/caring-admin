package incar.mobile.caring.admin.storage

import kotlinx.browser.localStorage

class WebAdminStorage : AdminStorage {
    override fun save(key: String, value: String) { localStorage.setItem(key, value) }
    override fun read(key: String): String? = localStorage.getItem(key)
    override fun delete(key: String) { localStorage.removeItem(key) }
}
