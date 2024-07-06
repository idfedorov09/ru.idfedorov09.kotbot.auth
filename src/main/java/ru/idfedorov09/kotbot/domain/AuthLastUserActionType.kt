package ru.idfedorov09.kotbot.domain

import ru.idfedorov09.telegram.bot.base.config.registry.LastUserActionType

object AuthLastUserActionType {
    val ENTER_EMAIL = LastUserActionType(
        "ENTER_CORP_EMAIL",
        "Ввод корпоративной почты для аутентификации"
    )
}