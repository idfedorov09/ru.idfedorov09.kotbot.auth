package ru.idfedorov09.kotbot.domain.dto

import ru.idfedorov09.kotbot.domain.entity.AuthDataEntity
import ru.idfedorov09.telegram.bot.base.domain.dto.BaseDTO
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity

data class AuthDataDTO(
    val id: Long? = null,
    val email: String? = null,
    val isVerified: Boolean = false,
    private val user: UserEntity
) : BaseDTO<AuthDataEntity>() {

    override fun toEntity() = AuthDataEntity(
        id = id,
        email = email,
        isVerified = isVerified,
        user = user,
    )
}