package incar.mobile.caring.domain.service

import incar.mobile.caring.domain.model.PushMessage
import kotlinx.coroutines.flow.Flow

interface NotificationService {
    suspend fun getToken(): String?
    val messages: Flow<PushMessage>
    fun notifyTokenRefresh(token: String)
    fun notifyMessageReceived(message: PushMessage)
}
