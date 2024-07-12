package ru.idfedorov09.kotbot.domain.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.idfedorov09.kotbot.domain.dto.AuthDataDTO
import ru.idfedorov09.kotbot.domain.entity.AuthDataEntity
import ru.idfedorov09.kotbot.repository.AuthDataRepository
import ru.idfedorov09.telegram.bot.base.domain.dto.UserDTO
import ru.idfedorov09.telegram.bot.base.domain.entity.UserEntity

@Service
class AuthDataService {

    @Autowired
    private lateinit var authDataRepository: AuthDataRepository<AuthDataEntity>

    fun UserEntity.getAuthData() = this.id?.let { authDataRepository.getAuthData(it)?.toDTO() }

    fun UserDTO.getAuthData() = this.id?.let { authDataRepository.getAuthData(it) }

    open fun save(data: AuthDataEntity): AuthDataEntity = authDataRepository.save(data)

    open fun save(data: AuthDataDTO): AuthDataDTO = save(data.toEntity()).toDTO()

    open fun updateVerified(userDTO: UserDTO, isVerified: Boolean): Int {
        userDTO.id ?: return 0
        return authDataRepository.updateIsVerified(userDTO.id!!, isVerified)
    }
}