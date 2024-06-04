package ru.idfedorov09.kotbot.fetcher

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.idfedorov09.telegram.bot.base.domain.annotation.Command
import ru.idfedorov09.telegram.bot.base.domain.annotation.InputText
import ru.idfedorov09.telegram.bot.base.domain.service.MessageSenderService
import ru.idfedorov09.telegram.bot.base.domain.service.UserService
import ru.idfedorov09.telegram.bot.base.fetchers.DefaultFetcher
import ru.idfedorov09.telegram.bot.base.util.MessageParams
import ru.idfedorov09.telegram.bot.base.util.UpdatesUtil
import ru.mephi.sno.libs.flow.belly.InjectData

@Component
class EmailAuthFetcher(
    private val userService: UserService,
    private val messageSenderService: MessageSenderService,
    private val updatesUtil: UpdatesUtil,
): DefaultFetcher() {

    @InjectData
    fun doFetch(){}

    @Command("/start")
    fun startCommand(update: Update) {
        val chatId = updatesUtil.getChatId(update) ?: return
        // TODO: not registered
        userService.findNotDeletedByTui(chatId)?.let { return }

        messageSenderService.sendMessage(
            MessageParams(
                chatId = chatId,
                text = "Для использования бота необходимо авторизироваться. Введите вашу корпоративную почту."
            )
        )
        // TODO: save last user action type
    }

    // TODO: ENTER_EMAIL.type
    @InputText("ENTER_CORP_EMAIL")
    fun enterEmail(update: Update) {
        // TODO: проверка email'а, fail -> сообщение об ошибке, LUAT -> default
        // TODO: генерация кода и отправка письма
        // TODO: сообщение об этом коде, перевод LUAT если все ок
    }
}