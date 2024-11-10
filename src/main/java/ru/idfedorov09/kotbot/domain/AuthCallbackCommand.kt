package ru.idfedorov09.kotbot.domain

import ru.idfedorov09.telegram.bot.base.config.registry.CallbackCommand

object AuthCallbackCommand {
    val AUTH_RESEND_CODE = CallbackCommand(
        command = "resend_confirm_code",
        description = "Отправить код подтверждения заново",
    )
}