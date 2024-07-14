package ru.idfedorov09.kotbot.fetcher.collector

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.idfedorov09.kotbot.domain.dto.AuthDataDTO
import ru.idfedorov09.kotbot.domain.service.AuthDataService
import ru.idfedorov09.telegram.bot.base.fetchers.DefaultFetcher
import ru.idfedorov09.telegram.bot.base.util.UpdatesUtil
import ru.mephi.sno.libs.flow.belly.InjectData

@Component
class AuthDataCollector(
    private val authDataService: AuthDataService,
    private val updatesUtil: UpdatesUtil,
) : DefaultFetcher() {

    @InjectData
    fun doFetch(
        update: Update,
    ): AuthDataDTO? {
        val userId = updatesUtil.getUserId(update) ?: return null
        return authDataService.getAuthDataByUserId(userId.toLong())
    }
}