package incar.mobile.caring.domain.service

interface PermissionService {
    suspend fun requestNotificationPermission(): Boolean
}
