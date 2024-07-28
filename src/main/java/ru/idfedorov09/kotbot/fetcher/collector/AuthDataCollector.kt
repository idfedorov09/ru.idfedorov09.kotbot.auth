package ru.idfedorov09.kotbot.fetcher.collector

import org.springframework.stereotype.Component
import ru.idfedorov09.kotbot.domain.dto.AuthDataDTO
import ru.idfedorov09.kotbot.domain.service.AuthDataService
import ru.idfedorov09.telegram.bot.base.domain.dto.UserDTO
import ru.idfedorov09.telegram.bot.base.fetchers.DefaultFetcher
import ru.mephi.sno.libs.flow.belly.InjectData

@Component
class AuthDataCollector(
    private val authDataService: AuthDataService,
) : DefaultFetcher() {

    @InjectData
    fun doFetch(
        user: UserDTO?,
    ): AuthDataDTO? {
        val userId = user?.id ?: return null
        return authDataService
            .getAuthDataByUserId(userId)
            ?: AuthDataDTO(user = user)
    }
}