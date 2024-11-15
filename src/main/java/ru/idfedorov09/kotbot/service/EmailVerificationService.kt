package ru.idfedorov09.kotbot.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.idfedorov09.telegram.bot.base.domain.service.EmailService
import kotlin.random.Random

@Service
class EmailVerificationService(
    private val emailService: EmailService,
) {

    companion object {
        val log = LoggerFactory.getLogger(EmailVerificationService::class.java)
    }
    /**
     * Отправляет письмо с кодом подтверждения. Возвращает код подтверждения
     */
    fun sendVerificationEmail(toEmail: String): String? {
        val code = generateVerifyCode()
        val subject = "Подтверждение входа в бота Telegram"
        runCatching {
            emailService.sendEmail(
                toEmail,
                subject,
                EmailService.buildBody("Verification", mapOf("code" to code)),
            )
        }.onFailure {
            log.error("Error sending verification email", it)
            return null
        }
        return code
    }

    private fun generateVerifyCode(): String {
        val code = Random.nextInt(100000, 999999).toString()
        return code
    }
}