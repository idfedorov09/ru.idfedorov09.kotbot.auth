package ru.idfedorov09.kotbot.fetcher

import jakarta.mail.internet.AddressException
import jakarta.mail.internet.InternetAddress
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import ru.idfedorov09.kotbot.domain.AuthLastUserActionType
import ru.idfedorov09.kotbot.domain.dto.AuthDataDTO
import ru.idfedorov09.kotbot.domain.service.AuthDataService
import ru.idfedorov09.kotbot.domain.service.AuthRedisService
import ru.idfedorov09.kotbot.service.EmailVerificationService
import ru.idfedorov09.telegram.bot.base.domain.LastUserActionTypes
import ru.idfedorov09.telegram.bot.base.domain.annotation.Callback
import ru.idfedorov09.telegram.bot.base.domain.annotation.Command
import ru.idfedorov09.telegram.bot.base.domain.annotation.InputText
import ru.idfedorov09.telegram.bot.base.domain.dto.CallbackDataDTO
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

    // TODO: ради избежания дублирования кода что-то придумать, думаю можно с генераторами кода (кста найс идея)
    companion object {
        const val AUTH_RESEND_CODE = "resend_confirm_code"
        const val AUTH_CHANGE_EMAIL = "auth_change_email"
    }

    @InjectData
    fun doFetch(){}

    @Command("/start")
    fun startCommand(
        update: Update,
        user: UserDTO,
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

    @Callback(AUTH_CHANGE_EMAIL)
    fun changeEmail(
        update: Update,
        user: UserDTO,
        authData: AuthDataDTO,

    ) {
        deleteUpdateMessage()
        if (authData.isVerified)
            return

        val chatId = updatesUtil.getChatId(update) ?: return
        messageSenderService.sendMessage(
            MessageParams(
                chatId = chatId,
                text = "Введите вашу корпоративную почту.",
            )
        )
        user.lastUserActionType = AuthLastUserActionType.ENTER_EMAIL
    }

    // TODO: ограничения на попытки
    @InputText("ENTER_CORP_EMAIL")
    @Callback(AUTH_RESEND_CODE)
    fun enterEmail(
        update: Update,
        user: UserDTO,
        authData: AuthDataDTO,
    ) {
        if (authData.isVerified)
            return

        val email = update.message?.text?.trim() ?: authData.email!!

        if (!isValidEmail(email)) {
            messageSenderService.sendMessage(
                MessageParams(
                    chatId = updatesUtil.getChatId(update)!!,
                    text = "Некорректный email.",
                )
            )
            return
        }

        val code = emailVerificationService
            .sendVerificationEmail(email)
            ?: run {
                messageSenderService.sendMessage(
                    MessageParams(
                        chatId = updatesUtil.getChatId(update)!!,
                        text = "Произошла ошибка при отправке сообщения. Попробуйте ввести почту еще раз.",
                    )
                )
                return
            }
        // TODO: duration в настройки
        authRedisService.setVerifyCode(user.tui!!.toLong(), code, 50)
        user.lastUserActionType = AuthLastUserActionType.ENTER_VERIFY_CODE
        authDataService.save(
            AuthDataDTO(
                email = email,
                isVerified = false,
                user = user,
            )
        )

        if (update.hasCallbackQuery())
            deleteUpdateMessage()

        val keyboard =
            listOf(
                listOf(
                    CallbackDataDTO(
                        callbackData = AUTH_RESEND_CODE,
                        metaText = "Выслать повторно",
                    ).save().createKeyboard()
                ),
                listOf(
                    CallbackDataDTO(
                        callbackData = AUTH_CHANGE_EMAIL,
                        metaText = "Изменить почту",
                    ).save().createKeyboard()
                ),
            )
        messageSenderService.sendMessage(
            MessageParams(
                chatId = updatesUtil.getChatId(update)!!,
                text = "Введите код подтверждения, отправленный на почту.",
                replyMarkup = createKeyboard(keyboard)
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

    private fun isValidEmail(email: String): Boolean {
        try {
            InternetAddress(email).validate()
            return true
        } catch (e: AddressException) {
            return false
        }
    }
}