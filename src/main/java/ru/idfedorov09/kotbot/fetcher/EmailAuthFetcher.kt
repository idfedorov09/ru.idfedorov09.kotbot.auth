package ru.idfedorov09.kotbot.fetcher

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.idfedorov09.kotbot.domain.AuthLastUserActionType
import ru.idfedorov09.kotbot.domain.dto.AuthDataDTO
import ru.idfedorov09.kotbot.domain.service.AuthDataService
import ru.idfedorov09.kotbot.domain.service.AuthRedisService
import ru.idfedorov09.kotbot.service.EmailVerificationService
import ru.idfedorov09.telegram.bot.base.domain.LastUserActionTypes
import ru.idfedorov09.telegram.bot.base.domain.annotation.Command
import ru.idfedorov09.telegram.bot.base.domain.annotation.InputText
import ru.idfedorov09.telegram.bot.base.domain.dto.UserDTO
import ru.idfedorov09.telegram.bot.base.domain.service.MessageSenderService
import ru.idfedorov09.telegram.bot.base.fetchers.DefaultFetcher
import ru.idfedorov09.telegram.bot.base.util.MessageParams
import ru.idfedorov09.telegram.bot.base.util.UpdatesUtil
import ru.mephi.sno.libs.flow.belly.InjectData

@Component
class EmailAuthFetcher(
    private val messageSenderService: MessageSenderService,
    private val updatesUtil: UpdatesUtil,
    private val emailVerificationService: EmailVerificationService,
    private val authRedisService: AuthRedisService,
    private val authDataService: AuthDataService,
): DefaultFetcher() {

    private lateinit var domains: String

    @InjectData
    fun doFetch(){}

    @Command("/start")
    fun startCommand(
        update: Update,
        user: UserDTO,
        authData: AuthDataDTO,
    ) {
        val chatId = updatesUtil.getChatId(update) ?: return

        messageSenderService.sendMessage(
            MessageParams(
                chatId = chatId,
                text = "Для использования бота необходимо авторизироваться. Введите вашу корпоративную почту."
            )
        )
        user.lastUserActionType = AuthLastUserActionType.ENTER_EMAIL
    }

    @InputText("ENTER_CORP_EMAIL")
    fun enterEmail(
        update: Update,
        user: UserDTO
    ) {
        val email = update.message.text.trim()
        if (!isValidEmail(email)) {
            messageSenderService.sendMessage(
                MessageParams(
                    chatId = updatesUtil.getChatId(update)!!,
                    text = "Некорректный email.",
                )
            )
            return
        }

        val code = emailVerificationService.sendVerificationEmail(email)
        authRedisService.setVerifyCode(user.tui!!.toLong(), code, 50)
        user.lastUserActionType = AuthLastUserActionType.ENTER_VERIFY_CODE
        authDataService.save(
            AuthDataDTO(
                email = email,
                isVerified = false,
                user = user,
            )
        )
        messageSenderService.sendMessage(
            MessageParams(
                chatId = updatesUtil.getChatId(update)!!,
                text = "Введите код подтверждения, отправленный на почту.",
            )
        )
    }

    @InputText("ENTER_VERIFY_CODE")
    fun enterVerifyCode(
        update: Update,
        user: UserDTO,
    ) {
        val text = update.message.text.trim()
        if (authRedisService.getVerifyCode(user.tui!!.toLong()) != text) {
            messageSenderService.sendMessage(
                MessageParams(
                    chatId = updatesUtil.getChatId(update)!!,
                    text = "Повторите попытку.",
                )
            )
            return
        }

        user.lastUserActionType = LastUserActionTypes.DEFAULT

        authDataService.updateVerified(user, true)
        messageSenderService.sendMessage(
            MessageParams(
                chatId = updatesUtil.getChatId(update)!!,
                text = "Вы успешно авторизировались.",
            )
        )
    }

    fun setDomains(domainList: List<String>) {
        domains = domainList
            .joinToString(separator = "|") { it.replace(".", "\\.") }
    }

    private fun isValidEmail(text: String): Boolean {
        val emailRegex = "^[A-Za-z0-9.+_-]+@($domains)$"
        return text.matches(emailRegex.toRegex())
    }
}