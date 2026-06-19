package incar.mobile.caring.data.service

import incar.mobile.caring.domain.model.PushMessage
import incar.mobile.caring.domain.service.NotificationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class NotificationServiceImpl : NotificationService {

    private var cachedToken: String? = null

    private val _messages = MutableSharedFlow<PushMessage>(replay = 0)
    override val messages: Flow<PushMessage> = _messages.asSharedFlow()

    override suspend fun getToken(): String? = cachedToken

    override fun notifyTokenRefresh(token: String) {
        cachedToken = token
    }

    override fun notifyMessageReceived(message: PushMessage) {
        _messages.tryEmit(message)
    }
}
