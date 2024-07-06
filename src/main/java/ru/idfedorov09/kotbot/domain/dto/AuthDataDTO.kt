package ru.idfedorov09.kotbot.domain.dto

import ru.idfedorov09.kotbot.domain.entity.AuthDataEntity
import ru.idfedorov09.telegram.bot.base.domain.dto.BaseDTO
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

data class AuthDataDTO(
    val id: Long? = null,
    val email: String? = null,
    val isVerified: Boolean = false,
    private val verifyCode: String? = null,
    private val codeExpiration: LocalDateTime? = null,
    private val user: UserEntity
) : BaseDTO<AuthDataEntity>() {

    fun getCode(): String? {
        if (verifyCode == null || codeExpiration == null) return null

        val zone = ZoneId.of("Europe/Moscow")
        val time = LocalDateTime.now(zone)
        val codeExpTime = ZonedDateTime.of(codeExpiration, zone).toLocalDateTime()

        if (codeExpTime.isAfter(time)) return null
        return verifyCode
    }

    override fun toEntity() = AuthDataEntity(
        id = id,
        email = email,
        isVerified = isVerified,
        tempVerifyCode = verifyCode,
        codeExpirationTime = codeExpiration,
        user = user,
    )
}