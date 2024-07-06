package ru.idfedorov09.kotbot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import ru.idfedorov09.kotbot.domain.entity.AuthDataEntity

interface AuthDataRepository<T: AuthDataEntity> : JpaRepository<T, Long> {

    @Query(
        """
            SELECT *
            FROM auth_data
            WHERE 1 = 1
                and user_id = :userId
            LIMIT 1
        """,
        nativeQuery = true,
    )
    fun getAuthData(userId: Long): T?
}