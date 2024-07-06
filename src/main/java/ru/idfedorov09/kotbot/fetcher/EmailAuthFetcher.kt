package ru.idfedorov09.kotbot.fetcher

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.idfedorov09.kotbot.domain.AuthLastUserActionType
import ru.idfedorov09.telegram.bot.base.domain.annotation.Command
import ru.idfedorov09.telegram.bot.base.domain.annotation.InputText
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity
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

    companion object {
        val log = LoggerFactory.getLogger(EmailAuthFetcher::class.java)!!
    }

    @InjectData
    fun doFetch(){}

    @Command("/start")
    fun startCommand(update: Update) {
        val chatId = updatesUtil.getChatId(update) ?: return
        val userId = updatesUtil.getUserId(update) ?: return

        userService.findNotDeletedByTui(chatId)?.let {
            // TODO
            return
        }

        messageSenderService.sendMessage(
            MessageParams(
                chatId = chatId,
                text = "Для использования бота необходимо авторизироваться. Введите вашу корпоративную почту."
            )
        )
        userService.save(
            UserEntity(
                tui = userId,
                lastUserActionType = AuthLastUserActionType.ENTER_EMAIL,
            )
        )
    }

    // TODO: ENTER_EMAIL.type
    @InputText("ENTER_CORP_EMAIL")
    fun enterEmail(update: Update) {
        // TODO: проверка email'а, fail -> сообщение об ошибке, LUAT -> default
        // TODO: генерация кода и отправка письма
        // TODO: сообщение об этом коде, перевод LUAT если все ок
        log.info("Ok, enter email")
    }
}