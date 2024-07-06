package ru.idfedorov09.kotbot.domain.entity

import jakarta.persistence.*
import ru.idfedorov09.kotbot.domain.dto.AuthDataDTO
import ru.idfedorov09.telegram.bot.base.domain.entity.BaseEntity
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity
import java.time.LocalDateTime

@Entity
@Table(name = "auth_data")
open class AuthDataEntity(
    @Id
    @Column(name = "user_id")
    open var id: Long? = null,

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    open var user: UserEntity = UserEntity(),

    @Column(name = "email")
    open var email: String? = null,

    @Column(name = "is_verified")
    open var isVerified: Boolean? = null,

    @Column(name = "temp_verify_code")
    open var tempVerifyCode: String? = null,

    @Column(name = "code_expiration_dttm")
    open var codeExpirationTime: LocalDateTime? = null,
): BaseEntity<AuthDataDTO>() {
    override fun toDTO() = AuthDataDTO(
        id = id,
        email = email,
        isVerified = isVerified ?: false,
        verifyCode = tempVerifyCode,
        codeExpiration = codeExpirationTime,
        user = user,
    )
}