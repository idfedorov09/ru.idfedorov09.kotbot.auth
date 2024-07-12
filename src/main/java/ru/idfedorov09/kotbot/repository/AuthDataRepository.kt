package ru.idfedorov09.kotbot.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.transaction.annotation.Isolation
import org.springframework.transaction.annotation.Transactional
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

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    @Modifying
    @Query(
        """
            UPDATE auth_data
            SET is_verified = :isVerified
            WHERE user_id = :userId
        """,
        nativeQuery = true,
    )
    fun updateIsVerified(userId: Long, isVerified: Boolean): Int
}