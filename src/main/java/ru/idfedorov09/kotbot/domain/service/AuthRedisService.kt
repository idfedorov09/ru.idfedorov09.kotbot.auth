package ru.idfedorov09.kotbot.domain.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import redis.clients.jedis.Jedis
import ru.idfedorov09.telegram.bot.base.domain.service.RedisService
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@Service
class AuthRedisService(
    jedis: Jedis,
): RedisService(jedis) {

    private companion object {
        const val VERIFY_CODE_SUFFIX = "vrfcd"
        const val CODE_EXPIRATION_SUFFIX = "cdxpr"
        val ZONE: ZoneId = ZoneId.of("Europe/Moscow")
        val log: Logger = LoggerFactory.getLogger(AuthRedisService::class.java)
    }

    /**
     * Возвращает код подтверждения для заданного юзера, если время действия не вышло
     */
    fun getVerifyCode(userId: Long): String? {
        val key = "${userId}_$VERIFY_CODE_SUFFIX"
        val currentTime = LocalDateTime.now(ZONE)
        val codeExpTime = getCodeExpiration(userId)
        if (codeExpTime.isBefore(currentTime)) {
            del(key)
            log.info("Verification code for user $userId has expired and has been deleted.")
            return null
        }
        return getSafe(key)
    }

    private fun getCodeExpiration(userId: Long): LocalDateTime {
        val expInSec = getSafe("${userId}_$CODE_EXPIRATION_SUFFIX")?.toLong() ?: 0

        return LocalDateTime.ofEpochSecond(expInSec, 0, ZoneOffset.UTC)
            .atZone(ZONE)
            .toLocalDateTime()
    }

    /**
     * Сетает код подтверждения
     */
    private fun setVerifyCode(userId: Long, code: String) {
        val key = "${userId}_$VERIFY_CODE_SUFFIX"
        setValue(key, code)
    }

    /**
     * Сетает дату когда код перестанет быть валидным
     * duration - длительность работы ключа в секундах
     */
    private fun setCodeExpiration(userId: Long, duration: Long) {
        val key = "${userId}_$CODE_EXPIRATION_SUFFIX"
        val now = LocalDateTime.now(ZONE)
        val expTime = now.toEpochSecond(ZoneOffset.UTC) + duration
        setValue(key, expTime.toString())
    }

    /**
     * Назначает код подтверждения и длительность duration (в секундах)
     */
    fun setVerifyCode(userId: Long, code: String, duration: Long) {
        setCodeExpiration(userId, duration)
        setVerifyCode(userId, code)
    }
}