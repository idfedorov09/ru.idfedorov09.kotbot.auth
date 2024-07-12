package ru.idfedorov09.kotbot.domain.entity

import jakarta.persistence.*
import ru.idfedorov09.kotbot.domain.dto.AuthDataDTO
import ru.idfedorov09.telegram.bot.base.domain.entity.BaseEntity
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity

@Entity
@Table(name = "auth_data")
open class AuthDataEntity(
    @Id
    @Column(name = "user_id")
    open var id: Long? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @MapsId
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    open var user: UserEntity = UserEntity(),

    @Column(name = "email")
    open var email: String? = null,

    @Column(name = "is_verified", updatable = false)
    open var isVerified: Boolean = false,
): BaseEntity<AuthDataDTO>() {
    override fun toDTO() = AuthDataDTO(
        id = id,
        email = email,
        isVerified = isVerified,
        user = user,
    )
}